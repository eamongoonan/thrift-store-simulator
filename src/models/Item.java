package models;

public class Item
{
    private static int nextId = 1; // Static counter to generate unique IDs for each item
    private final int id;
    private final String type; // Type of item (e.g., "electronics", "clothing", "homeware")

    public Item(String type)
    {
        this.id = nextId++;
        this.type = type;
    }

    public int getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return "Item{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
