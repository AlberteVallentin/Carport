package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.AddressMapper;
import app.persistence.ConnectionPool;
import app.persistence.ShippingMapper;
import app.persistence.UserMapper;
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

    }

    private static void confirmOrder(Context ctx, ConnectionPool connectionPool) {
        Order order = ctx.sessionAttribute("currentOrder");
        User user = ctx.sessionAttribute("currentUser");

        // Forsøg at oprette ordren og shipping i databasen
        try {
            int shippingId = ShippingMapper.createShipping(user.getAddressId(), connectionPool);
            UserMapper.createUser(firstName, lastName, email, phone, password1, addressId, connectionPool);
            ctx.attribute("message", "Du er nu oprettet med e-mailen: " + email + ". Log på.");
            ctx.render("login.html");
        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getSQLState().equals("23505")) {
                msg = "E-mailen findes allerede. Vælg en anden e-mail eller log ind";
            }
            handleDatabaseError(ctx, new DatabaseException(msg, e.getMessage()), firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCode, city);
        } catch (DatabaseException e) {
            handleDatabaseError(ctx, e, firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCode, city);
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


}