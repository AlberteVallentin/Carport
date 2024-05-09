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
import java.util.Arrays;
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
        // Indhent værdier fra formular
        String firstName = ctx.formParam("first-name");
        String lastName = ctx.formParam("last-name");
        String email = ctx.formParam("email");
        String phone = ctx.formParam("phone");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");
        String streetName = ctx.formParam("ship-address");
        String houseNumber = ctx.formParam("house-number");
        String floorAndDoor = ctx.formParam("floor-and-door");
        String postalCodeString = ctx.formParam("postcode");
        String city = ctx.formParam("locality");

        // Fejlmeddelelse og attribut opsætning
        if (isAnyFieldEmpty(new String[]{firstName, lastName, email, phone, password1, password2, streetName, houseNumber, postalCodeString, city})) {
            setAttributesAndRenderForm(ctx, "Alle de påkrævede felter skal være udfyldt.", firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCodeString, city);
            return;
        }

        // Konverter postnummer til integer
        int postalCode;
        try {
            postalCode = Integer.parseInt(postalCodeString);
        } catch (NumberFormatException e) {
            setAttributesAndRenderForm(ctx, "Postnummeret skal være et gyldigt tal.", firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCodeString, city);
            return;
        }

        // Tjek om passwords matcher
        if (!password1.equals(password2)) {
            setAttributesAndRenderForm(ctx, "De to adgangskoder er ikke ens. Prøv igen.", firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCodeString, city);
            return;
        }

        // Forsøg at oprette bruger og adresse i databasen
        try {
            int addressId = AddressMapper.createAddress(streetName, houseNumber, floorAndDoor, postalCode, city, connectionPool);
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


    private static boolean isAnyFieldEmpty(String[] fields) {
        return Arrays.stream(fields).anyMatch(f -> f == null || f.isEmpty());
    }

    private static void setAttributesAndRenderForm(Context ctx, String message, String... values) {
        String[] attributeNames = {"firstName", "lastName", "email", "phone", "streetName", "houseNumber", "floorAndDoor", "postalCode", "city"};
        for (int i = 0; i < attributeNames.length; i++) {
            ctx.attribute(attributeNames[i], values[i]);
        }
        ctx.attribute("message", message);
        ctx.render("create-account.html");
    }

    private static void handleDatabaseError(Context ctx, DatabaseException e, String firstName, String lastName, String email, String phone, String streetName, String houseNumber, String floorAndDoor, int postalCode, String city) {
        ctx.attribute("message", e.getMessage());
        setAttributesAndRenderForm(ctx, e.getMessage(), firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, String.valueOf(postalCode), city);
    }
}