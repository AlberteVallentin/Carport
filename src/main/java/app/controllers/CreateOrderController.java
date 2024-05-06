package app.controllers;
import app.entities.Order;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;




public class CreateOrderController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> currentOrder(ctx, connectionPool));
        app.post("/", ctx -> currentOrder(ctx, connectionPool));

    }


    private static void index(Context ctx , ConnectionPool connectionPool){

    }

    private static void currentOrder(Context ctx , ConnectionPool connectionPool){

        Order order = ctx.sessionAttribute("currentOrder");
        if (order == null)
        {
         order=new Order();
         ctx.sessionAttribute("currentOrder",order);
        }
        try {
            int widthCarport = Integer.parseInt(ctx.formParam("width-carport"));
            int lengthCarport = Integer.parseInt(ctx.formParam("length-carport"));
            String roofType= ctx.formParam("roof-type");
            int widthShred = Integer.parseInt(ctx.formParam("width-shred"));
            int lengthShred = Integer.parseInt(ctx.formParam("length-shred"));
            String comment = ctx.formParam("comment");
            ctx.render("carport-order.html");


        } catch (NumberFormatException e) {
            ctx.attribute("message", e.getMessage());
            ctx.render("carport-order.html");
        }


    }



}
