package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Delivery
{
    private final List<Item> items;

    public Delivery()
    {
        this.items = new ArrayList<>();
        generateRandomItems();
    }

    private void generateRandomItems()
    {
        // Assuming the categories are "electronics", "clothing", and "homeware".
        String[] categories = {"electronics", "clothing", "homeware"};
        Random random = new Random();

        // Ensure a total of 10 items per delivery
        for (int i = 0; i < 10; i++)
        {
            int categoryIndex = random.nextInt(categories.length);
            this.items.add(new Item(categories[categoryIndex]));
        }
    }

    public List<Item> getItems()
    {
        return items;
    }

    @Override
    public String toString()
    {
        return "Delivery{" +
                "items=" + items +
                '}';
    }
}
