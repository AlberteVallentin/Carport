package app.controllers;

import app.entities.Order;
import app.entities.Svg;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Locale;

public class SvgController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/showCarportDrawing", ctx -> SvgController.showCarportDrawing(ctx));

    }




    public static void showCarportDrawing(Context ctx){

        Order order = ctx.sessionAttribute("Order");
        int length = order.getCpLength();
        int width = order.getCpWidth();
        int height = 225;

        String viewBox = "0 0 "+String.valueOf(length+50)+" "+String.valueOf(width+50);
        String viewBoxSide = "0 0 "+String.valueOf(length+50)+" "+String.valueOf(height+50);
        String innerviewBox = "0 0 "+String.valueOf(length)+" "+String.valueOf(width);

        Locale.setDefault(new Locale("US"));

        Svg carportSvgTop = new Svg(0,0,viewBox, "100%", "auto");
        Svg innerSvg = new Svg(0,0,viewBox,"100%","auto");

        //Vandret pil
        carportSvgTop.addArrow(40,width+30,length+40,width+30);
        //Lodret pil
        carportSvgTop.addArrow(10,0,10,width);

        //Vandret tekst
        carportSvgTop.addText(length/2,width+25,0,String.valueOf(length)+" cm");
        //Lodret tekst
        carportSvgTop.addText(15,width/2,90,String.valueOf(width)+ " cm");

        //Draw area:
        innerSvg.addRectangle(40,0,length,width,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        //Horisontale træstykker
        innerSvg.addRectangle(40,25,length,5,"stroke-width:1px; stroke:#000000; fill:#ffffff");
        innerSvg.addRectangle(40,width-30,length,5,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        //Flade ender
        innerSvg.addRectangle(40,0,5,width,"stroke-width:1px; stroke:#000000; fill:#ffffff");
        innerSvg.addRectangle(length+35,0,5,width,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        //Pæle
        if (length<=420) {
            innerSvg.addRectangle(60, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length + 5, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");

            innerSvg.addRectangle(60, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length + 5, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        }
        else {
            innerSvg.addRectangle(60, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length / 2 + 40, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length + 5, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");

            innerSvg.addRectangle(60, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length / 2 + 40, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length + 5, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        }
        //wire\
        innerSvg.addLine(length/10+40,30, (int) (length/1.4),width-30,"stroke:#000000; stroke-dasharray:5 5");
        //wire/
        innerSvg.addLine(length/10+40,width-30, (int) (length/1.4),30,"stroke:#000000; stroke-dasharray:5 5");

        for(int cm = 40; cm<length+40;cm+=55){
            innerSvg.addRectangle(cm,0,5,width,"stroke-width:1px; stroke:#000000; fill:#ffffff");
            cm+=5;
        }

        carportSvgTop.addSvg(innerSvg);

        //Carport sideview
        Svg carportSvgSide = new Svg(0,0,viewBoxSide, "100%", "auto");
        Svg innerSvgSide = new Svg(0,0,viewBoxSide,"100%","auto");

        //Vandret pil og tekst
        carportSvgSide.addArrow(40,height+30,length+40,height+30);
        carportSvgSide.addText(length/2,height+25,0,String.valueOf(length)+" cm");

        //Lodret pil og tekst
        carportSvgSide.addArrow(10,0,10,height);
        carportSvgSide.addText(15,height/2,90,String.valueOf(height)+ " cm");


        //Draw area:
        innerSvgSide.addRectangle(40,0,length,height,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        if (length<=420) {
            //Pæle
            innerSvgSide.addRectangle(60, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvgSide.addRectangle(length + 5, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        }
        else {
            innerSvgSide.addRectangle(60, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvgSide.addRectangle(length / 2 + 40, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvgSide.addRectangle(length + 5, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        }
        //Vandret bjælke top
        innerSvgSide.addRectangle(40,0,length,15,"stroke-width:1px; stroke:#000000; fill:#ffffff");

        carportSvgSide.addSvg(innerSvgSide);

        ctx.attribute("carportSvgTop", carportSvgTop.toString());

        ctx.attribute("carportSvgSide", carportSvgSide.toString());

        ctx.render("order-summary.html");
    }
}
