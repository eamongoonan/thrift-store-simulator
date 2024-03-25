package simulation;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TickManager implements Runnable
{
    private final AtomicInteger currentTick = new AtomicInteger(0);
    private final int tickTimeSize;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Object tickUpdateMonitor = new Object();

    public TickManager(int tickTimeSize)
    {
        this.tickTimeSize = tickTimeSize;
    }

    @Override
    public void run()
    {
        while (running.get())
        {
            try
            {
                // Simulate the passage of time for one tick
                Thread.sleep(tickTimeSize);
                synchronized (tickUpdateMonitor)
                {
                    // Increment the tick count and notify all waiting threads
                    currentTick.incrementAndGet();
                    tickUpdateMonitor.notifyAll();
                }
            } catch (InterruptedException e)
            {
                // Handle thread interruption (e.g., for graceful shutdown)
                running.set(false);
                Thread.currentThread().interrupt(); // Ensure the interrupt flag is set
            }
        }
    }

    public int getCurrentTick()
    {
        // Accessor for the current tick count
        return currentTick.get();
    }

    public void stopTickManager()
    {
        // Method to stop the tick manager's execution
        running.set(false);
        synchronized (tickUpdateMonitor)
        {
            // Notify all waiting threads to enable a graceful shutdown
            tickUpdateMonitor.notifyAll();
        }
    }

    public Object getTickUpdateMonitor()
    {
        // Accessor for the object on which threads synchronise for tick updates
        return tickUpdateMonitor;
    }
}
