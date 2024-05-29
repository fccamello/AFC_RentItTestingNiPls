package com.example.afc_rentit.DashboardUtils;

public class dashboard_item {
    int item_id;
    int owner_id;
    String title;
    String image;
    String description;
    String category;
    double price;
    int isAvailable;
    int isReturned;
    int rent_id; // for renter
    public dashboard_item(int item_id, String title, String image, String description, String category, double price) {
        this.item_id = item_id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    public int getRent_id() {
        return rent_id;
    }

    public void setRent_id(int rent_id) {
        this.rent_id = rent_id;
    }

    public int getItem_id() {
        return item_id;
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

    public int getIsAvailable() {
        return isAvailable;
    }

    public int getIsReturned() {
        return isReturned;
    }

    public void setIsAvailable(int isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setItemOwner (int owner_id){
        this.owner_id = owner_id;
    }

    public int getOwnerId(){
        return owner_id;
    }
    public void setIsReturned(int isReturned) {
        this.isReturned = isReturned;
    }
}