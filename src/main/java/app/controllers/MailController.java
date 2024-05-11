package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.utility.MailServer;

import java.io.IOException;

public class MailController {

    public static void sendOrderConfirmation(Order order) {
        User user = order.getUser();
        try {
            MailServer.sendOrderConfirmation(user.getFirstName(), user.getLastName(), user.getEmail(), order.getOrderId());
        } catch (IOException e) {
            System.out.println("Error sending mail: " + e.getMessage());
        }
    }
}
