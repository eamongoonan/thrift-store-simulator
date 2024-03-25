package threads;

import models.Delivery;
import simulation.TickManager;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class DeliveryManager implements Runnable {
    private final BlockingQueue<Delivery> deliveryQueue;
    private final TickManager tickManager;
    private final Random random = new Random();
    private int lastDeliveryTick = -1; // Track the last tick on which a delivery was made
    private static final int COOLDOWN_TICKS = 50; // Minimum number of ticks between deliveries

    public DeliveryManager(BlockingQueue<Delivery> deliveryQueue, TickManager tickManager) {
        this.deliveryQueue = deliveryQueue;
        this.tickManager = tickManager;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int currentTick = tickManager.getCurrentTick();

            // Ensure a delivery is made only once per tick and respects the cooldown period
            if (currentTick - lastDeliveryTick >= COOLDOWN_TICKS && random.nextDouble() < 0.01) { // Assuming a 1% chance per tick
                Delivery delivery = new Delivery();
                try {
                    deliveryQueue.put(delivery);
                    System.out.println("Tick: " + currentTick + " | DeliveryManager created a new delivery with " + delivery.getItems().size() + " items.");
                    lastDeliveryTick = currentTick; // Update the last delivery tick
                } catch (InterruptedException e) {
                    System.out.println("DeliveryManager was interrupted while adding a delivery. Exiting.");
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Minimal sleep to wait for the next tick update
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("DeliveryManager was interrupted during the wait for the next tick. Exiting.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
