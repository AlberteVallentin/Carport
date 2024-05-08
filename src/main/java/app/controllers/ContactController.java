package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ContactController {


    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
       app.get("/contactDetails", ctx -> contactDetails(ctx, connectionPool));
       app.get("/backToOrder", ctx -> backToOrder(ctx, connectionPool));
    }

    private static void backToOrder(Context ctx, ConnectionPool connectionPool) {

        ctx.redirect("carport-order.html");
    }

    private static void contactDetails(Context ctx, ConnectionPool connectionPool) {
        ctx.render("contact-details.html");
    }
}
