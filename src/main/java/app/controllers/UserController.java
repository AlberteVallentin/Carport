package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("createaccount", ctx -> ctx.render("create-account.html"));
        app.post("createaccount", ctx -> createAccount(ctx, connectionPool));
    }

    private static void createAccount(Context ctx, ConnectionPool connectionPool) {
        String firstName = ctx.formParam("first-name");
        String lastName = ctx.formParam("last-name");
        String email = ctx.formParam("email");
        String phone = ctx.formParam("phone");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String streetName = ctx.formParam("street-name");



        if (password1.equals(password2)) {
            try {

                int customerId = UserMapper.createUser(name, streetName, houseNumber, floor, phoneNumber, zipCode, connectionPool);
                ctx.attribute("message", "Hermed oprettet med email: " + email + ". Log på.");
                UserMapper.createUserTable(email, password1, customerId, connectionPool);
                ctx.render("login.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Brugernavn findes allerede - prøv igen eller log ind.");
                ctx.render("login.html");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            ctx.attribute("message", "passwords matcher ikke - prøv igen.");
            ctx.render("login.html");
        }

    }

    private static void opretBruger(Context ctx, ConnectionPool connectionPool) {
        // Hent form parametre
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (password1.equals(password2)) {
            try {
                UserMapper.opretBruger(name, email, password1, connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med e-mailen: " + email +
                        ". Nu skal du logge på.");
                ctx.render("login.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", "Din e-mail findes allerede. Prøv igen, eller log ind");
                ctx.render("opretbruger.html");
            }
        } else {
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen");
            ctx.render("opretbruger.html");
        }

    }
