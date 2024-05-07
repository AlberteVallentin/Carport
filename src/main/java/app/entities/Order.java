package app.entities;

public class Order {
    private int orderID;
    private double price;
    private int userID;
    private String comment;
    private int shippingId;
    private int cpLength;
    private int cpWidth;
    private int shedLength;
    private int shedWidth;
    private int statusID;


    public Order(int orderID, double price, int userID, String comment, int shippingId, int cpLength, int cpWidth, int shedLength, int shedWidth, int statusID) {
        this.orderID = orderID;
        this.price = price;
        this.userID = userID;
        this.comment = comment;
        this.shippingId = shippingId;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.shedLength = shedLength;
        this.shedWidth = shedWidth;
        this.statusID = statusID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getShippingId() {
        return shippingId;
    }

    public void setShippingId(int shippingId) {
        this.shippingId = shippingId;
    }

    public int getCpLength() {
        return cpLength;
    }

    public void setCpLength(int cpLength) {
        this.cpLength = cpLength;
    }

    public int getCpWidth() {
        return cpWidth;
    }

    public void setCpWidth(int cpWidth) {
        this.cpWidth = cpWidth;
    }

    public int getShedLength() {
        return shedLength;
    }

    public void setShedLength(int shedLength) {
        this.shedLength = shedLength;
    }

    public int getShedWidth() {
        return shedWidth;
    }

    public void setShedWidth(int shedWidth) {
        this.shedWidth = shedWidth;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }
}
