# ⚡ How Velocity Render Works: Anisotropic Velocity Prioritization

> **"Render what's ahead, not what's behind."**

---

## 🎯 The Core Concept

Vanilla Minecraft generates and meshes chunks in a uniform circular radius centered on the player. Whether you are standing still or flying at 40 m/s with an Elytra, vanilla allocates equal CPU time to terrain behind your back as it does to terrain in front of your eyes.

**Velocity Render** transforms this circular model into **dynamic, velocity-biased anisotropic prioritization**. As you accelerate, the game reorients its generation and meshing resources into a forward-facing vector cone.

---

## 📊 The Velocity Cone Progression

![Velocity Cone Progression](Doc/Media/velocity_cone_progression.png)

```
       Stage 1                   Stage 2                  Stage 3                  Stage 4
   [Standing Still]             [Walking]               [Sprinting]             [Elytra Flight]

         ┌─┐                      ┌─┐                      ┌─┐                      \  /
       ┌─┘ └─┐                  ┌─┘ └─┐                  ┌─┘ └─┐                     \/
      │   ●   │                │ \ ● / │                │ \ ● / │                    ││
       └─┐ ┌─┘                  └─┐ ┌─┘                  \ ┌─┐ /                     ││
         └─┘                      \ /                     \   /                      \●/
                                   V                        V                         V
     Vanilla Circle             Wide V                   Medium V                  Tight V
    Uniform Radius         Low Forward Bias        Balanced Priority           Max Forward Reach
```

---

## 🔬 How Each Stage Works

### Stage 1: Vanilla Circle (`Speed < 0.20 b/t` / Standing or Idling)
- **Behavior**: Pure vanilla behavior.
- **Client Meshing**: Standard Euclidean distance ($\text{dist}^2 = \Delta x^2 + \Delta y^2 + \Delta z^2$). Chunks in all directions share equal compile priority.
- **Server Tickets**: Zero forward tickets issued. Only vanilla chunk loading is active.

### Stage 2: Wide V (`Speed ~ 0.20 – 0.50 b/t` / Walking & Trotting)
- **Behavior**: Forward bias activates subtly.
- **Client Meshing**: Chunks directly ahead receive a small negative distance bias, pushing them slightly ahead of side and rear chunks in the render builder queue.
- **Server Tickets**: 1 to 4 forward chunks requested along the travel vector.

### Stage 3: Medium V (`Speed ~ 0.50 – 1.20 b/t` / Sprinting, Riding Horses, Minecarts)
- **Behavior**: Prioritization cone narrows and extends forward.
- **Client Meshing**: Forward chunks cut in front of side chunks in the compile queue. Chunks behind the player receive positive distance penalties, deprioritizing them so render threads focus purely on your path.
- **Server Tickets**: 4 to 8 forward chunks requested. Chunks left behind are immediately pruned.

### Stage 4: Tight High-Speed V (`Speed > 1.20 b/t` / Elytra Dives & Blue Ice Highways)
- **Behavior**: Maximum forward reach and narrowest lookahead corridor.
- **Client Meshing**: Forward chunks up to 16 chunks (256 meters) ahead are treated as if they were right next to the player, eliminating chunk pop-in and void stalls at high speed. Chunks behind the player are effectively ignored by the meshing thread until speed decreases.
- **Server Tickets**: Full dynamic corridor generation (up to 16 chunks in the Overworld/End, clamped to 60% / 10 chunks in the Nether).

---

## 🔄 Dynamic Banked Turn Compensation

When a player banks sharply into a turn (e.g. whipping their camera 90° or 180° during flight):

1. **Angular Rate Detection**: `TurnRateCalculator` calculates instantaneous yaw turn rate ($\Delta \text{yaw} / \Delta t$) using an Exponential Moving Average (EMA).
2. **Arc Fan-Out Expansion**: If turning is active ($\ge 4^\circ/\text{tick}$ at $\ge 0.50\text{ b/t}$), the forward corridor fans outward dynamically:
   - **Inner curve**: Expands +1 to +2 chunks deep into the apex of your turn (where your camera will face next).
   - **Outer curve**: Maintains a +1 chunk safety tangent on the outer rim.
3. **Recovery**: Once flight straightens out, the corridor immediately decays back into the narrow forward pencil line, conserving server resources.

---

## 🛡️ MSPT Watchdog & Performance Safeguards

- **MSPT Watchdog**: If server tick duration exceeds $25\text{ms}$ (dropping below 20 TPS), the lookahead reach automatically sheds distance and cuts outer fan-out chunks.
- **Zero-Allocation Hot Path**: Primitive sets (`LongOpenHashSet`), vector pooling, and cached references guarantee **0 bytes of GC heap allocation per frame and per tick**.
- **Fair Ticket Budget**: Multi-player servers distribute a global ticket budget fairly based on speed share, preventing any single player from starving chunk generation workers.
