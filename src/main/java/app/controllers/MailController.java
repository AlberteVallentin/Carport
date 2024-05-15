package app.controllers;

import app.entities.Order;
import app.entities.Shipping;
import app.entities.User;
import app.utility.MailServer;

import java.io.IOException;

public class MailController {

    public static void sendOrderConfirmation(Order order, User user, int orderId) {
        try {
            MailServer.sendOrderConfirmation(user.getFirstName(), user.getLastName(), user.getEmail(), orderId, order.getCpWidth(), order.getCpLength(), order.getCpRoof(), order.getShWidth(), order.getShLength());
        } catch (IOException e) {
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    public static void sendOffer(Order order, int orderId, double shippingRate, double price) {
        User user = order.getUser();
        try {
            MailServer.sendOffer(user.getFirstName(), user.getLastName(), user.getEmail(), shippingRate, price, order.getOrderId());
        } catch (IOException e) {
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    public static void sendNewOffer(Order order, int orderId, double shippingRate, double price) {
        User user = order.getUser();
        try {
            MailServer.sendNewOffer(user.getFirstName(), user.getLastName(), user.getEmail(), shippingRate, price, orderId);
        } catch (IOException e) {
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    public static void denyNewOffer(Order order, int orderId, double shippingRate) {
        User user = order.getUser();
        try {
            MailServer.denyNewOffer(user.getFirstName(), user.getLastName(), user.getEmail(), shippingRate, order.getPrice(), orderId);
        } catch (IOException e) {
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    public static void paymentConfirmed(Order order) {
        User user = order.getUser();
        try {
            MailServer.paymentConfirmed(user.getFirstName(), user.getLastName(), user.getEmail(), order.getOrderId(), order.getPrice());
        } catch (IOException e) {
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    public static void sendModifiedOrder(Order order, int orderId) {
        User user = order.getUser();
        try {
            MailServer.sendModifiedOrder(user.getFirstName(), user.getLastName(), user.getEmail(), order.getCpWidth(), order.getCpLength(), order.getCpRoof(), order.getShWidth(), order.getShLength(), order.getOrderId());
        } catch (IOException e) {
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

}
