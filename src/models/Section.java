package models;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Section {
    private final Queue<Item> items = new LinkedList<>();
    private final String name;
    private final int capacity;
    private final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public Section(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public void addItem(Item item, int tickCount) throws InterruptedException {
        lock.lock();
        try {
            while (items.size() >= capacity) {
                notFull.await(); // Wait if section is full
            }
            items.add(item);
            notEmpty.signal(); // Notify waiting customers that an item is available
            // Enhanced logging with tick count
            System.out.println("Tick: " + tickCount + " | " + "Item added to " + name + ": " + item);
        } finally {
            lock.unlock();
        }
    }

    public Item removeItem(int tickCount) throws InterruptedException {
        lock.lock();
        try {
            while (items.isEmpty()) {
                notEmpty.await(); // Wait if section is empty
            }
            Item item = items.poll();
            notFull.signal(); // Notify waiting assistants that there's space
            // Enhanced logging with tick count
            System.out.println("Tick: " + tickCount + " | " + "Item removed from " + name + ": " + item);
            return item;
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return items.size() >= capacity;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return items.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public String getName() {
        return name;
    }

    // Method to display current items in the section for debugging
    public void displayItems() {
        lock.lock();
        try {
            System.out.println("Items in " + name + ": " + items.size());
        } finally {
            lock.unlock();
        }
    }
}
