package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Objects;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportorder", ctx -> ctx.render("carport-order.html"));
        app.get("/savecarportdetails", ctx -> saveCarportDetails(ctx, connectionPool));

    }

    private static void saveCarportDetails(Context ctx, ConnectionPool connectionPool) {
        int carportWidth = Integer.parseInt(Objects.requireNonNull(ctx.formParam("carport-width")));
        int carportLength = Integer.parseInt(Objects.requireNonNull(ctx.formParam("carport-length")));
        String carportRoof = ctx.formParam("carport-roof");
        int shedWidth = Integer.parseInt(Objects.requireNonNull(ctx.formParam("shed-width")));
        int shedLength = Integer.parseInt(Objects.requireNonNull(ctx.formParam("shed-length")));
        String comment = ctx.formParam("comment");

        // Hent currentUser fra sessionen
        User currentUser = ctx.sessionAttribute("currentUser");

        // Opret en ny Order objekt med den indtastede data
        Order order = new Order(0, 0, currentUser, comment, 0, carportLength, carportWidth , carportRoof, shedLength, shedWidth, 0);

        // Gem Order objektet i sessionen
        ctx.sessionAttribute("order", order);

        // Redirect brugeren til næste side
        ctx.redirect("/contact-details");
    }
}
