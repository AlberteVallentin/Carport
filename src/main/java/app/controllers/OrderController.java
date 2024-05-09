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
        app.post("/contactdetails", ctx -> contactDetails(ctx, connectionPool));
    }

    private static void carportOrder(Context ctx, ConnectionPool connectionPool) {


            ctx.render("carport-order.html"); // Redirect to the carport order page

    }

    private static void contactDetails(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");


        // Retrieve form parameters
        int cpWidth = Integer.parseInt(ctx.formParam("carport-width"));
        int cpLength = Integer.parseInt(ctx.formParam("carport-length"));
        String cpRoof = ctx.formParam("carport-roof");
        int shWidth = Integer.parseInt(ctx.formParam("shed-width"));
        int shLength = Integer.parseInt(ctx.formParam("shed-length"));
        String comment = ctx.formParam("comment");


        // Create an Order object to store the form values
        Order order = new Order(0, 0.0, 0, comment, 0, cpLength, cpWidth, shLength, shWidth, 0);
        // Set session attribute to store the order
        ctx.sessionAttribute("Order", order);

        ctx.render("contact-details.html");
    }
private static void saveCarportSelection(Context ctx, ConnectionPool connectionPool) {

        String cpWidth = ctx.formParam("carport-width");
        String cpLength = ctx.formParam("carport-length");
        String cpRoof = ctx.formParam("carport-roof");
        String shWidth = ctx.formParam("shed-width");
        String shLength = ctx.formParam("shed-length");
        String comment = ctx.formParam("comment");


        ctx.sessionAttribute("currentWidth",cpWidth);
        ctx.sessionAttribute("currentLength",cpLength);
        ctx.sessionAttribute("currentRoof",cpRoof);
        ctx.sessionAttribute("currentShedWidth",shWidth);
        ctx.sessionAttribute("currentShedLength",shLength);
        ctx.sessionAttribute("currentComment",comment);


        ctx.render("carport-selection.html");

    }

}
