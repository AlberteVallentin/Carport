package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.*;
import app.utility.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;

import static app.controllers.UserController.contactDetails;


public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carportorder", ctx -> carportOrder(ctx, connectionPool));
        app.post("/savecarportdetails", ctx -> saveCarportDetails(ctx, connectionPool));
        app.get("/backtoorder", ctx -> backToOrder(ctx, connectionPool));
        app.post("/confirmorder", ctx -> confirmOrder(ctx, connectionPool));
        app.post("/deleteorder", ctx -> deleteOrder(ctx, connectionPool));
        app.get("/deleteorder", ctx -> ctx.render("order-deleted.html"));

    }

    private static void deleteOrder(Context ctx, ConnectionPool connectionPool) {
        try {
            // Hent orderId fra sessionen
            Integer orderId = ctx.sessionAttribute("orderId");
            if (orderId == null) {
                System.out.println("No 'orderId' found in session"); // Logudskrift
                ctx.attribute("message", "No 'orderId' found in session");
                ctx.render("/confirm-offer.html");
                return;
            }

            // Opdater status-ID'et for den pågældende ordre
            OrderMapper.deleteBillOfMaterialLinesByOrderId(orderId, connectionPool);
            OrderMapper.deleteOrder(orderId, connectionPool);

            ctx.redirect("/deleteorder");
        } catch (DatabaseException e) {
            System.out.println("Error updating order status: " + e.getMessage()); // Logudskrift
            ctx.attribute("message", "Error updating order status: " + e.getMessage());
            ctx.render("confirm-offer.html");
        }
    }


    private static void confirmOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        Order order = ctx.sessionAttribute("currentOrder");
        User user = ctx.sessionAttribute("currentUser");
        int cpWidth=ctx.sessionAttribute("currentWidth");
        int cpLength= ctx.sessionAttribute("currentLength");


//
        // Forsøg at oprette ordren og shipping i databasen
        try {
            int shippingId = ShippingMapper.createShipping(user.getAddressId(), connectionPool);
            OrderMapper.createOrder(order, user, shippingId, connectionPool);

            Calculator calculator = new Calculator(cpWidth,cpLength,connectionPool);
            calculator.calcCarport(order);
            OrderMapper.createBomLine(calculator.getBomLine(),connectionPool);
            int orderId = OrderMapper.getLastOrder(connectionPool);

            MailController.sendOrderConfirmation(order, user, orderId);
            ctx.render("order-confirmation.html");
        } catch (SQLException | DatabaseException e) {
            ctx.status(500);
            ctx.result("Der er sket en fejl ved oprettelse af ordren: " + e.getMessage());
        }
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
        ctx.sessionAttribute("currentOrder", order);
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

// private static void sendRequest(Context ctx, ConnectionPool connectionPool)
//  {
//        // Get order details from front-end
//        int width = ctx.sessionAttribute("width");
//       int length = ctx.sessionAttribute("length");
//       int status = 1; // hardcoded for now
//       int totalPrice = 19999; // hardcoded for now
//
//     // hardcoded for now
//     Order order = new Order(0, totalPrice, user, "tada", sh, length, width,"no roof", 600,600,5);
//
//        // TODO: Insert order in database
//        try
//        {
//            order = OrderMapper.insertOrder(order, connectionPool);
//
//            // TODO: Calculate order items (stykliste)
//            Calculator calculator = new Calculator(width, length, connectionPool);
//            calculator.calcCarport(order);
//
//            // TODO: Save order items in database (stykliste)
//            OrderMapper.insertOrderItems(calculator.getBomLine(), connectionPool);
//
//            // TODO: Create message to customer and render order / request confirmation
//
//            ctx.render("orderflow/requestconfirmation.html");
//        }
//        catch (DatabaseException e)
//        {
//            // TODO: handle exception later
//            throw new RuntimeException(e);
//        }
//   }


}