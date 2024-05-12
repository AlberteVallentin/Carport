package app.entities;

public class Order {
    private int orderId;
    private double price;
    private User user;
    private String comment;
    private Shipping shipping;
    private int cpLength;
    private int cpWidth;
    private String cpRoof;
    private int shLength;
    private int shWidth;
    private Status status;

    public Order(int orderId, double price, User user, String comment, Shipping shipping, int cpLength, int cpWidth, String cpRoof, int shLength, int shWidth, Status status) {
        this.orderId = orderId;
        this.price = price;
        this.user = user;
        this.comment = comment;
        this.shipping = shipping;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.cpRoof = cpRoof;
        this.shLength = shLength;
        this.shWidth = shWidth;
        this.status = status;
    }

    // Constructor without price, shipping, orderId and status
    public Order(User user, String comment, int cpLength, int cpWidth, String cpRoof, int shLength, int shWidth) {
        this.user = user;
        this.comment = comment;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.cpRoof = cpRoof;
        this.shLength = shLength;
        this.shWidth = shWidth;
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

    public int getShLength() {
        return shLength;
    }

    public void setShLength(int shLength) {
        this.shLength = shLength;
    }

    public int getShWidth() {
        return shWidth;
    }

    public void setShWidth(int shWidth) {
        this.shWidth = shWidth;
    }

    public Status getStatus() {
        return status;
    }

    public int getUserId() {
        return user.getUserId();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public double getShippingPrice() {
        return shipping.getShippingRate();
    }

    public Shipping getShipping() {
        return shipping;
    }
}