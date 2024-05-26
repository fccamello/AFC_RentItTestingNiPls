package com.example.afc_rentit;

public class NotificationItem {
    private int ownerid;
    private int rentId;
    private int itemId;
    private int userId;
    private int isApproved;
    private String title;
    private double pricePerDay;
    private int durationDays;
    private String startDate;
    private String endDate;
    private double totalPrice;
    private String image;

    public NotificationItem(int rentId, int itemId, int userId, int isApproved, String title, double pricePerDay, int durationDays, String startDate, String endDate, double totalPrice, String image, int ownerid) {
        this.rentId = rentId;
        this.ownerid = ownerid;
        this.itemId = itemId;
        this.userId = userId;
        this.isApproved = isApproved;
        this.title = title;
        this.pricePerDay = pricePerDay;
        this.durationDays = durationDays;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.image = image;
    }

    public int getRentId() {
        return rentId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getUserId() {
        return userId;
    }

    public int getOwnerId(){
        return ownerid;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public String getTitle() {
        return title;
    }


    public int getDurationDays() {
        return durationDays;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getImage() {

        return image;
    }

}
