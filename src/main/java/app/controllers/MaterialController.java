package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class MaterialController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/materials", ctx -> ctx.render("admin-materials.html"));
     




    }

}
