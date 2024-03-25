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

public class ThriftStoreSimulation
{
    private static final int TICK_TIME = 100; // Milliseconds per tick

    public static void main(String[] args)
    {
        System.out.println("Initializing Thrift Store Simulation...");

        List<Section> sections = new ArrayList<>();
        sections.add(new Section("electronics", 10));
        sections.add(new Section("clothing", 10));
        sections.add(new Section("homeware", 10));

        BlockingQueue<Delivery> deliveryQueue = new ArrayBlockingQueue<>(10);

        TickManager tickManager = new TickManager(TICK_TIME);
        Thread tickThread = new Thread(tickManager, "TickManager");
        tickThread.start();

        ExecutorService executor = Executors.newCachedThreadPool();

        DeliveryManager deliveryManager = new DeliveryManager(deliveryQueue, tickManager);
        executor.execute(deliveryManager);

        // Object tickUpdateMonitor is no longer needed for Customer instantiation
        Object tickUpdateMonitor = tickManager.getTickUpdateMonitor();

        for (int i = 0; i < 2; i++) {
            Assistant assistant = new Assistant(sections, "Assistant-" + (i + 1), tickManager, deliveryQueue, tickUpdateMonitor);
            executor.execute(assistant);
        }


        for (int i = 0; i < 5; i++)
        {
            // Updated to match the new Customer constructor without actionIntervalTicks and tickUpdateMonitor
            Customer customer = new Customer(sections, "Customer-" + (i + 1), tickManager);
            executor.execute(customer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            System.out.println("Shutting down simulation...");
            tickManager.stopTickManager();
            executor.shutdownNow();
            try
            {
                tickThread.join();
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            System.out.println("Simulation has been successfully shutdown.");
        }));

        System.out.println("Thrift Store Simulation is running...");
    }
}
