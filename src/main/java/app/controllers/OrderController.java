package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.ShippingMapper;
import app.utility.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;

import static app.Main.connectionPool;
import static app.controllers.UserController.contactDetails;

public class OrderController {

    /**
     * Adds routes to the Javalin application for various order-related functionalities.
     *
     * @param app            The Javalin application instance.
     * @param connectionPool The connection pool for database connections.
     */
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        // Define routes for order operations
        app.get("/carportorder", ctx -> carportOrder(ctx, connectionPool));
        app.post("/savecarportdetails", ctx -> saveCarportDetails(ctx));
        app.get("/backtoorder", ctx -> backToOrder(ctx, connectionPool));
        app.post("/confirmorder", ctx -> confirmOrder(ctx, connectionPool));
        app.post("/deleteorder", ctx -> deleteOrder(ctx, connectionPool));
        app.get("/deleteorder", ctx -> ctx.render("order-deleted.html"));
    }

    /**
     * Deletes an order from the database.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void deleteOrder(Context ctx, ConnectionPool connectionPool) {
        try {
            // Retrieve orderId from the session
            Integer orderId = ctx.sessionAttribute("orderId");
            if (orderId == null) {
                // Log error if orderId is not found in the session
                System.out.println("No 'orderId' found in session");
                ctx.attribute("message", "No 'orderId' found in session");
                ctx.render("/confirm-offer.html");
                return;
            }

            // Delete the order and its associated bill of material lines from the database
            OrderMapper.deleteBillOfMaterialLinesByOrderId(orderId, connectionPool);
            OrderMapper.deleteOrder(orderId, connectionPool);

            // Redirect to the delete order confirmation page
            ctx.redirect("/deleteorder");
        } catch (DatabaseException e) {
            // Log error if order deletion fails
            System.out.println("Error updating order status: " + e.getMessage());
            ctx.attribute("message", "Error updating order status: " + e.getMessage());
            ctx.render("confirm-offer.html");
        }
    }

    /**
     * Confirms an order and saves it to the database.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
    private static void confirmOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        // Retrieve order and user details from the session
        Order order = ctx.sessionAttribute("currentOrder");
        User user = ctx.sessionAttribute("currentUser");
        int cpWidth = ctx.sessionAttribute("currentWidth");
        int cpLength = ctx.sessionAttribute("currentLength");

        try {
            // Create a new shipping entry in the database
            assert user != null;
            int shippingId = ShippingMapper.createShipping(user.getAddressId(), connectionPool);

            // Insert the order into the database with initial price 0
            OrderMapper.createOrder(order, user, shippingId, 0, connectionPool);
            int orderId = OrderMapper.getLastOrder(connectionPool);

            // Update the order object with the new orderId
            order.setOrderId(orderId);

            // Calculate the bill of materials (BOM) for the carport
            Calculator calculator = new Calculator(cpWidth, cpLength, connectionPool);
            calculator.calcCarport(order);
            OrderMapper.createBomLine(calculator.getBomLine(), connectionPool);

            // Calculate total price and update the order in the database
            double materialPrice = calculator.getTotalMaterialPrice();
            double shippingRate = ShippingMapper.getShippingRate(shippingId, connectionPool);
            double totalPrice = materialPrice + shippingRate;
            OrderMapper.updatePriceByOrderId(orderId, totalPrice, connectionPool);

            // Send order confirmation email to the user
            MailController.sendOrderConfirmation(order, user, orderId);
            ctx.render("order-confirmation.html");
        } catch (SQLException | DatabaseException e) {
            // Set HTTP status to 500 and display error message if order confirmation fails
            ctx.status(500);
            ctx.result("Der er sket en fejl ved oprettelse af ordren: " + e.getMessage());
        }
    }

    /**
     * Renders the carport order page.
     *
     * @param ctx The Javalin context, which provides access to the request and response.
     */
    private static void backToOrder(Context ctx, ConnectionPool connectionPool) {
        // Render the carport order page
        ctx.render("carport-order.html");
    }

    /**
     * Starts the carport order process.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void carportOrder(Context ctx, ConnectionPool connectionPool) {
        // Set a session attribute to indicate the ordering process has started
        ctx.sessionAttribute("isOrdering", true);
        // Render the carport order page
        ctx.render("carport-order.html");
    }

    /**
     * Saves the carport details provided by the user and stores them in the session.
     *
     * @param ctx The Javalin context, which provides access to the request and response.
     */
    private static void saveCarportDetails(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        int cpWidth = Integer.parseInt(ctx.formParam("carport-width"));
        int cpLength = Integer.parseInt(ctx.formParam("carport-length"));
        String cpRoof = ctx.formParam("carport-roof");
        int shWidth = Integer.parseInt(ctx.formParam("shed-width"));
        int shLength = Integer.parseInt(ctx.formParam("shed-length"));
        String comment = ctx.formParam("comment");

        Order order = new Order(user, comment, cpLength, cpWidth, cpRoof, shLength, shWidth);
        ctx.sessionAttribute("currentOrder", order);
        ctx.sessionAttribute("currentWidth", cpWidth);
        ctx.sessionAttribute("currentLength", cpLength);
        ctx.sessionAttribute("currentRoof", cpRoof);
        ctx.sessionAttribute("currentShedWidth", shWidth);
        ctx.sessionAttribute("currentShedLength", shLength);
        ctx.sessionAttribute("currentComment", comment);
        ctx.sessionAttribute("isOrdering", null);
        ctx.sessionAttribute("hasAnOrder", true);

        ctx.redirect("/contactdetails");
    }
}