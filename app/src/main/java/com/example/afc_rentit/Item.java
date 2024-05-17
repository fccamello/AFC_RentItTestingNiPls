package com.example.afc_rentit;

public class Item {
    int item_id;
    int owner_id;
    String title;
    String image;
    String description;
    String category;
    double price;
    public Item(int item_id, int user_id, String title, String image, String description, String category, double price) {
        this.item_id = item_id;
        this.owner_id = user_id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    public int getItem_id() {
        return item_id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

}