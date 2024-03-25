package simulation;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TickManager implements Runnable {
    private final AtomicInteger currentTick = new AtomicInteger(0);
    private final int tickTimeSize;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Object tickUpdateMonitor = new Object();

    public TickManager(int tickTimeSize) {
        this.tickTimeSize = tickTimeSize;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Thread.sleep(tickTimeSize);
                currentTick.incrementAndGet();
                synchronized (tickUpdateMonitor) {
                    tickUpdateMonitor.notifyAll();
                }
            } catch (InterruptedException e) {
                running.set(false);
                Thread.currentThread().interrupt();
            }
        }
    }


    public int getCurrentTick() {
        return currentTick.get();
    }

    public void stopTickManager() {
        running.set(false);
        synchronized (tickUpdateMonitor) {
            tickUpdateMonitor.notifyAll(); // Ensure all waiting threads can exit
        }
    }

    public Object getTickUpdateMonitor() {
        return tickUpdateMonitor;
    }
}
