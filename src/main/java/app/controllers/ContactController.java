package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ContactController {


    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/contactdetails", ctx -> contactDetails(ctx, connectionPool));
        app.get("/backtoorder", ctx -> backToOrder(ctx, connectionPool));
        app.post("/backtoorder", ctx -> backToOrder(ctx, connectionPool));
    }

    private static void backToOrder(Context ctx, ConnectionPool connectionPool) {

            ctx.redirect("/carportorder");
    }

    private static void contactDetails(Context ctx, ConnectionPool connectionPool) {
        ctx.render("contact-details.html");
    }
}
