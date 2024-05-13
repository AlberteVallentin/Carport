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
import java.util.List;

public class AdminController
{
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/adminpage", ctx -> ctx.render("adminpage.html"));
        app.post("/adminpage", ctx -> adminPage(ctx, connectionPool));
        app.get("/admin-order", ctx -> ctx.render("admin-order.html"));
        app.post("/admin-order", ctx -> viewOrders(ctx, connectionPool));
        app.get("/admin-offer", ctx -> ctx.render("admin-offer.html"));
        app.post("/admin-offer",ctx -> OffersSent(ctx, connectionPool));


    }

    private static void adminPage(Context ctx, ConnectionPool connectionPool) {

        ctx.render("adminpage.html");
    }



    public static void login(Context ctx, ConnectionPool connectionPool) {
        // Hent form parametre
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        // Check om bruger findes i DB med de angivne username + password
        try {
            User user = AdminMapper.adminLoginCheck(email, password, connectionPool);

            // Hvis ja, og brugeren er admin, send videre til admin page
            if (AdminController.isAdmin(user)) {
                ctx.sessionAttribute("currentUser", user);
                ctx.redirect("/adminpage");

              /*  List<User> userList = AdminMapper.getAllUsers(connectionPool);
                ctx.attribute("userList", userList);
                ctx.render("adminpage.html");

               */

                } else {
                ctx.attribute("message","du er ikke autoriseret til denne side");
                    ctx.render("login.html");
                }
            } catch (DatabaseException e)
        { ctx.attribute("message", "Fejl i enten email eller kode, prøv igen");
            ctx.render("login.html");
            throw new RuntimeException(e);
        }
    }

    public static User adminLoginCheck(String email, String password, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "select * from users where email=? and password=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                boolean admin = rs.getBoolean("admin");

                return new User(userId, name, password, email, admin);
            } else {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        } catch (SQLException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }


    private static void BackToAdminPage(Context ctx, ConnectionPool connectionPool) {
    ctx.render("adminpage.html");

    }

    private static void viewOrders(Context ctx, ConnectionPool connectionPool){

            List<Order> orderList = OrdersMapper.getAllOrders(connectionPool);
            ctx.attribute("orderList", orderList);
            ctx.render("admin-order.html");
    }

    private static void OffersSent(Context ctx, ConnectionPool connectionPool)
    {
        ctx.render("admin-offer.html");
    }
}
