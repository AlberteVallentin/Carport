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
    }

    private static void statusRedirect(Context ctx, ConnectionPool connectionPool) {
        try {
            String orderIdStr = ctx.formParam("order-id");
            if (orderIdStr == null || orderIdStr.isEmpty()) {
                ctx.attribute("message", "Missing or empty 'orderId' parameter");
                ctx.render("status.html");
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);
            int statusId = OrderMapper.getOrderStatusByOrderId(orderId, connectionPool);

            switch (statusId) {
                case 1:
                    ctx.redirect("/status1.html");
                    break;
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

