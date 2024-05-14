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

    public static void sendOffer(Order order) {
        User user = order.getUser();
        Shipping shipping = order.getShipping();
        try {
            MailServer.sendOffer(user.getFirstName(), user.getLastName(), user.getEmail(), shipping.getShippingRate(), order.getPrice());
        } catch (IOException e) {
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    public static void sendNewOffer(Order order) {
        User user = order.getUser();
        Shipping shipping = order.getShipping();
        try {
            MailServer.sendNewOffer(user.getFirstName(), user.getLastName(), user.getEmail(), shipping.getShippingRate(), order.getPrice());
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
