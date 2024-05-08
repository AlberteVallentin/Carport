package app.controllers;

import app.entities.Address;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.AddressMapper;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/createAccount", ctx -> ctx.render("create-account.html"));
        app.post("/createAccount", ctx -> createAccount(ctx, connectionPool));
    }

    private static void createAccount(Context ctx, ConnectionPool connectionPool) {
        String firstName = ctx.formParam("first-name");
        String lastName = ctx.formParam("last-name");
        String email = ctx.formParam("email");
        int phone = Integer.parseInt(ctx.formParam("phone"));
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String streetName = ctx.formParam("ship-address");
        String houseNumber = ctx.formParam("house-number");
        String floorAndDoor = ctx.formParam("floor-and-door");
        int postalCode = Integer.parseInt(ctx.formParam("postcode"));
        String city = ctx.formParam("locality");


        if (password1.equals(password2)) {
            try {
               int addressId = AddressMapper.createAddress(streetName, houseNumber, floorAndDoor, postalCode, city, connectionPool);
                UserMapper.createUser(firstName, lastName, email, phone, password1, addressId, connectionPool);

                ctx.attribute("message", "Hermed oprettet med e-mailen: " + email + ". Log på.");
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
}
