package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ContactController {


    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
       app.get("/backtoorder", ctx -> backToOrder(ctx, connectionPool));
    }

    private static void backToOrder(Context ctx, ConnectionPool connectionPool) {

        ctx.render("carport-order.html");
    }

}
