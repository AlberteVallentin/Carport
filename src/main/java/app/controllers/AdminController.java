package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.*;
import app.utility.Calculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    /**
     * Adds routes to the Javalin application for various admin functionalities.
     *
     * @param app            The Javalin application instance.
     * @param connectionPool The connection pool for database connections.
     */
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        // Define routes for admin operations
        app.get("/adminpage", ctx -> viewOrders(ctx, connectionPool));
        app.get("/admin-order", ctx -> ctx.render("admin-order.html"));
        //app.post("/admin-order", ctx -> viewOrders(ctx, connectionPool));
        app.post("/showorder", ctx -> showOrder(ctx, connectionPool));
        //app.get("/showorder", ctx -> showOrder(ctx, connectionPool));
        app.post("/changeorder", ctx -> changeOrder(ctx, connectionPool));
        app.post("/nonewoffer", ctx -> noNewOffer(ctx, connectionPool));
        app.post("/admindeleteorder", ctx -> adminDeleteOrder(ctx, connectionPool));
        app.post("/sendoffer", ctx -> sendOffer(ctx, connectionPool));
        app.get("/materials", ctx -> displayMaterialPage(ctx, connectionPool));
        app.post("/creatematerial", ctx -> addMaterial(ctx, connectionPool));
        app.post("/deletematerial", ctx -> deleteMaterial(ctx, connectionPool));
        app.post("/changematerial", ctx -> changeMaterial(ctx, connectionPool));
    }

    /**
     * Sends a new offer to the customer based on the order details.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void sendOffer(Context ctx, ConnectionPool connectionPool) {
        // Extract form parameters from the request
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        double newPrice = Double.parseDouble(ctx.formParam("price"));
        double originalPrice = Double.parseDouble(ctx.formParam("originalPrice"));

        try {
            // Retrieve order details and shipping rate from the database
            Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
            double shippingRate = ShippingMapper.getShippingRate(order.getShippingId(), connectionPool);

            System.out.println("OrderId: " + orderId);

            if (newPrice != originalPrice) {
                // Update the order price if it has changed and send a new offer email to the customer
                OrderMapper.updatePriceByOrderId(orderId, newPrice, connectionPool);
                OrderMapper.updateOrderStatusById(orderId, 2, connectionPool);
                MailController.sendNewOffer(order, orderId, shippingRate, newPrice);

                // Set a session attribute to show a message to the admin and redirect to the admin page
                ctx.sessionAttribute("message", "Det nye tilbud er sendt til kunden");
                ctx.redirect("/adminpage");
            } else {
                // If the price hasn't changed, just send the existing offer email
                OrderMapper.updateOrderStatusById(orderId, 2, connectionPool);
                MailController.sendOffer(order, orderId, shippingRate, originalPrice);

                // Set a session attribute to show a message to the admin and redirect to the admin page
                ctx.sessionAttribute("message", "Tilbuddet er sendt til kunden");
                ctx.redirect("/adminpage");
            }

        } catch (DatabaseException | SQLException e) {
            // Handle exceptions by rethrowing as runtime exceptions
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes an order from the database.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void adminDeleteOrder(Context ctx, ConnectionPool connectionPool) {
        // Extract the order ID from the form parameters
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        try {
            // Delete related bill of material lines and the order itself from the database
            OrderMapper.deleteBillOfMaterialLinesByOrderId(orderId, connectionPool);
            OrderMapper.deleteOrder(orderId, connectionPool);

            // Set a session attribute to show a message to the admin and redirect to the admin page
            ctx.sessionAttribute("message", "Ordren er blevet slettet");
            ctx.redirect("/adminpage");
        } catch (DatabaseException e) {
            // Handle exceptions by rethrowing as runtime exceptions
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles cases where no new offer is made and informs the customer.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void noNewOffer(Context ctx, ConnectionPool connectionPool) {
        // Extract the order ID from the form parameters
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        try {
            // Retrieve order details from the database
            Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
            // Update the order status and send an email to the customer
            OrderMapper.updateOrderStatusById(orderId, 2, connectionPool);
            double shippingRate = ShippingMapper.getShippingRate(order.getShippingId(), connectionPool);
            MailController.denyNewOffer(order, order.getOrderId(), shippingRate);

            // Set a session attribute to show a message to the admin and redirect to the admin page
            ctx.sessionAttribute("message", "Der er sendt en mail til kunden om, at der ikke er lavet et nyt tilbud");
            ctx.redirect("/adminpage");
        } catch (DatabaseException | SQLException e) {
            // Handle exceptions by rethrowing as runtime exceptions
            throw new RuntimeException(e);
        }
    }

    /**
     * Changes the order details and updates the order in the database.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void changeOrder(Context ctx, ConnectionPool connectionPool) {
        // Extract form parameters from the request
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        String cpWidthStr = ctx.formParam("cpWidth");
        String cpLengthStr = ctx.formParam("cpLength");
        String shWidthStr = ctx.formParam("shWidth");
        String shLengthStr = ctx.formParam("shLength");
        String cpRoof = ctx.formParam("cpRoof");

        try {
            // Retrieve the existing order details from the database
            Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
            // Parse and update order details, using existing values if parameters are null
            int cpWidth = cpWidthStr != null ? Integer.parseInt(cpWidthStr) : order.getCpWidth();
            int cpLength = cpLengthStr != null ? Integer.parseInt(cpLengthStr) : order.getCpLength();
            int shWidth = shWidthStr != null ? Integer.parseInt(shWidthStr) : order.getShWidth();
            int shLength = shLengthStr != null ? Integer.parseInt(shLengthStr) : order.getShLength();
            cpRoof = cpRoof != null ? cpRoof : order.getCpRoof();

            // Update the order in the database with the new values
            AdminMapper.updateOrder(orderId, cpWidth, cpLength, shWidth, shLength, cpRoof, connectionPool);

            // Update the order instance with the new values
            order.setCpWidth(cpWidth);
            order.setCpLength(cpLength);
            order.setShWidth(shWidth);
            order.setShLength(shLength);
            order.setCpRoof(cpRoof);

            // Update the status ID for the order
            OrderMapper.updateOrderStatusById(orderId, 3, connectionPool);

            // Send a modified order email with the updated order
            MailController.sendModifiedOrder(order, order.getOrderId());

            // Set a session attribute to show a message to the admin and redirect to the admin page
            ctx.sessionAttribute("message", "Ordren er blevet ændret, og der er sendt en mail til kunden");
            ctx.redirect("/adminpage");
        } catch (DatabaseException e) {
            // Handle exceptions by rethrowing as runtime exceptions
            throw new RuntimeException(e);
        }
    }

    /**
     * Displays the details of a specific order.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     * @throws SQLException      If a SQL error occurs.
     */
    private static void showOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException, SQLException {
        // Extract the order ID from the form parameters
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        // Retrieve order details and related information from the database
        Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
        int postalCode = AddressMapper.getAddressById(order.getUser().getAddressId(), connectionPool).getPostalCode();
        double shippingRate = ShippingMapper.getShippingRate(order.getShippingId(), connectionPool);
        System.out.println(shippingRate);

        // Add order details and related attributes to the context
        ctx.attribute("order", order);
        ctx.attribute("orderId", order.getOrderId());
        ctx.attribute("firstName", order.getUser().getFirstName());
        ctx.attribute("lastName", order.getUser().getLastName());
        ctx.attribute("email", order.getUser().getEmail());
        ctx.attribute("status", order.getStatus());
        ctx.attribute("cpLength", order.getCpLength());
        ctx.attribute("cpWidth", order.getCpWidth());
        ctx.attribute("cpRoof", order.getCpRoof());
        ctx.attribute("shLength", order.getShLength());
        ctx.attribute("shWidth", order.getShWidth());
        ctx.attribute("price", order.getPrice() + shippingRate);
        ctx.attribute("comment", order.getComment());
        ctx.attribute("postalCode", postalCode);
        ctx.attribute("shippingRate", shippingRate);
        ctx.attribute("materialPrice", order.getPrice());

        // Calculate the Bill of Materials (BOM) for the carport
        Calculator calculator = new Calculator(order.getCpWidth(), order.getCpLength(), connectionPool);
        calculator.calcCarport(order);

        // Retrieve the BOM lines and add them to the context
        List<BillOfMaterialLine> bomLines = calculator.getBomLine();
        ctx.attribute("bomLines", bomLines);

        // Render the order details page
        ctx.render("admin-order.html");
    }

    /**
     * Retrieves and displays the list of orders.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    static void viewOrders(Context ctx, ConnectionPool connectionPool) {
        // Retrieve the status ID from the query parameters
        String statusIdString = ctx.queryParam("statusId");
        List<Order> orderList = null;

        try {
            if (statusIdString == null)
                statusIdString = "0"; // Default to status ID 0 if not provided
            int statusId = Integer.parseInt(statusIdString);

            // Retrieve orders based on the status ID
            if (statusId == 0)
                orderList = AdminMapper.getAllOrders(connectionPool);
            else
                orderList = AdminMapper.getOrderByStatus(statusId, connectionPool);

            // Get the session attribute for messages
            String message = ctx.sessionAttribute("message");

            // Remove the session attribute after retrieving it
            ctx.sessionAttribute("message", null);

            // Add the list of orders and the message to the context
            ctx.attribute("orderList", orderList);
            ctx.attribute("message", message);

            // Render the admin page with the orders
            ctx.render("adminpage.html");
        } catch (NumberFormatException | DatabaseException e) {
            // Handle exceptions by rethrowing as runtime exceptions
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all users from the database.
     *
     * @param connectionPool The connection pool for database connections.
     * @return A list of all users.
     */
    public static List<User> getAllUsers(ConnectionPool connectionPool) {
        // SQL query to select all users
        String sql = "SELECT * FROM users";
        List<User> userList = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            // Execute the query and process the result set
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String password = rs.getString("password");
                String email = rs.getString("email");
                boolean admin = rs.getBoolean("admin");
                int addressId = rs.getInt("address");
                User user = new User(userId, firstName, lastName, phoneNumber, password, email, admin, addressId);
                userList.add(user);
            }
        } catch (SQLException e) {
            // Handle exceptions by rethrowing as runtime exceptions
            throw new RuntimeException(e);
        }
        return userList;
    }

    /**
     * Adds a new material to the database.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
    public static void addMaterial(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        // SQL queries to insert a new material and material variant
        String sql = "INSERT INTO material (width, depth, type, material_price, unit, material_description) VALUES (?, ?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO material_variant (length, material_id) VALUES (?, ?)";
        String sqlGetMaterialId = "SELECT material_id FROM material_variant ORDER BY material_id DESC LIMIT 1";

        // Extract form parameters from the request
        int width = Integer.parseInt(ctx.formParam("width"));
        int depth = Integer.parseInt(ctx.formParam("depth"));
        String type = ctx.formParam("type");
        int materialPrice = Integer.parseInt(ctx.formParam("price"));
        String unit = ctx.formParam("unit");
        String materialDescription = ctx.formParam("material_description");
        int length = Integer.parseInt(ctx.formParam("length"));
        int materialId = 0;

        // Insert new material into the material table
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, width);
            preparedStatement.setInt(2, depth);
            preparedStatement.setString(3, type);
            preparedStatement.setInt(4, materialPrice);
            preparedStatement.setString(5, unit);
            preparedStatement.setString(6, materialDescription);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Proper error handling should be implemented
        }

        // Retrieve the latest material ID
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sqlGetMaterialId);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                materialId = rs.getInt("material_id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving the latest material ID", e.getMessage());
        }

        // Insert new material variant into the material_variant table
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql2)) {

            preparedStatement.setInt(1, length);
            preparedStatement.setInt(2, materialId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Proper error handling should be implemented
        }

        // Display the materials page after adding the new material
        displayMaterialPage(ctx, connectionPool);
        ctx.render("admin-materials.html");
    }

    /**
     * Displays the materials page with all materials.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    public static void displayMaterialPage(Context ctx, ConnectionPool connectionPool) {
        // Retrieve all materials from the database
        List<Material> materials = MaterialMapper.getAllMaterials(connectionPool);

        // Add the list of materials to the context
        ctx.attribute("materials", materials);

        // Render the materials page
        ctx.render("admin-materials.html");
    }

    /**
     * Deletes a material from the database.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    public static void deleteMaterial(Context ctx, ConnectionPool connectionPool) {
        // Extract the material ID from the form parameters
        int materialId = Integer.parseInt(ctx.formParam("deleteId"));
        String sql = "DELETE FROM material WHERE material_id = ?";
        System.out.println(materialId);

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set the material ID parameter and execute the delete query
            preparedStatement.setInt(1, materialId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Proper error handling should be implemented
        }

        // Display the materials page after deleting the material
        displayMaterialPage(ctx, connectionPool);
    }

    /**
     * Updates a material in the database.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     * @throws DatabaseException If a database error occurs.
     */
    public static void changeMaterial(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        // SQL query to update a material
        String sql = "UPDATE material SET width = ?, depth = ?, type = ?, material_price = ?, unit = ?, material_description = ? WHERE material_id = ?";

        try {
            // Fetching parameters from the form
            int materialId = Integer.parseInt(ctx.formParam("material_IdC"));
            int width = Integer.parseInt(ctx.formParam("widthC"));
            int depth = Integer.parseInt(ctx.formParam("depthC"));
            String type = ctx.formParam("typeC");
            int materialPrice = Integer.parseInt(ctx.formParam("priceC"));
            String unit = ctx.formParam("unitC");
            String materialDescription = ctx.formParam("material_descriptionC");

            try (Connection connection = connectionPool.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                // Setting the parameters for the SQL query
                preparedStatement.setInt(1, width);
                preparedStatement.setInt(2, depth);
                preparedStatement.setString(3, type);
                preparedStatement.setInt(4, materialPrice);
                preparedStatement.setString(5, unit);
                preparedStatement.setString(6, materialDescription);
                preparedStatement.setInt(7, materialId);  // Set materialId in the WHERE clause

                // Executing the update query
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Logging the error
            ctx.status(500).result("An error occurred: " + e.getMessage());
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Logging the error for invalid number format
            ctx.status(400).result("Invalid input: " + e.getMessage());
        }

        // Display the materials page after updating the material
        displayMaterialPage(ctx, connectionPool);
    }
}
