package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.AddressMapper;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
import app.persistence.ShippingMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminController
{
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/adminpage", ctx -> viewOrders(ctx, connectionPool));
        app.get("/admin-order", ctx -> ctx.render("admin-order.html"));
        //app.post("/admin-order", ctx -> viewOrders(ctx, connectionPool));
        app.get("/admin-offer", ctx -> ctx.render("admin-offer.html"));
        app.post("/admin-offer",ctx -> OffersSent(ctx, connectionPool));
        app.post("/showorder",ctx -> showOrder(ctx, connectionPool));
        //app.get("/showorder",ctx -> showOrder(ctx, connectionPool));



    }

    private static void showOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException, SQLException {
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        Order order = AdminMapper.getOrderDetailsById(orderId, connectionPool);
        int postalCode = AddressMapper.getAddressById(order.getUser().getAddressId(), connectionPool).getPostalCode();
        //double shippingRate = ShippingMapper.getShippingRate(order.getUser().getAddressId(), connectionPool);

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
        //ctx.attribute("shippingRate", shippingRate);

        ctx.render("admin-order.html");

    }

//    private static void adminPage(Context ctx, ConnectionPool connectionPool) {
//
//        ctx.render("adminpage.html");
//    }

    private static void viewOrders(Context ctx, ConnectionPool connectionPool){

        List<Order> orderList = null;
        String statusIdString = ctx.queryParam("statusId");

        try
        {
            if (statusIdString == null)
                statusIdString = "0";
            int statusId = Integer.parseInt(statusIdString);
            if (statusId == 0)
                orderList = AdminMapper.getAllOrders(connectionPool);
            else
                orderList = AdminMapper.getOrderByStatus(statusId, connectionPool);
            ctx.attribute("orderList", orderList);
            ctx.render("adminpage.html");
        } catch (NumberFormatException | DatabaseException e)
        {
            throw new RuntimeException(e);
        }

    }

    public static List<User> getAllUsers(ConnectionPool connectionPool)
    {
        String sql = "SELECT * FROM users";
        List<User> userList = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
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

    private static void OffersSent(Context ctx, ConnectionPool connectionPool)
    {
        ctx.render("admin-offer.html");
    }
}
