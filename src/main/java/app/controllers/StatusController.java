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
            int orderId = Integer.parseInt(ctx.formParam("order-id"));
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
        } catch (DatabaseException e) {
            ctx.status(500);
            ctx.result("Der er sket en fejl ved hentning af ordren: " + e.getMessage());
        }
    }
}
