package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportdetails", ctx -> saveCarportDetails(ctx, connectionPool));

    }

    private static void saveCarportDetails(Context ctx, ConnectionPool connectionPool) {
        String carportWidth = ctx.formParam("carport-width");
        String carportLength = ctx.formParam("carport-length");
        String carportRoof = ctx.formParam("carport-roof");
        String shedWidth = ctx.formParam("shed-width");
        String shedLength = ctx.formParam("shed-length");
        String comment = ctx.formParam("comment");

        // Hent currentUser fra sessionen
        User currentUser = ctx.sessionAttribute("currentUser");

        // Opret en ny Order objekt med den indtastede data
        Order order = new Order(0, 0, currentUser, comment, 0, carportLength, carportWidth , carportRoof, shedWidth, shedLength, comment, currentUser);

        // Gem Order objektet i sessionen
        ctx.sessionAttribute("order", order);

        // Redirect brugeren til næste side
        ctx.redirect("/contact-details");
    }
}
