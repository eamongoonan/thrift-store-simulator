package threads;

import models.Delivery;
import models.Section;
import simulation.TickManager;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Assistant extends Thread
{
    private final List<Section> sections;
    private final TickManager tickManager;
    private final BlockingQueue<Delivery> deliveryQueue;
    private volatile int lastActionTick = 0;
    private final Object tickUpdateMonitor;

    public Assistant(List<Section> sections, String name, TickManager tickManager, BlockingQueue<Delivery> deliveryQueue, Object tickUpdateMonitor)
    {
        super(name);
        this.sections = sections;
        this.tickManager = tickManager;
        this.deliveryQueue = deliveryQueue;
        this.tickUpdateMonitor = tickUpdateMonitor;
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            synchronized (tickUpdateMonitor)
            {
                try
                {
                    tickUpdateMonitor.wait();
                    int currentTick = tickManager.getCurrentTick();
                    if (currentTick - lastActionTick >= 10) {
                        if (!deliveryQueue.isEmpty())
                        {
                            Delivery delivery = deliveryQueue.take();
                            stockItems(delivery, currentTick);
                            lastActionTick = currentTick;
                        }
                    }
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void stockItems(Delivery delivery, int tick)
    {
        delivery.getItems().forEach(item -> {
            sections.forEach(section ->
            {
                if (section.getName().equalsIgnoreCase(item.getType()))
                {
                    try
                    {
                        section.addItem(item, tick);
                        System.out.println("Tick: " + tick + " | " + getName() + " stocked " + item + " in " + section.getName());
                    } catch (InterruptedException e)
                    {
                        System.out.println(getName() + " was interrupted while stocking items.");
                        Thread.currentThread().interrupt();
                    }
                }
            });
        });
    }
}
