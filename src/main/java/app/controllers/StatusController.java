package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.sql.SQLException;

public class StatusController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/status", ctx -> ctx.render("status.html"));
        app.post("/statusRedirect", ctx -> statusRedirect(ctx, connectionPool));
        app.get("/confirm-offer", ctx -> ctx.render("confirm-offer.html"));
        app.get("/confirm-newcarport", ctx -> ctx.render("confirm-newcarport.html"));
        app.post("/newcarportconfirmed", ctx -> newCarportConfirmed(ctx, connectionPool));
        app.post("/offerconfirmed", ctx -> offerConfirmed(ctx, connectionPool));
        app.get("/orderdone", ctx -> ctx.render("order-done.html"));
        app.get("/deleteorder", ctx -> ctx.render("order-deleted.html"));


    }


    private static void statusRedirect(Context ctx, ConnectionPool connectionPool) {
        try {
            String email = ctx.formParam("email");
            String password = ctx.formParam("password");
            String orderIdStr = ctx.formParam("order-id");
            if (orderIdStr == null || orderIdStr.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
                ctx.attribute("message", "Udfyld venligst alle felter");
                ctx.render("status.html");
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);

            // Hent brugeren fra databasen
            User user = UserMapper.getUserByEmailAndPassword(email, password, connectionPool);
            if (user == null) {
                ctx.attribute("message", "Din e-mail og kodeord stemmer ikke overens");
                ctx.render("status.html");
                return;
            }

            // Hent ordren fra databasen
            Order order = OrderMapper.getOrderByIdAndUserId(orderId, user.getUserId(), connectionPool);
            ctx.sessionAttribute("orderId", orderId);
            if (order == null) {
                ctx.attribute("message", "Ud fra dine loginoplysninger samt ordrenummer, kunne vi ikke finde din ordre med følgende ordrenummer: " + orderId + ". Prøv igen og tjek evt. din e-mail, hvor du kan se dit ordrenummer.");
                ctx.render("status.html");
                return;
            }

            // Hent status-id'et fra ordren
            int statusId = order.getStatusId();

            // Switch-case på status-id'et
            switch (statusId) {
                case 2:
                    ctx.redirect("/confirm-offer");
                    break;
                case 3:
                    ctx.redirect("/confirm-newcarport");
                    break;
                default:
                    ctx.redirect("/statusUnknown.html");
                    break;
            }
        } catch (NumberFormatException e) {
            ctx.attribute("message", "Invalid 'orderId' parameter");
            ctx.render("status.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("status.html");
        }
    }

    private static void offerConfirmed(Context ctx, ConnectionPool connectionPool) {
        try {
            // Hent orderId fra sessionen
            Integer orderId = ctx.sessionAttribute("orderId");
            if (orderId == null) {
                System.out.println("No 'orderId' found in session"); // Logudskrift
                ctx.attribute("message", "No 'orderId' found in session");
                ctx.render("/confirm-offer.html");
                return;
            }

            // Opdater status-ID'et for den pågældende ordre
            OrderMapper.updateOrderStatusById(orderId, 5, connectionPool);

            // Hent ordren fra databasen
            Order order = OrderMapper.getOrderById(orderId, connectionPool);

            // Send mail til brugeren om at ordren er bekræftet
            MailController.paymentConfirmed(order);

            ctx.redirect("/orderdone");
        } catch (DatabaseException e) {
            System.out.println("Error updating order status: " + e.getMessage()); // Logudskrift
            ctx.attribute("message", "Error updating order status: " + e.getMessage());
            ctx.render("confirm-offer.html");
        }
    }



    private static void newCarportConfirmed(Context ctx, ConnectionPool connectionPool) {
        try {
            // Hent orderId fra sessionen
            Integer orderId = ctx.sessionAttribute("orderId");
            if (orderId == null) {
                System.out.println("No 'orderId' found in session"); // Logudskrift
                ctx.attribute("message", "No 'orderId' found in session");
                ctx.render("/confirm-newcarport.html");
                return;
            }

            // Opdater status-ID'et for den pågældende ordre
            OrderMapper.updateOrderStatusById(orderId, 1, connectionPool);

            ctx.render("newcarport-confirmation.html");
        } catch (DatabaseException e) {
            System.out.println("Error updating order status: " + e.getMessage()); // Logudskrift
            ctx.attribute("message", "Error updating order status: " + e.getMessage());
            ctx.render("confirm-newcarport.html");
        }
    }

}
