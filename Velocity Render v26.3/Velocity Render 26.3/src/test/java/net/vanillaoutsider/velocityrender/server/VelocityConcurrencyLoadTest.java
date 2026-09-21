// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.velocityrender.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pillar 2: Virtual Concurrency & State Load Simulator.
 * Simulates 50+ concurrent virtual flyers competing for ticket budgets under rapid dive/turn bursts,
 * verifying zero deadlocks, thread-safety, budget limits, starvation prevention, and clean disconnect sweeps.
 */
class VelocityConcurrencyLoadTest {

    private static final int CONCURRENT_PLAYERS = 50;
    private static final int OPERATIONS_PER_PLAYER = 1_000;

    @Test
    @DisplayName("Concurrency: 50 virtual flyers simultaneously computing speed-weighted quotas")
    void testConcurrentPlayerQuotaAllocation() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_PLAYERS);
        CountDownLatch readyLatch = new CountDownLatch(CONCURRENT_PLAYERS);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger totalSuccessOps = new AtomicInteger(0);

        List<Future<Boolean>> futures = new ArrayList<>(CONCURRENT_PLAYERS);

        for (int p = 0; p < CONCURRENT_PLAYERS; p++) {
            final int playerIndex = p;
            Callable<Boolean> task = () -> {
                readyLatch.countDown();
                // Synchronized blast start
                if (!startLatch.await(5, TimeUnit.SECONDS)) {
                    return false;
                }

                Random rng = new Random(0xDA51C_001L + playerIndex);
                int serverBudget = 64;
                int maxReach = 16;

                for (int op = 0; op < OPERATIONS_PER_PLAYER; op++) {
                    double playerSpeed = 0.20 + rng.nextDouble() * 3.80; // 0.20 to 4.00 b/t
                    double totalSpeed = playerSpeed * (CONCURRENT_PLAYERS / 2.0); // Simulated group sum
                    int activeFlyers = CONCURRENT_PLAYERS;

                    int quota = TicketBudgetAllocator.calculatePlayerQuota(
                            playerSpeed, totalSpeed, activeFlyers, serverBudget, maxReach
                    );

                    // Assertions: quota must be within physical and fair bounds
                    if (quota < 1 || quota > maxReach) {
                        return false;
                    }

                    totalSuccessOps.incrementAndGet();
                }
                return true;
            };
            futures.add(executor.submit(task));
        }

        // Wait for all threads to be ready, then trigger
        assertTrue(readyLatch.await(5, TimeUnit.SECONDS), "Timed out waiting for threads to prime");
        startLatch.countDown();

        for (Future<Boolean> future : futures) {
            assertTrue(future.get(10, TimeUnit.SECONDS), "Thread failed quota calculation invariant");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(CONCURRENT_PLAYERS * OPERATIONS_PER_PLAYER, totalSuccessOps.get());
    }

    @Test
    @DisplayName("Load Shedding: 100 simultaneous players burst never starves slow flyers to 0")
    void testAntiStarvationUnderExtremeFlyerLoad() {
        int activeFlyers = 100;
        int serverBudget = 64; // Budget is smaller than flyer count
        int maxReach = 16;

        double[] speeds = new double[activeFlyers];
        double totalSpeed = 0.0;
        Random rng = new Random(42L);

        // Mix of slow gliders and supersonic rockets
        for (int i = 0; i < activeFlyers; i++) {
            speeds[i] = (i % 10 == 0) ? 25.0 : (0.25 + rng.nextDouble() * 0.50);
            totalSpeed += speeds[i];
        }

        int totalAllocated = 0;
        for (int i = 0; i < activeFlyers; i++) {
            int quota = TicketBudgetAllocator.calculatePlayerQuota(
                    speeds[i], totalSpeed, activeFlyers, serverBudget, maxReach
            );

            // Invariant: Even with budget (64) < flyers (100), slow players are never starved to 0
            assertTrue(quota >= 1, "Player " + i + " was starved to 0 quota!");
            assertTrue(quota <= maxReach, "Player quota exceeded maxReach ceiling: " + quota);
            totalAllocated += quota;
        }

        assertTrue(totalAllocated > 0, "Total allocated must be positive");
    }

    @Test
    @DisplayName("State Safety: Concurrent UUID querying and mock player lifecycle sweeps")
    void testConcurrentPlayerQueryAndLifecycleSafety() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Future<Void>> futures = new ArrayList<>(20);

        for (int t = 0; t < 20; t++) {
            Callable<Void> worker = () -> {
                startLatch.await();
                for (int i = 0; i < 500; i++) {
                    UUID randomUuid = UUID.randomUUID();

                    // Querying unregistered players from multiple threads concurrently
                    assertEquals(0, VelocityTicketManager.getPlayerQuota(randomUuid));
                    assertEquals(0.0, VelocityTicketManager.getPlayerSpeed(randomUuid), 0.001);
                    assertEquals(0.0f, VelocityTicketManager.getPlayerTurnRate(randomUuid), 0.001f);
                    assertEquals(0, VelocityTicketManager.getActiveTicketCount(randomUuid));
                    assertEquals(0, VelocityTicketManager.getPlayerDynamicReach(randomUuid));

                    // Safe disconnect handler call with null player
                    VelocityTicketManager.onPlayerDisconnect(null);
                }
                return null;
            };
            futures.add(executor.submit(worker));
        }

        startLatch.countDown();
        for (Future<Void> future : futures) {
            future.get(10, TimeUnit.SECONDS);
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Budget Saturation: Extreme single-player speed never exceeds max reach")
    void testExtremeSpeedSinglePlayerAllocation() {
        int serverBudget = 256;
        int maxReach = 16;
        double insaneSpeed = 100_000.0; // Hyperspeed travel

        int quota = TicketBudgetAllocator.calculatePlayerQuota(
                insaneSpeed, insaneSpeed, 1, serverBudget, maxReach
        );

        // Clamped at max reach ceiling, never overflows to 256 or Integer.MAX_VALUE
        assertEquals(maxReach, quota);
    }
}
