package threads;

import models.Section;
import simulation.TickManager;
import java.util.List;
import java.util.Random;

public class Customer extends Thread
{
    private final List<Section> sections;
    private final TickManager tickManager;
    private static final double PURCHASE_PROBABILITY = 0.1; // Adjust as necessary
    private final Random rand = new Random();

    public Customer(List<Section> sections, String name, TickManager tickManager)
    {
        super(name);
        this.sections = sections;
        this.tickManager = tickManager;
    }

    @Override
    public void run()
    {
        Object tickUpdateMonitor = tickManager.getTickUpdateMonitor();
        while (!Thread.currentThread().isInterrupted())
        {
            synchronized (tickUpdateMonitor)
            {
                try {
                    tickUpdateMonitor.wait(); // Wait for the next tick update
                    if (rand.nextDouble() < PURCHASE_PROBABILITY)
                    {
                        attemptPurchase(tickManager.getCurrentTick());
                    }
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void attemptPurchase(int currentTick)
    {
        // Implement purchase logic here
    }
}
