package com.example.afc_rentit;

public class NotificationMessage {
    private int senderid;
    private int rentId;
    private double amount;
    private String start_date;
    private String end_date;
    private int duration;
    private String image;


    private String message;

    private String title;

    public int getSenderid() {
        return senderid;
    }

    public int getRentId() {
        return rentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public int getDuration() {
        return duration;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }



    public NotificationMessage(int senderid, int rentId, double amount, String start_date, String end_date, int duration, String message, String title) {
        this.senderid = senderid;
        this.rentId = rentId;
        this.amount = amount;
        this.start_date = start_date;
        this.end_date = end_date;
        this.duration = duration;
        this.message = message;
        this.title = title;
    }

    public String toString() {
        return "NotificationMessage{" +
                "senderId=" + senderid +
                ", rentId=" + rentId +
                ", totalAmount=" + amount +
                ", startDate='" + start_date + '\'' +
                ", endDate='" + end_date + '\'' +
                ", duration=" + duration +
                ", message='" + message + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
