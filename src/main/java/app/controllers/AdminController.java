package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
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
        app.post("/admin-order", ctx -> viewOrders(ctx, connectionPool));
        app.get("/admin-offer", ctx -> ctx.render("admin-offer.html"));
        app.post("/admin-offer",ctx -> OffersSent(ctx, connectionPool));


    }

    private static void adminPage(Context ctx, ConnectionPool connectionPool) {

        ctx.render("adminpage.html");
    }

    private static void viewOrders(Context ctx, ConnectionPool connectionPool){

        List<Order> orderList = null;
        try
        {
            orderList = AdminMapper.getAllOrders(connectionPool);
            ctx.attribute("orderList", orderList);
            ctx.render("adminpage.html");
        } catch (DatabaseException e)
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
