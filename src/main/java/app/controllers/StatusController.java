package app.controllers;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;

public class StatusController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/status", ctx -> ctx.render("status.html"));
        app.post("/status", ctx -> statusRedirect(ctx, connectionPool));
    }

    private static void statusRedirect(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            Order order = OrderMapper.getOrderById(orderId, connectionPool);
            int statusId = order.getStatusId();

            switch (statusId) {
                case 1:
                    ctx.redirect("/status1.html");
                    break;
                case 2:
                    ctx.redirect("/status2.html");
                    break;
                case 3:
                    ctx.redirect("/status3.html");
                    break;
                default:
                    ctx.redirect("/statusUnknown.html");
                    break;
            }
        } catch (SQLException | DatabaseException e) {
            ctx.status(500);
            ctx.result("Der er sket en fejl ved hentning af ordren: " + e.getMessage());
        }
    }
}
