package app.controllers;

import app.entities.BillOfMaterialLine;
import app.entities.MaterialVariant;
import app.entities.Order;
import app.entities.User;
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
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/adminpage", ctx -> viewOrders(ctx, connectionPool));
        app.get("/admin-order", ctx -> ctx.render("admin-order.html"));
        //app.post("/admin-order", ctx -> viewOrders(ctx, connectionPool));
        app.post("/showorder", ctx -> showOrder(ctx, connectionPool));
        //app.get("/showorder",ctx -> showOrder(ctx, connectionPool));
        app.post("/changeorder", ctx -> changeOrder(ctx, connectionPool));
        app.post("/nonewoffer", ctx -> noNewOffer(ctx, connectionPool));
        app.post("/admindeleteorder", ctx -> adminDeleteOrder(ctx, connectionPool));
        app.post("/sendoffer", ctx -> sendOffer(ctx, connectionPool));
        app.get("/materials", ctx -> ctx.render("admin-materials.html"));




    }

    private static void sendOffer(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        double newPrice = Double.parseDouble(ctx.formParam("price"));
        double originalPrice = Double.parseDouble(ctx.formParam("originalPrice"));

        try {
            Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
            double shippingRate = ShippingMapper.getShippingRate(order.getShippingId(), connectionPool);

            System.out.println("OrderId: " + orderId);

            if (newPrice != originalPrice) {
                OrderMapper.updatePriceByOrderId(orderId, newPrice, connectionPool);
                OrderMapper.updateOrderStatusById(orderId, 2, connectionPool);
                MailController.sendNewOffer(order, orderId, shippingRate, newPrice);

                ctx.sessionAttribute("message", "Det nye tilbud er sendt til kunden");
                ctx.redirect("/adminpage");
            } else {
                OrderMapper.updateOrderStatusById(orderId, 2, connectionPool);
                MailController.sendOffer(order, orderId, shippingRate, originalPrice);

                ctx.sessionAttribute("message", "Tilbuddet er sendt til kunden");
                ctx.redirect("/adminpage");
            }

        } catch (DatabaseException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void adminDeleteOrder(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        try {
            OrderMapper.deleteBillOfMaterialLinesByOrderId(orderId, connectionPool);
            OrderMapper.deleteOrder(orderId, connectionPool);
            ctx.sessionAttribute("message", "Ordren er blevet slettet");
            ctx.redirect("/adminpage");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void noNewOffer(Context ctx, ConnectionPool connectionPool) {
    int orderId = Integer.parseInt(ctx.formParam("orderId"));
        try {
            Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
            OrderMapper.updateOrderStatusById(orderId, 2, connectionPool);
            double shippingRate = ShippingMapper.getShippingRate(order.getShippingId(), connectionPool);
            MailController.denyNewOffer(order, order.getOrderId(), shippingRate);
            ctx.sessionAttribute("message", "Der er sendt en mail til kunden om, at der ikke er lavet et nyt tilbud");
            ctx.redirect("/adminpage");
        } catch (DatabaseException | SQLException e) {
            throw new RuntimeException(e);
        }

    }


    private static void changeOrder(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        String cpWidthStr = ctx.formParam("cpWidth");
        String cpLengthStr = ctx.formParam("cpLength");
        String shWidthStr = ctx.formParam("shWidth");
        String shLengthStr = ctx.formParam("shLength");
        String cpRoof = ctx.formParam("cpRoof");

        try {
            Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
            int cpWidth = cpWidthStr != null ? Integer.parseInt(cpWidthStr) : order.getCpWidth();
            int cpLength = cpLengthStr != null ? Integer.parseInt(cpLengthStr) : order.getCpLength();
            int shWidth = shWidthStr != null ? Integer.parseInt(shWidthStr) : order.getShWidth();
            int shLength = shLengthStr != null ? Integer.parseInt(shLengthStr) : order.getShLength();
            cpRoof = cpRoof != null ? cpRoof : order.getCpRoof();

            AdminMapper.updateOrder(orderId, cpWidth, cpLength, shWidth, shLength, cpRoof, connectionPool);

            // Update the order instance with the new values
            order.setCpWidth(cpWidth);
            order.setCpLength(cpLength);
            order.setShWidth(shWidth);
            order.setShLength(shLength);
            order.setCpRoof(cpRoof);

            // Opdater status-ID'et for den pågældende ordre
            OrderMapper.updateOrderStatusById(orderId, 3, connectionPool);
            // After updating the order, send a modified order email with the updated order
            MailController.sendModifiedOrder(order, order.getOrderId());
            ctx.sessionAttribute("message", "Ordren er blevet ændret, og der er sendt en mail til kunden");

            ctx.redirect("/adminpage");
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException, SQLException {
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
        int postalCode = AddressMapper.getAddressById(order.getUser().getAddressId(), connectionPool).getPostalCode();
        double shippingRate = ShippingMapper.getShippingRate(order.getShippingId(), connectionPool);
        System.out.println(shippingRate);


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
        ctx.attribute("price", order.getPrice());
        ctx.attribute("comment", order.getComment());
        ctx.attribute("postalCode", postalCode);
        ctx.attribute("shippingRate", shippingRate);

        Calculator calculator = new Calculator(order.getCpWidth(), order.getCpLength(), connectionPool);
        calculator.calcCarport(order);
        List <BillOfMaterialLine> bomLines = calculator.getBomLine();


        ctx.attribute("bomLines", bomLines);
        ctx.render("admin-order.html");
    }

//    private static void adminPage(Context ctx, ConnectionPool connectionPool) {
//
//        ctx.render("adminpage.html");
//    }

    static void viewOrders(Context ctx, ConnectionPool connectionPool) {
        List<Order> orderList = null;
        String statusIdString = ctx.queryParam("statusId");

        try {
            if (statusIdString == null)
                statusIdString = "0";
            int statusId = Integer.parseInt(statusIdString);
            if (statusId == 0)
                orderList = AdminMapper.getAllOrders(connectionPool);
            else
                orderList = AdminMapper.getOrderByStatus(statusId, connectionPool);

            // Get the session attribute
            String message = ctx.sessionAttribute("message");

            // Remove the session attribute
            ctx.sessionAttribute("message", null);

            ctx.attribute("orderList", orderList);
            ctx.attribute("message", message); // Add the session message to the model

            ctx.render("adminpage.html");
        } catch (NumberFormatException | DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<User> getAllUsers(ConnectionPool connectionPool) {
        String sql = "SELECT * FROM users";
        List<User> userList = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
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
            throw new RuntimeException(e);
        }
        return userList;
    }
}