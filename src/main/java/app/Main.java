package app;
import app.config.ThymeleafConfig;
import app.config.SessionConfig;
import app.controllers.ContactController;
import app.controllers.OrderController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;



public class Main {


    public static final ConnectionPool connectionPool = ConnectionPool.getInstance(
            System.getenv("JDBC_USER"),
            System.getenv("JDBC_PASSWORD"),
            System.getenv("JDBC_CONNECTION_STRING"),
            System.getenv("JDBC_DB")
    );

    public static void main(String[] args) {

        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);


        // Routing


        ContactController.addRoutes(app,connectionPool);
        OrderController.addRoutes(app,connectionPool );

    }
}