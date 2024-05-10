package app.entities;

public class Order {
    private int orderId;
    private double price;
    private User user;
    private String comment;
    private int shippingId;
    private Integer cpLength;
    private Integer cpWidth;
    private String cpRoof;
    private Integer shedLength;
    private Integer shedWidth;
    private int statusId;

    public Order(int orderId, double price, User user, String comment, int shippingId, Integer cpLength, Integer cpWidth, String cpRoof, Integer shedLength, Integer shedWidth, int statusId) {
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

    public Integer getCpLength() {
        return cpLength;
    }

    public void setCpLength(Integer cpLength) {
        this.cpLength = cpLength;
    }

    public Integer getCpWidth() {
        return cpWidth;
    }

    public void setCpWidth(Integer cpWidth) {
        this.cpWidth = cpWidth;
    }

    public String getCpRoof() {
        return cpRoof;
    }

    public void setCpRoof(String cpRoof) {
        this.cpRoof = cpRoof;
    }

    public Integer getShedLength() {
        return shedLength;
    }

    public void setShedLength(Integer shedLength) {
        this.shedLength = shedLength;
    }

    public Integer getShedWidth() {
        return shedWidth;
    }

    public void setShedWidth(Integer shedWidth) {
        this.shedWidth = shedWidth;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
