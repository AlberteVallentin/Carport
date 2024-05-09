package app.controllers;

import app.entities.Order;
import app.entities.OrderLine;
import app.entities.User;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportorder", ctx -> carportOrder(ctx, connectionPool));
        app.post("/orderdetails", ctx -> orderDetails(ctx, connectionPool));
    }

    private static void carportOrder(Context ctx, ConnectionPool connectionPool) {


            ctx.render("carport-order.html");


    }

    private static void orderDetails(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");

        try {
        // Retrieve form parameters
        int cpWidth = Integer.parseInt(ctx.formParam("carport-width"));
        int cpLength = Integer.parseInt(ctx.formParam("carport-length"));
        String cpRoof = ctx.formParam("carport-roof");
        int shWidth = Integer.parseInt(ctx.formParam("shed-width"));
        int shLength = Integer.parseInt(ctx.formParam("shed-length"));
        String comment = ctx.formParam("comment");



        Order order = ctx.sessionAttribute("Order");

        if(order == null) {
            order = new Order(0, 0.0, 0,"" , 0, 0, 0, 0, 0, 0);
        }

            order = new Order(0, 0.0, 0, comment, 0, cpLength, cpWidth, shLength, shWidth, 0);


            ctx.sessionAttribute("Order", order);
        } catch (NumberFormatException e) {
            ctx.attribute("message", "Ugyldigt format for top, bund eller antal.");
        }



        ctx.render("contact-details.html");
    }



}
