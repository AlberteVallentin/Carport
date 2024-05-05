package app;
import app.config.ThymeleafConfig;
import app.config.SessionConfig;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {
        private static final String USER = "postgres";
        private static final String PASSWORD = "postgres";
        private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
        private static final String DB = "databasename";
        private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);



        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing

        app.get("/", ctx -> ctx.render("build-self.html"));
        app.get("/users-data.html", ctx -> ctx.render("users-data.html"));

    }
}