<p align="center">
    <a href="https://modrinth.com/mod/fabric-api"><img src="https://img.shields.io/badge/Requires-Fabric_API-blue?style=for-the-badge&amp;logo=fabric" alt="Requires Fabric API"></a>
    <a href="https://modrinth.com/mod/dasik-library"><img src="https://img.shields.io/badge/Requires-Dasik_Library-purple?style=for-the-badge" alt="Requires Dasik Library"></a>
    <img src="https://img.shields.io/badge/Language-Java_25-orange?style=for-the-badge&amp;logo=java" alt="Java 25">
    <img src="https://img.shields.io/badge/License-GPLv3-green?style=for-the-badge" alt="License">
    <img src="https://img.shields.io/badge/Minecraft-26.3-brightgreen?style=for-the-badge" alt="Minecraft 26.3">
</p>

<h2>&amp;#9889; Velocity Render</h2>

<blockquote>
  <p><strong>&quot;Render what&#39;s ahead, not what&#39;s behind.&quot;</strong></p>
</blockquote>

<p>Have you ever soared across the skies with an Elytra, raced boats across blue ice highways, or sprinted under Speed II&mdash;only to crash directly into an invisible chunk barrier or fall through a terrifying blue void hole while the terrain desperately tries to catch up?</p>

<p>Vanilla Minecraft&#39;s chunk builder treats all directions equally: it wastes precious CPU rendering power meshing chunks behind your back that you cannot even see, while the landscape directly in your flight path remains unbuilt.</p>

<p><strong>Velocity Render</strong> completely rewires this balance. By dynamically synchronizing Minecraft&#39;s chunk world generation and client meshing pipeline with your instantaneous velocity vector, the game actively prioritizes the terrain ahead of your travel trajectory&mdash;eliminating high-speed void pop-in, preventing Elytra wall stalls, and keeping your journey butter-smooth.</p>

<p>Part of the <strong>Vanilla Outsider Collection</strong> &mdash; mods that refine the vanilla experience with modern standards.</p>

<hr>

<h2>&amp;#10024; Features (Version 1.0.0)</h2>

<h3>&amp;#127950; Client-Side Anisotropic Meshing Prioritization</h3>
<p>Stop waiting for forward chunks to appear while your computer renders terrain behind you:</p>
<ul>
  <li><strong>Vanilla Limitation:</strong> Vanilla&#39;s chunk compile queue (<code>SectionTaskDynamicQueue</code>) evaluates chunk tasks using purely spherical Euclidean distance from the camera. A chunk 50 blocks behind your back gets the exact same compile priority as a chunk 50 blocks in front of your face.</li>
  <li><strong>Directional Velocity Bias:</strong> Calculates your instantaneous movement vector and applies an anisotropic dot-product distance bias. Sections inside your forward flight cone compile first, while rear sections are seamlessly deferred without visual glitches.</li>
  <li><strong>Zero-Allocation Hot Path:</strong> Precomputed volatile caching ensures 0B heap memory allocation per frame on the client render thread.</li>
</ul>

<h3>&amp;#127760; Server-Side Predictive Chunk Generation</h3>
<p>Smooth exploration across uncharted lands on dedicated servers and singleplayer worlds:</p>
<ul>
  <li><strong>Kinematic Look-Ahead Vector Blending:</strong> Blends player delta-movement momentum (70%) with camera look direction (30%). When you bank into a turn, chunks in the direction you are steering begin streaming immediately <em>before</em> momentum catches up.</li>
  <li><strong>Continuous Corridor Marching:</strong> Uses continuous step-by-step corridor ray marching (with banked turn fan-out at speeds &ge; 0.75 b/t), preventing diagonal chunk gaps.</li>
  <li><strong>Leak-Free Lifecycle:</strong> Tickets are immediately released when slowing down, changing dimensions, or disconnecting.</li>
</ul>

<h3>&amp;#9878;&#65039; Adaptive MSPT Load Watchdog</h3>
<p>High speed without sacrificing server performance:</p>
<ul>
  <li><strong>Continuous Server Monitoring:</strong> Continuously tracks server tick duration via <code>getAverageTickTimeNanos()</code>.</li>
  <li><strong>Automatic Load Shedding:</strong> If server MSPT exceeds 25ms, forward reach dynamically scales down to protect server tick rate, guaranteeing rock-solid 20 TPS.</li>
</ul>

<h3>&amp;#128268; Dedicated Compatibility &amp; HUD Integration</h3>
<ul>
  <li><strong>Universal Shaders &amp; Performance Engines:</strong> Because this mod purely optimizes CPU task ordering without altering graphical shaders or vertex formats, it is 100% compatible with <strong>Iris</strong>, <strong>Sodium</strong>, <strong>ImmediatelyFast</strong>, <strong>Lithium</strong>, <strong>Nvidium</strong>, <strong>Distant Horizons</strong>, and <strong>Bobby</strong>.</li>
  <li><strong>Server &amp; Client Sidedness:</strong>
    <ul>
      <li><em>Client-Only on Vanilla Servers:</em> Prioritizes meshing of received chunks in your forward velocity cone.</li>
      <li><em>Server-Only with Vanilla Clients:</em> Predictively generates and loads terrain ahead of fast players on the server, eliminating void walls for all connecting players.</li>
      <li><em>Both:</em> Maximum synergy across generation and meshing pipelines.</li>
    </ul>
  </li>
</ul>

<hr>

<h2>&amp;#128202; Quick Reference &amp; Mechanics Matrix</h2>

<table>
  <thead>
    <tr>
      <th>Mechanic</th>
      <th>Vanilla Behavior</th>
      <th>Velocity Render Enhancement</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Client Meshing Priority</strong></td>
      <td>Radial Euclidean distance (all directions equal)</td>
      <td>Anisotropic forward dot-product vector bias</td>
    </tr>
    <tr>
      <td><strong>High-Speed Chunk Loading</strong></td>
      <td>Loads radial ring around player; misses fast players</td>
      <td>Predictive forward corridor tickets up to 16 chunks ahead</td>
    </tr>
    <tr>
      <td><strong>Banked Turns (Elytra)</strong></td>
      <td>Only tracks past momentum</td>
      <td>70/30 kinematic blend with camera look vector</td>
    </tr>
    <tr>
      <td><strong>Server Load Protection</strong></td>
      <td>Unchecked generation can cause server tick spikes</td>
      <td>Adaptive MSPT watchdog throttles reach if MSPT &gt; 25ms</td>
    </tr>
    <tr>
      <td><strong>Client Hot-Path Memory</strong></td>
      <td>Standard heap allocations</td>
      <td>Volatile precomputed cache with 0B/frame heap allocation</td>
    </tr>
  </tbody>
</table>

<hr>

<h2>&amp;#128736;&#65039; In-Game Commands (<code>/velocityrender</code>, <code>/vr</code>)</h2>

<p>Full in-game Brigadier command suite accessible under <code>/velocityrender</code> or the short alias <code>/vr</code>:</p>

<table>
  <thead>
    <tr>
      <th>Command</th>
      <th>Permission</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>/vr status</code></td>
      <td>All Players</td>
      <td>Displays real-time speed (m/s &amp; b/t), forward lead distance, active tickets, and server MSPT.</td>
    </tr>
    <tr>
      <td><code>/vr get &lt;rule&gt;</code></td>
      <td>All Players</td>
      <td>Queries the current value of a configuration GameRule.</td>
    </tr>
    <tr>
      <td><code>/vr set &lt;rule&gt; &lt;value&gt;</code></td>
      <td>Gamemasters (Level 2)</td>
      <td>Updates settings in real time.</td>
    </tr>
    <tr>
      <td><code>/vr reset</code></td>
      <td>Gamemasters (Level 2)</td>
      <td>Restores all settings to vanilla defaults.</td>
    </tr>
    <tr>
      <td><code>/vr reload</code></td>
      <td>Gamemasters (Level 2)</td>
      <td>Reloads active configuration.</td>
    </tr>
  </tbody>
</table>

<hr>

<h2>&amp;#9881;&#65039; Native GameRules &amp; Configuration</h2>

<blockquote>
  <p><strong>&amp;#128161; Config vs. In-Game GameRules:</strong><br>
  The global configuration file only defines default values for newly created worlds. In existing worlds, change settings in-game via the <strong>Edit Game Rules</strong> UI screen or the <code>/gamerule</code> / <code>/vr</code> command.</p>
</blockquote>

<p>Customize behavior per-world with standard dynamic GameRules:</p>
<ul>
  <li><code>velocityrender:enabled</code>: Master toggle for the entire prioritization system. (Default: <code>true</code>)</li>
  <li><code>velocityrender:lead_multiplier</code>: Scales forward lead distance (0% to 300%). (Default: <code>100</code>)</li>
  <li><code>velocityrender:budget_conservation</code>: Enables adaptive lateral and rear ticket trimming. (Default: <code>true</code>)</li>
  <li><code>velocityrender:min_speed_threshold_pct</code>: Minimum speed in hundredths of a block/tick (0.20 b/t = 4.0 m/s) to activate forward bias. (Default: <code>20</code>)</li>
  <li><code>velocityrender:debug_mode</code>: Enables developer diagnostic logging to server console. (Default: <code>false</code>)</li>
</ul>

<hr>

<h2>&amp;#128214; In-Depth How-To &amp; Operational Playbook</h2>

<h3>1. Installation &amp; Dependency Verification</h3>
<ul>
  <li>Ensure you have installed <strong>Fabric Loader</strong> (&ge;0.18.4), <strong>Fabric API</strong>, and <strong>Dasik Library</strong> (&ge;1.8.0).</li>
  <li>Drop <code>velocity-render-1.0.0+26.3.jar</code> into your <code>mods</code> folder.</li>
</ul>

<h3>2. Live In-Game Telemetry Verification</h3>
<ul>
  <li>Hop into your world, equip an Elytra or jump on an ice boat.</li>
  <li>Type <code>/vr status</code> while in motion to view your real-time speed in b/t and m/s, active trajectory tickets, and current dynamic reach.</li>
</ul>

<h3>3. Tuning Forward Reach for High-Speed Transport</h3>
<ul>
  <li>If your server has powerful hardware and you want even farther Elytra lookahead, increase the multiplier:<br>
  <code>/vr set lead_multiplier 150</code></li>
  <li>If you run a heavily populated server and want conservative chunk generation, keep <code>budget_conservation</code> enabled:<br>
  <code>/vr set budget_conservation true</code></li>
</ul>

<hr>

<h2>&amp;#9749; Support</h2>

<p>If you enjoy the <strong>Vanilla Outsider</strong> collection and want to support ongoing development, consider supporting me!</p>

<p align="center">
    <a href="https://ko-fi.com/dasikigaijin"><img src="https://img.shields.io/badge/Ko--fi-Support%20Me-FF5E5B?style=for-the-badge&amp;logo=ko-fi&amp;logoColor=white" alt="Ko-fi"></a>
    <a href="https://sociabuzz.com/dasikigaijin/tribe"><img src="https://img.shields.io/badge/SocioBuzz-Local_Support-7BB32E?style=for-the-badge" alt="SocioBuzz"></a>
    <a href="https://saweria.co/DasikIgaijinn"><img src="https://img.shields.io/badge/Saweria-Local_Support-FFA500?style=for-the-badge" alt="Saweria"></a>
</p>

<blockquote>
  <p><strong>&amp;#127470;&amp;#127465; Indonesian Users:</strong> SocioBuzz and Saweria support local payment methods (Gopay, OVO, Dana, etc.) if you want to support me without using PayPal/Ko-fi!</p>
</blockquote>

<hr>

<h2>&amp;#128220; Credits &amp; Modpack Permissions</h2>

<table>
  <thead>
    <tr>
      <th>Role</th>
      <th>Author</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Creator</strong></td>
      <td><strong>Dasik</strong> (Rifaditya)</td>
    </tr>
    <tr>
      <td><strong>Collection</strong></td>
      <td>Vanilla Outsider</td>
    </tr>
    <tr>
      <td><strong>License</strong></td>
      <td>GPLv3</td>
    </tr>
    <tr>
      <td><strong>Source Code</strong></td>
      <td><a href="https://github.com/Rifaditya/Vanilla-Outsider-Velocity-Render">GitHub Repository</a></td>
    </tr>
  </tbody>
</table>

<blockquote>
  <p><strong>&amp;#128230; Modpack Permissions &amp; Distribution:</strong><br>
  You are fully welcome to include this mod in any modpack on any platform! However, the mod file must be downloaded directly through official distribution channels (<strong>CurseForge</strong> or <strong>Modrinth</strong>). Re-uploading, mirroring, or redistributing the original mod JAR to third-party mirror sites, scraper portals, or unauthorized launchers is strictly prohibited.</p>
  <p><strong>&amp;#9878;&#65039; License &amp; Fork Guidelines (No Zero-Change Re-uploads):</strong><br>
  This project is open-source under the <strong>GNU GPLv3</strong>. You are fully encouraged to inspect the code, learn from it, and fork the repository to create genuine modifications, substantial feature expansions, or community ports&mdash;provided your project remains open-source under GPLv3 with proper attribution.<br>
  <strong>However, straight 1:1 re-uploads, clone forks with no meaningful functional changes, or re-publishing identical builds under different project names (e.g. to farm downloads or rewards) are strictly forbidden.</strong></p>
</blockquote>

<hr>

<p align="center">
  <strong>Made with &amp;#10084;&#65039; for the Minecraft community</strong><br>
  <em>Part of the Vanilla Outsider Collection</em>
</p>
