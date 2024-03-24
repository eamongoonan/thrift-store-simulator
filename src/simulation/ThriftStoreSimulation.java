package simulation;

import models.Section;
import models.Delivery;
import threads.Assistant;
import threads.Customer;
import threads.DeliveryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThriftStoreSimulation {
    private static final int TICK_TIME = 100; // Milliseconds per tick, can be adjusted
    private static final int DELIVERY_QUEUE_CAPACITY = 10; // Capacity for the delivery queue

    public static void main(String[] args) {
        System.out.println("Initializing Thrift Store Simulation...");

        List<Section> sections = new ArrayList<>();
        sections.add(new Section("electronics", 10));
        sections.add(new Section("clothing", 10));
        sections.add(new Section("homeware", 10));

        BlockingQueue<Delivery> deliveryQueue = new ArrayBlockingQueue<>(DELIVERY_QUEUE_CAPACITY);

        ExecutorService executor = Executors.newCachedThreadPool();

        // Initialize and start the TickManager
        TickManager tickManager = new TickManager(TICK_TIME);
        Thread tickThread = new Thread(tickManager, "TickManager");
        tickThread.start();

        // Start the DeliveryManager with integration to the TickManager
        DeliveryManager deliveryManager = new DeliveryManager(deliveryQueue, tickManager);
        executor.execute(deliveryManager);

        // Start Assistant threads
        for (int i = 0; i < 2; i++) { // Starting 2 assistants for demonstration
            Assistant assistant = new Assistant(sections, "Assistant-" + (i + 1), tickManager, deliveryQueue);
            executor.execute(assistant);
        }

        // Start Customer threads
        for (int i = 0; i < 5; i++) { // Starting 5 customers for demonstration
            Customer customer = new Customer(sections, "Customer-" + (i + 1), tickManager, 10); // Attempts purchase every 10 ticks
            executor.execute(customer);
        }

        // Add shutdown hook for graceful simulation shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down simulation...");
            tickManager.stopTickManager(); // Signal the TickManager to stop
            executor.shutdownNow(); // Attempt to stop all actively executing tasks
            try {
                tickThread.join(); // Ensure the tick manager thread stops gracefully
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
            }
            System.out.println("Simulation has been successfully shutdown.");
        }));

        System.out.println("Thrift Store Simulation is running...");
    }
}
