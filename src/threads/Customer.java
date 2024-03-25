package threads;

import models.Section;
import simulation.TickManager;
import java.util.List;

public class Customer extends Thread {
    private final List<Section> sections;
    private final TickManager tickManager;
    private volatile int lastActionTick = 0;
    private final int actionInterval;

    public Customer(List<Section> sections, String name, TickManager tickManager, int actionIntervalTicks, Object tickUpdateMonitor) {
        super(name);
        this.sections = sections;
        this.tickManager = tickManager;
        this.actionInterval = actionIntervalTicks;
    }

    @Override
    public void run() {
        Object tickUpdateMonitor = tickManager.getTickUpdateMonitor();
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (tickUpdateMonitor) {
                try {
                    tickUpdateMonitor.wait();
                    int currentTick = tickManager.getCurrentTick();
                    if (currentTick - lastActionTick >= actionInterval) {
                        performAction(currentTick);
                        lastActionTick = currentTick;
                    }
                } catch (InterruptedException e) {
                    System.out.println(getName() + " was interrupted.");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void performAction(int currentTick) {
        Section section = selectRandomSection();
        boolean purchased = false;
        synchronized (section) {
            try {
                if (!section.isEmpty()) {
                    section.removeItem(currentTick);
                    purchased = true;
                }
            } catch (InterruptedException e) {
                System.out.println(getName() + " was interrupted while attempting to purchase.");
                Thread.currentThread().interrupt();
                return;
            }
        }
        if (purchased) {
            System.out.println("Tick: " + currentTick + " | " + getName() + " bought an item from " + section.getName());
        } else {
            System.out.println("Tick: " + currentTick + " | " + getName() + " found " + section.getName() + " empty and will try again later.");
        }
    }

    private Section selectRandomSection() {
        return sections.get((int) (Math.random() * sections.size()));
    }
}
