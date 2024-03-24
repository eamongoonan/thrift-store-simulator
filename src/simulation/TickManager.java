package simulation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TickManager implements Runnable {
    private final AtomicInteger currentTick = new AtomicInteger(0);
    private final int tickTimeSize; // Time in milliseconds for one tick
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final List<Consumer<Integer>> tickObservers = new CopyOnWriteArrayList<>();

    public TickManager(int tickTimeSize) {
        this.tickTimeSize = tickTimeSize;
    }

    public void registerTickObserver(Consumer<Integer> observer) {
        tickObservers.add(observer);
    }

    public void unregisterTickObserver(Consumer<Integer> observer) {
        tickObservers.remove(observer);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Thread.sleep(tickTimeSize);
                int tick = currentTick.incrementAndGet();
                // Notify all registered observers about the new tick
                tickObservers.forEach(observer -> observer.accept(tick));
            } catch (InterruptedException e) {
                running.set(false);
                Thread.currentThread().interrupt(); // Preserve interrupt status
                System.out.println("TickManager was interrupted and is stopping.");
            }
        }
    }

    public int getCurrentTick() {
        return currentTick.get();
    }

    public void stopTickManager() {
        running.set(false);
    }
}
