package app.entities;

import java.util.Objects;

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
    private int statusId;
    private String status;// fra status tabel
    private int shippingId;


    public Order(double price, User user, String comment, int cpLength, int cpWidth, String cpRoof, int shLength, int shWidth) {
        this.price = price;
        this.user = user;
        this.comment = comment;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.cpRoof = cpRoof;
        this.shLength = shLength;
        this.shWidth = shWidth;

    }

    public Order(int orderId, double price, User user, String comment, Shipping shipping, int cpLength, int cpWidth, String cpRoof, int shLength, int shWidth, int statusId) {
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
        this.statusId = statusId;

    }

    // Constructor without orderId for testing
    public Order(double price, User user, String comment, int shippingId, int cpLength, int cpWidth, int shLength, int shWidth, int statusId, String cpRoof) {
        this.price = price;
        this.user = user;
        this.comment = comment;
        this.shippingId = shippingId;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.shLength = shLength;
        this.shWidth = shWidth;
        this.statusId = statusId;
        this.cpRoof = cpRoof;

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



    // Constructor without comment and shipping
    public Order(int orderId, double price, User user, int cpLength, int cpWidth, String cpRoof, int shLength, int shWidth, int statusId) {
        this.orderId = orderId;
        this.price = price;
        this.user = user;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.cpRoof = cpRoof;
        this.shLength = shLength;
        this.shWidth = shWidth;
        this.statusId = statusId;
    }

    // Constructor with status name
    public Order(int orderId, double price, User user, int cpLength, int cpWidth, String cpRoof, int shLength, int shWidth, int statusId, String status) {
        this.orderId = orderId;
        this.price = price;
        this.user = user;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.cpRoof = cpRoof;
        this.shLength = shLength;
        this.shWidth = shWidth;
        this.statusId = statusId;
        this.status = status;
    }

    // Constructor with status name and comment and shippingId
    public Order(int orderId, double price, User user, int cpLength, int cpWidth, String cpRoof, int shLength, int shWidth, int statusId, String status, String comment, int shippingId) {
        this.orderId = orderId;
        this.price = price;
        this.user = user;
        this.cpLength = cpLength;
        this.cpWidth = cpWidth;
        this.cpRoof = cpRoof;
        this.shLength = shLength;
        this.shWidth = shWidth;
        this.statusId = statusId;
        this.status = status;
        this.comment = comment;
        this.shippingId = shippingId;
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

    public int getStatusId() {
        return statusId;
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

    public String getStatus() {
        return status;
    }

    public int getShippingId() {
        return shippingId;
    }

    public void setShippingId(int shippingId) {
        this.shippingId = shippingId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", price=" + price +
                ", user=" + user +
                ", comment='" + comment + '\'' +
                ", shipping=" + shipping +
                ", cpLength=" + cpLength +
                ", cpWidth=" + cpWidth +
                ", cpRoof='" + cpRoof + '\'' +
                ", shLength=" + shLength +
                ", shWidth=" + shWidth +
                ", statusId=" + statusId +
                ", status='" + status + '\'' +
                ", shippingId=" + shippingId +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Order order = (Order) obj;
        return orderId == order.orderId &&
                Double.compare(order.price, price) == 0 &&
                cpLength == order.cpLength &&
                cpWidth == order.cpWidth &&
                shLength == order.shLength &&
                shWidth == order.shWidth &&
                statusId == order.statusId &&
                Objects.equals(user, order.user) &&
                Objects.equals(comment, order.comment) &&
                Objects.equals(shipping, order.shipping) &&
                Objects.equals(cpRoof, order.cpRoof);
    }
}