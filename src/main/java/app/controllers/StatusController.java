package app.controllers;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;

public class StatusController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/status", ctx -> ctx.render("status.html"));
        app.post("/statusRedirect", ctx -> statusRedirect(ctx, connectionPool));
        app.get("/confirm-offer", ctx -> ctx.render("confirm-offer.html"));
        app.post("/offerconfirmed", ctx -> offerConfirmed(ctx, connectionPool));
        app.get("/orderdone", ctx -> ctx.render("order-done.html"));

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

            // TODO: Hent ordren fra databasen
            // Hent ordren fra databasen
            //Order order = OrderMapper.getOrderById(orderId, connectionPool);

            // TODO: Send en bekræftelsesmail
            // Send en bekræftelsesmail
            //MailController.paymentConfirmed(order);

            ctx.redirect("/orderdone");
        } catch (DatabaseException e) {
            System.out.println("Error updating order status: " + e.getMessage()); // Logudskrift
            ctx.attribute("message", "Error updating order status: " + e.getMessage());
            ctx.render("confirm-offer.html");
        }
    }



    private static void statusRedirect(Context ctx, ConnectionPool connectionPool){
            try {
                String orderIdStr = ctx.formParam("order-id");
                if (orderIdStr == null || orderIdStr.isEmpty()) {
                    ctx.attribute("message", "Missing or empty 'orderId' parameter");
                    ctx.render("status.html");
                    return;
                }

            int orderId = Integer.parseInt(orderIdStr);
            int statusId = OrderMapper.getOrderStatusByOrderId(orderId, connectionPool);
            ctx.sessionAttribute("orderId", orderId);

            switch (statusId) {
                case 2:
                    ctx.redirect("/confirm-offer");
                    break;
                case 3:
                    ctx.redirect("/status3.html");
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
}

