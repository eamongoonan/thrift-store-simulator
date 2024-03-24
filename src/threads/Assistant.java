package threads;

import models.Delivery;
import models.Section;
import simulation.TickManager;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class Assistant extends Thread implements Consumer<Integer> {
    private final List<Section> sections;
    private final TickManager tickManager;
    private final BlockingQueue<Delivery> deliveryQueue;
    private volatile int lastActionTick = 0;

    public Assistant(List<Section> sections, String name, TickManager tickManager, BlockingQueue<Delivery> deliveryQueue) {
        super(name);
        this.sections = sections;
        this.tickManager = tickManager;
        this.deliveryQueue = deliveryQueue;
        this.tickManager.registerTickObserver(this); // Register as observer
    }

    @Override
    public void run() {
        // The actual work is moved to the accept method to be triggered by tick updates
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100); // Sleep to reduce CPU usage, waiting for tick updates
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
                System.out.println(getName() + " was interrupted.");
            }
        }
    }

    @Override
    public void accept(Integer currentTick) {
        // Perform actions in response to tick updates
        if (currentTick - lastActionTick >= 10) { // Check if it's time to act
            try {
                Delivery delivery = deliveryQueue.take(); // Attempt to take a delivery from the queue
                System.out.println("Tick: " + currentTick + " | " + getName() + " started processing a delivery.");
                stockItems(delivery, currentTick);
                lastActionTick = currentTick;
            } catch (InterruptedException e) {
                System.out.println(getName() + " was interrupted while processing a delivery.");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void stockItems(Delivery delivery, int tick) {
        delivery.getItems().forEach(item -> {
            sections.forEach(section -> {
                if (section.getName().equalsIgnoreCase(item.getType())) {
                    try {
                        section.addItem(item, tick); // Pass tick count for enhanced logging
                        System.out.println("Tick: " + tick + " | " + getName() + " stocked " + item + " in " + section.getName());
                    } catch (InterruptedException e) {
                        System.out.println(getName() + " was interrupted while stocking items.");
                        Thread.currentThread().interrupt(); // Preserve interrupt status
                    }
                }
            });
        });
    }

    // Cleanup on thread termination, if necessary
    @Override
    public void interrupt() {
        super.interrupt();
        tickManager.unregisterTickObserver(this); // Unregister as observer when interrupted
    }
}
