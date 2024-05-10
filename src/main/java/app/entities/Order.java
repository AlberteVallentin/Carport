package app.entities;

public class Order {
    private int orderId;
    private double price;
    private User user;
    private String comment;
    private int shippingId;
    private int cpLength;
    private int cpWidth;
    private String cpRoof;
    private int shedLength;
    private int shedWidth;
    private int statusId;

public Order(int orderId, double price, User user, String comment, int shippingId, int cpLength, int cpWidth, String cpRoof, int shedLength, int shedWidth, int statusId) {
        this.orderId = orderId;
        this.price = price;
        this.user = user;
        this.comment = comment;
        this.shippingId = shippingId;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.cpRoof = cpRoof;
        this.shedLength = shedLength;
        this.shedWidth = shedWidth;
        this.statusId = statusId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getCpRoof() {
        return cpRoof;
    }

    public void setCpRoof(String cpRoof) {
        this.cpRoof = cpRoof;
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

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", price=" + price +
                ", user=" + user +
                ", comment='" + comment + '\'' +
                ", shippingId=" + shippingId +
                ", cpLength=" + cpLength +
                ", cpWidth=" + cpWidth +
                ", cpRoof='" + cpRoof + '\'' +
                ", shedLength=" + shedLength +
                ", shedWidth=" + shedWidth +
                ", statusId=" + statusId +
                '}';
    }
}