package app;

import app.config.ThymeleafConfig;
import app.config.SessionConfig;
import app.controllers.OrderController;
import app.controllers.StatusController;
import app.controllers.SvgController;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.io.IOException;

public class Main {

    private static final String USER = System.getenv("JDBC_USER");
    private static final String PASSWORD = System.getenv("JDBC_PASSWORD");
    private static final String URL = System.getenv("JDBC_CONNECTION_STRING");
    private static final String DB = System.getenv("JDBC_DB");
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) throws IOException {

        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing
        app.get("/", ctx -> ctx.render("index.html"));
        OrderController.addRoutes(app, connectionPool);
        UserController.addRoutes(app, connectionPool);
        SvgController.addRoutes(app, connectionPool);
        StatusController.addRoutes(app, connectionPool);

    }
}