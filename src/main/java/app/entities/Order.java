package app.entities;

public class Order {
    private int orderID;
    private double price;
    private int userID;
    private String comment;
    private int shippingId;
    private int cpLength;
    private int cpWidth;
    private int shredLength;
    private int shredWidth;
    private int statusID;



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

    public int getShredLength() {
        return shredLength;
    }

    public void setShredLength(int shredLength) {
        this.shredLength = shredLength;
    }

    public int getShredWidth() {
        return shredWidth;
    }

    public void setShredWidth(int shredWidth) {
        this.shredWidth = shredWidth;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }
}
