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
import java.util.Objects;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/createaccount", ctx -> ctx.render("create-account.html"));
        app.post("/createaccount", ctx -> createAccount(ctx, connectionPool));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {
        String mail = ctx.formParam("email");
        String password = ctx.formParam("password");

        // Check om bruger findes i DB med de angivne username + password
        try {
            User user = UserMapper.login(mail, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
        } catch (DatabaseException e) {
            //hvis nej send tilbage til login side med fejl
            ctx.attribute("message", "Forkert login. Prøv venligst igen.");
            ctx.render("login.html");
        }
    }

    private static void createAccount(Context ctx, ConnectionPool connectionPool) {
        String firstName = ctx.formParam("first-name");
        String lastName = ctx.formParam("last-name");
        String email = ctx.formParam("email");
        String phone = ctx.formParam("phone");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String streetName = ctx.formParam("ship-address");
        String houseNumber = ctx.formParam("house-number");
        String floorAndDoor = ctx.formParam("floor-and-door");
        int postalCode = Integer.parseInt(Objects.requireNonNull(ctx.formParam("postcode")));
        String city = ctx.formParam("locality");

        if(firstName == null || lastName == null || email == null || phone == null || password1 == null || password2 == null || streetName == null || houseNumber == null || floorAndDoor == null || city == null) {
            ctx.attribute("message", "Alle felter skal udfyldes.");
            ctx.render("create-account.html");
            return;
        }


        if (password1.equals(password2)) {
            try {
                int addressId = AddressMapper.createAddress(streetName, houseNumber, floorAndDoor, postalCode, city, connectionPool);
                UserMapper.createUser(firstName, lastName, email, phone, password1, addressId, connectionPool);

                ctx.attribute("message", "Du er nu oprettet med e-mailen: " + email + ". Log på.");
                ctx.render("login.html");
            } catch (DatabaseException | SQLException e) {
                ctx.attribute("message", e.getMessage());

                // Sæt attributter for tidligere indtastede værdier
                ctx.attribute("firstName", firstName);
                ctx.attribute("lastName", lastName);
                ctx.attribute("email", "");  // Sæt til tom, da email skal ændres
                ctx.attribute("phone", phone);
                ctx.attribute("streetName", streetName);
                ctx.attribute("houseNumber", houseNumber);
                ctx.attribute("floorAndDoor", floorAndDoor);
                ctx.attribute("postalCode", postalCode);
                ctx.attribute("city", city);

                ctx.render("create-account.html");
            }
        } else {
            ctx.attribute("message", "De to adgangskoder er ikke ens. Prøv igen.");

            // Sæt attributter for tidligere indtastede værdier
            ctx.attribute("firstName", firstName);
            ctx.attribute("lastName", lastName);
            ctx.attribute("email", email);
            ctx.attribute("phone", phone);
            ctx.attribute("streetName", streetName);
            ctx.attribute("houseNumber", houseNumber);
            ctx.attribute("floorAndDoor", floorAndDoor);
            ctx.attribute("postalCode", postalCode);
            ctx.attribute("city", city);

            ctx.render("create-account.html");
        }
    }
}
