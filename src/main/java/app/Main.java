package app;
import app.config.ThymeleafConfig;
import app.config.SessionConfig;
import app.controllers.ContactController;
import app.controllers.CreateOrderController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;



public class Main {


    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(
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



        CreateOrderController.addRoutes(app,connectionPool );
        ContactController.addRoutes(app,connectionPool);
    }
}