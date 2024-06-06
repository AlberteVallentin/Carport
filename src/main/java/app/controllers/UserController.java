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

public class UserController {

    /**
     * Adds routes to the Javalin application for various user-related functionalities.
     *
     * @param app            The Javalin application instance.
     * @param connectionPool The connection pool for database connections.
     */
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        // Define routes for user operations
        app.get("/createaccount", ctx -> ctx.render("create-account.html"));
        app.post("/createaccount", ctx -> createAccount(ctx, connectionPool));
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/contactdetails", ctx -> contactDetails(ctx, connectionPool));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/loginorout", ctx -> loginOrOut(ctx));
    }

    /**
     * Renders the login or logout page based on user session.
     *
     * @param ctx The Javalin context, which provides access to the request and response.
     */
    private static void loginOrOut(Context ctx) {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.render("login.html");
        } else {
            ctx.render("/logout.html");
        }
    }

    /**
     * Logs out the current user and invalidates the session.
     *
     * @param ctx The Javalin context, which provides access to the request and response.
     */
    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    /**
     * Displays the contact details of the current user.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    static void contactDetails(Context ctx, ConnectionPool connectionPool) {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.redirect("/login");
        } else {
            try {
                User user = UserMapper.getUserById(currentUser.getUserId(), connectionPool);
                Address address = AddressMapper.getAddressById(user.getAddressId(), connectionPool);
                ctx.attribute("firstName", user.getFirstName());
                ctx.attribute("lastName", user.getLastName());
                ctx.attribute("email", user.getEmail());
                ctx.attribute("phoneNumber", user.getPhoneNumber());
                ctx.attribute("streetName", address.getStreetName());
                ctx.attribute("houseNumber", address.getHouseNumber());
                ctx.attribute("floorAndDoor", address.getFloorAndDoorDescription());
                ctx.attribute("postalCode", address.getPostalCode());
                ctx.attribute("city", address.getCity());
                ctx.render("contact-details.html");
            } catch (DatabaseException e) {
                ctx.attribute("message", e.getMessage());
                ctx.render("contact-details.html");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Logs in the user and sets the session attributes.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    public static void login(Context ctx, ConnectionPool connectionPool) {
        String mail = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            // Check if user exists in the database with the given email and password
            User user = UserMapper.login(mail, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            Boolean hasAnOrder = ctx.sessionAttribute("hasAnOrder");
            Boolean isOrdering = ctx.sessionAttribute("isOrdering");

            // Redirect user based on their role and session state
            if (user.isAdmin()) {
                ctx.redirect("/adminpage");
            } else if (hasAnOrder != null && hasAnOrder) {
                contactDetails(ctx, connectionPool);
            } else if (isOrdering != null && isOrdering) {
                ctx.redirect("/carportorder");
            } else {
                ctx.render("index.html");
            }
        } catch (DatabaseException e) {
            // If login fails, return to login page with error message
            ctx.attribute("message", "Forkert login. Prøv venligst igen.");
            ctx.render("login.html");
        }
    }

    /**
     * Creates a new user account and saves it to the database.
     *
     * @param ctx            The Javalin context, which provides access to the request and response.
     * @param connectionPool The connection pool for database connections.
     */
    private static void createAccount(Context ctx, ConnectionPool connectionPool) {
        // Retrieve form values from the request
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

        // Validate that all required fields are filled
        if (isAnyFieldEmpty(new String[]{firstName, lastName, email, phone, password1, password2, streetName, houseNumber, postalCodeString, city})) {
            setAttributesAndRenderForm(ctx, "Alle de påkrævet felter skal være udfyldt.", firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCodeString, city);
            return;
        }

        // Convert postal code to integer
        int postalCode;
        try {
            postalCode = Integer.parseInt(postalCodeString);
        } catch (NumberFormatException e) {
            setAttributesAndRenderForm(ctx, "Postnummeret skal være et gyldigt tal.", firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCodeString, city);
            return;
        }

        // Check if passwords match
        if (!password1.equals(password2)) {
            setAttributesAndRenderForm(ctx, "De to adgangskoder er ikke ens. Prøv igen.", firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCodeString, city);
            return;
        }

        // Attempt to create user and address in the database
        try {
            int addressId = AddressMapper.createAddress(streetName, houseNumber, floorAndDoor, postalCode, city, connectionPool);
            UserMapper.createUser(firstName, lastName, email, phone, password1, addressId, connectionPool);
            ctx.attribute("message", "Du er nu oprettet med e-mailen: " + email + ". Log på.");
            ctx.render("login.html");
        } catch (DatabaseException e) {
            handleDatabaseError(ctx, e, firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCode, city);
        } catch (SQLException e) {
            handleDatabaseError(ctx, new DatabaseException("Der er sket en fejl. Prøv igen"), firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, postalCode, city);
        }
    }


    /**
     * Checks if any of the fields are empty.
     *
     * @param fields Array of strings representing the fields to check.
     * @return True if any field is empty, false otherwise.
     */
    private static boolean isAnyFieldEmpty(String[] fields) {
        return Arrays.stream(fields).anyMatch(f -> f == null || f.isEmpty());
    }

    /**
     * Sets attributes and renders the create account form with a message.
     *
     * @param ctx     The Javalin context, which provides access to the request and response.
     * @param message The message to display on the form.
     * @param values  The values to set on the form fields.
     */
    private static void setAttributesAndRenderForm(Context ctx, String message, String... values) {
        String[] attributeNames = {"firstName", "lastName", "email", "phone", "streetName", "houseNumber", "floorAndDoor", "postalCode", "city"};
        for (int i = 0; i < attributeNames.length; i++) {
            ctx.attribute(attributeNames[i], values[i]);
        }
        ctx.attribute("message", message);
        ctx.render("create-account.html");
    }

    /**
     * Handles database errors and sets the form attributes with error messages.
     *
     * @param ctx         The Javalin context, which provides access to the request and response.
     * @param e           The database exception that occurred.
     * @param firstName   The first name of the user.
     * @param lastName    The last name of the user.
     * @param email       The email of the user.
     * @param phone       The phone number of the user.
     * @param streetName  The street name of the user's address.
     * @param houseNumber The house number of the user's address.
     * @param floorAndDoor The floor and door description of the user's address.
     * @param postalCode  The postal code of the user's address.
     * @param city        The city of the user's address.
     */
    private static void handleDatabaseError(Context ctx, DatabaseException e, String firstName, String lastName, String email, String phone, String streetName, String houseNumber, String floorAndDoor, int postalCode, String city) {
        ctx.attribute("message", e.getMessage());
        setAttributesAndRenderForm(ctx, e.getMessage(), firstName, lastName, email, phone, streetName, houseNumber, floorAndDoor, String.valueOf(postalCode), city);
    }
}
