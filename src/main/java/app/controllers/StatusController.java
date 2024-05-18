package app.controllers;

import app.entities.BillOfMaterialLine;
import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import app.utility.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;

public class StatusController {

    /**
     * Adds routes to the Javalin application for various status-related functionalities.
     *
     * @param app            The Javalin application instance.
     * @param connectionPool The connection pool for database connections.
     */
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        // Define routes for status operations
        app.get("/status", ctx -> ctx.render("status.html"));
        app.post("/statusRedirect", ctx -> statusRedirect(ctx, connectionPool));
        app.get("/confirm-offer", ctx -> ctx.render("confirm-offer.html"));
        app.get("/confirm-newcarport", ctx -> ctx.render("confirm-newcarport.html"));
        app.post("/newcarportconfirmed", ctx -> newCarportConfirmed(ctx, connectionPool));
        app.post("/offerconfirmed", ctx -> offerConfirmed(ctx, connectionPool));
        app.get("/orderdone", ctx -> orderDone(ctx, connectionPool));
        app.get("/statusRedirect", ctx -> statusRedirect(ctx, connectionPool));
    }

    /**
     * Redirects the user based on the order status.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void statusRedirect(Context ctx, ConnectionPool connectionPool) {
        try {
            // Retrieve form parameters from the request
            String email = ctx.formParam("email");
            String password = ctx.formParam("password");
            String orderIdStr = ctx.formParam("order-id");

            // Validate input fields
            if (orderIdStr == null || orderIdStr.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
                ctx.attribute("message", "Udfyld venligst alle felter");
                ctx.render("status.html");
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);

            // Retrieve the user from the database
            User user = UserMapper.getUserByEmailAndPassword(email, password, connectionPool);
            if (user == null) {
                ctx.attribute("message", "Din e-mail og kodeord stemmer ikke overens");
                ctx.render("status.html");
                return;
            }

            // Retrieve the order from the database
            Order order = OrderMapper.getOrderByIdAndUserId(orderId, user.getUserId(), connectionPool);
            ctx.sessionAttribute("orderId", orderId);
            if (order == null) {
                ctx.attribute("message", "Ud fra dine loginoplysninger samt ordrenummer, kunne vi ikke finde din ordre med følgende ordrenummer: " + orderId + ". Prøv igen og tjek evt. din e-mail, hvor du kan se dit ordrenummer.");
                ctx.render("status.html");
                return;
            }

            // Retrieve the status ID from the order
            int statusId = order.getStatusId();

            // Redirect based on the status ID
            switch (statusId) {
                case 2:
                    ctx.redirect("/confirm-offer");
                    break;
                case 3:
                    ctx.redirect("/confirm-newcarport");
                    break;
                default:
                    ctx.redirect("/status.html");
                    break;
            }
        } catch (NumberFormatException e) {
            ctx.attribute("message", "Invalid 'orderId' parameter");
            ctx.render("status.html");
        } catch (DatabaseException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("status.html");
        }
    }

    /**
     * Confirms the offer for an order and updates the order status.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void offerConfirmed(Context ctx, ConnectionPool connectionPool) {
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

            // Update the status ID for the order
            OrderMapper.updateOrderStatusById(orderId, 5, connectionPool);

            // Retrieve the order from the database
            Order order = OrderMapper.getOrderById(orderId, connectionPool);

            // Send email to the user confirming the order
            MailController.paymentConfirmed(order);

            // Redirect to the order done page
            ctx.redirect("/orderdone");
        } catch (DatabaseException e) {
            // Log error if order status update fails
            System.out.println("Error updating order status: " + e.getMessage());
            ctx.attribute("message", "Error updating order status: " + e.getMessage());
            ctx.render("confirm-offer.html");
        }
    }

    /**
     * Confirms the creation of a new carport and updates the order status.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void newCarportConfirmed(Context ctx, ConnectionPool connectionPool) {
        try {
            // Retrieve orderId from the session
            Integer orderId = ctx.sessionAttribute("orderId");
            if (orderId == null) {
                // Log error if orderId is not found in the session
                System.out.println("No 'orderId' found in session");
                ctx.attribute("message", "No 'orderId' found in session");
                ctx.render("/confirm-newcarport.html");
                return;
            }

            // Update the status ID for the order
            OrderMapper.updateOrderStatusById(orderId, 1, connectionPool);

            // Render the new carport confirmation page
            ctx.render("newcarport-confirmation.html");
        } catch (DatabaseException e) {
            // Log error if order status update fails
            System.out.println("Error updating order status: " + e.getMessage());
            ctx.attribute("message", "Error updating order status: " + e.getMessage());
            ctx.render("confirm-newcarport.html");
        }
    }

    /**
     * Displays the order completion page with the order and bill of materials.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void orderDone(Context ctx, ConnectionPool connectionPool) {
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

            // Retrieve the order from the database
            Order order = OrderMapper.getOrderById(orderId, connectionPool);

            // Calculate the bill of materials (BOM) for the carport
            Calculator calculator = new Calculator(order.getCpWidth(), order.getCpLength(), connectionPool);
            calculator.calcCarport(order);
            List<BillOfMaterialLine> bomLines = calculator.getBomLine();

            // Add the order and BOM lines to the model
            ctx.attribute("order", order);
            ctx.attribute("bomLines", bomLines);

            // Render the order completion page
            ctx.render("order-done.html");
        } catch (DatabaseException e) {
            // Log error if order or BOM retrieval fails
            System.out.println("Error retrieving order or bill of materials: " + e.getMessage());
            ctx.attribute("message", "Error retrieving order or bill of materials: " + e.getMessage());
            ctx.render("confirm-offer.html");
        }
    }
}
