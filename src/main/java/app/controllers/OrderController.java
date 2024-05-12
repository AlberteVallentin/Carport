package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import static app.controllers.UserController.contactDetails;


public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportorder", ctx -> carportOrder(ctx, connectionPool));
        app.post("/savecarportdetails", ctx -> saveCarportDetails(ctx, connectionPool));
        app.get("/backtoorder", ctx -> backToOrder(ctx, connectionPool));
        app.post("/confirmorder", ctx -> confirmOrder(ctx, connectionPool));

    }

    private static void confirmOrder(Context ctx, ConnectionPool connectionPool) {
        Order order = ctx.sessionAttribute("Order");
        User user = ctx.sessionAttribute("currentUser");
        // Save the order to the database
        //OrderMapper.createOrder(order, connectionPool);
        // Send an order confirmation email
        //MailController.sendOrderConfirmation(order);
        // Set session attributes to null
        ctx.sessionAttribute("Order", null);
        ctx.sessionAttribute("currentWidth",null);
        ctx.sessionAttribute("currentLength",null);
        ctx.sessionAttribute("currentRoof",null);
        ctx.sessionAttribute("currentShedWidth",null);
        ctx.sessionAttribute("currentShedLength",null);
        ctx.sessionAttribute("currentComment",null);
        ctx.sessionAttribute("hasAnOrder", null);
        ctx.sessionAttribute("isOrdering", null);
        ctx.render("order-confirmation.html");
    }

    private static void backToOrder(Context ctx, ConnectionPool connectionPool) {
        ctx.render("carport-order.html");
    }

    private static void carportOrder(Context ctx, ConnectionPool connectionPool) {
        ctx.sessionAttribute("isOrdering", true);
        ctx.render("carport-order.html"); // Redirect to the carport order page

    }

    private static void saveCarportDetails(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        // Retrieve form parameters
        int cpWidth = Integer.parseInt(ctx.formParam("carport-width"));
        int cpLength = Integer.parseInt(ctx.formParam("carport-length"));
        String cpRoof = ctx.formParam("carport-roof");
        int shWidth = Integer.parseInt(ctx.formParam("shed-width"));
        int shLength = Integer.parseInt(ctx.formParam("shed-length"));
        String comment = ctx.formParam("comment");


        // Create an Order object to store the form values
        Order order = new Order(user, comment, cpLength, cpWidth, cpRoof, shLength, shWidth);
        // Set session attribute to store the order
        ctx.sessionAttribute("Order", order);
        ctx.sessionAttribute("currentWidth",cpWidth);
        ctx.sessionAttribute("currentLength",cpLength);
        ctx.sessionAttribute("currentRoof",cpRoof);
        ctx.sessionAttribute("currentShedWidth",shWidth);
        ctx.sessionAttribute("currentShedLength",shLength);
        ctx.sessionAttribute("currentComment",comment);
        ctx.sessionAttribute("isOrdering", null);
        ctx.sessionAttribute("hasAnOrder", true);

        contactDetails(ctx, connectionPool);

    }


}