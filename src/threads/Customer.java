package threads;

import models.Section;
import simulation.TickManager;

import java.util.List;
import java.util.function.Consumer;

public class Customer extends Thread implements Consumer<Integer> {
    private final List<Section> sections;
    private final TickManager tickManager;
    private volatile int lastActionTick = 0;
    private final int actionInterval;

    public Customer(List<Section> sections, String name, TickManager tickManager, int actionIntervalTicks) {
        super(name);
        this.sections = sections;
        this.tickManager = tickManager;
        this.actionInterval = actionIntervalTicks;
        this.tickManager.registerTickObserver(this); // Register as an observer to the TickManager
    }

    @Override
    public void run() {
        // Now waiting for tick updates, no need to actively check the tick count in the run method
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100); // Sleep to reduce CPU usage, still responsive to tick updates
            } catch (InterruptedException e) {
                System.out.println(getName() + " was interrupted.");
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void accept(Integer currentTick) {
        // Perform actions in response to tick updates
        if (currentTick - lastActionTick >= actionInterval) {
            Section section = selectRandomSection();
            boolean purchased = false;
            try {
                synchronized (section) { // Synchronize access to the section
                    if (!section.isEmpty()) {
                        section.removeItem(currentTick); // Enhanced to include tick count
                        purchased = true;
                    }
                }
                if (purchased) {
                    System.out.println("Tick: " + currentTick + " | " + getName() + " bought an item from " + section.getName());
                } else {
                    System.out.println("Tick: " + currentTick + " | " + getName() + " found " + section.getName() + " empty and will try again later.");
                }
                lastActionTick = currentTick;
            } catch (InterruptedException e) {
                System.out.println(getName() + " was interrupted while attempting to purchase.");
                Thread.currentThread().interrupt();
            }
        }
    }

    private Section selectRandomSection() {
        return sections.get((int) (Math.random() * sections.size()));
    }

    @Override
    public void interrupt() {
        super.interrupt();
        tickManager.unregisterTickObserver(this); // Unregister from TickManager upon interruption
    }
}
