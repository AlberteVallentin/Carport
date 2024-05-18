package app.controllers;

import app.entities.Order;
import app.entities.Shipping;
import app.entities.User;
import app.utility.MailServer;

import java.io.IOException;

public class MailController {

    /**
     * Sends an order confirmation email to the customer.
     *
     * @param order   The order details.
     * @param user    The user details.
     * @param orderId The order ID.
     */
    public static void sendOrderConfirmation(Order order, User user, int orderId) {
        try {
            // Send order confirmation email using MailServer utility
            MailServer.sendOrderConfirmation(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    orderId,
                    order.getCpWidth(),
                    order.getCpLength(),
                    order.getCpRoof(),
                    order.getShWidth(),
                    order.getShLength()
            );
        } catch (IOException e) {
            // Log error if email sending fails
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    /**
     * Sends an offer email to the customer with order and shipping details.
     *
     * @param order        The order details.
     * @param orderId      The order ID.
     * @param shippingRate The shipping rate for the order.
     * @param price        The total price of the order.
     */
    public static void sendOffer(Order order, int orderId, double shippingRate, double price) {
        User user = order.getUser();
        try {
            // Send offer email using MailServer utility
            MailServer.sendOffer(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    shippingRate,
                    price,
                    orderId
            );
        } catch (IOException e) {
            // Log error if email sending fails
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    /**
     * Sends a new offer email to the customer with updated order and shipping details.
     *
     * @param order        The order details.
     * @param orderId      The order ID.
     * @param shippingRate The shipping rate for the order.
     * @param price        The updated total price of the order.
     */
    public static void sendNewOffer(Order order, int orderId, double shippingRate, double price) {
        User user = order.getUser();
        try {
            // Send new offer email using MailServer utility
            MailServer.sendNewOffer(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    shippingRate,
                    price,
                    orderId
            );
        } catch (IOException e) {
            // Log error if email sending fails
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    /**
     * Sends an email to the customer denying the new offer.
     *
     * @param order        The order details.
     * @param orderId      The order ID.
     * @param shippingRate The shipping rate for the order.
     */
    public static void denyNewOffer(Order order, int orderId, double shippingRate) {
        User user = order.getUser();
        try {
            // Send denial email using MailServer utility
            MailServer.denyNewOffer(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    shippingRate,
                    order.getPrice(),
                    orderId
            );
        } catch (IOException e) {
            // Log error if email sending fails
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    /**
     * Sends a payment confirmation email to the customer.
     *
     * @param order The order details.
     */
    public static void paymentConfirmed(Order order) {
        User user = order.getUser();
        try {
            // Send payment confirmation email using MailServer utility
            MailServer.paymentConfirmed(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    order.getOrderId(),
                    order.getPrice()
            );
        } catch (IOException e) {
            // Log error if email sending fails
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }

    /**
     * Sends an email to the customer with the modified order details.
     *
     * @param order   The order details.
     * @param orderId The order ID.
     */
    public static void sendModifiedOrder(Order order, int orderId) {
        User user = order.getUser();
        try {
            // Send modified order email using MailServer utility
            MailServer.sendModifiedOrder(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    order.getCpWidth(),
                    order.getCpLength(),
                    order.getCpRoof(),
                    order.getShWidth(),
                    order.getShLength(),
                    order.getOrderId()
            );
        } catch (IOException e) {
            // Log error if email sending fails
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }
}
