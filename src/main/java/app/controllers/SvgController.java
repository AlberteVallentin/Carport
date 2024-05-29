package app.controllers;

import app.entities.Order;
import app.entities.Svg;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Locale;

public class SvgController {

    /**
     * Adds routes to the Javalin application for various SVG-related functionalities.
     *
     * @param app            The Javalin application instance.
     */
    public static void addRoutes(Javalin app) {
        // Define route for showing carport drawing
        app.get("/showCarportDrawing", ctx -> SvgController.showCarportDrawing(ctx));
    }

    /**
     * Generates and displays the SVG drawings of the carport based on the current order.
     *
     * @param ctx The Javalin context, which provides access to the request and response.
     */
    public static void showCarportDrawing(Context ctx) {
        // Retrieve the current order from the session
        Order order = ctx.sessionAttribute("currentOrder");
        int length = order.getCpLength();
        int width = order.getCpWidth();
        int height = 225;

        // Define viewBox dimensions for the SVG
        String viewBox = "0 0 " + (length + 50) + " " + (width + 50);
        String viewBoxSide = "0 0 " + (length + 50) + " " + (height + 50);

        // Set default locale to US for consistent number formatting
        Locale.setDefault(new Locale("US"));

        // Create SVG objects for the top view of the carport
        Svg carportSvgTop = new Svg(0, 0, viewBox, "100%", "auto");
        Svg innerSvg = new Svg(0, 0, viewBox, "100%", "auto");

        // Add horizontal and vertical arrows to the top view SVG
        carportSvgTop.addArrow(40, width + 30, length + 40, width + 30);
        carportSvgTop.addArrow(10, 0, 10, width);

        // Add dimension text to the top view SVG
        carportSvgTop.addText(length / 2, width + 25, 0, length + " cm");
        carportSvgTop.addText(15, width / 2, 90, width + " cm");

        // Draw the main rectangle for the carport area
        innerSvg.addRectangle(40, 0, length, width, "stroke-width:1px; stroke:#000000; fill:#ffffff");

        // Draw horizontal beams
        innerSvg.addRectangle(40, 25, length, 5, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        innerSvg.addRectangle(40, width - 30, length, 5, "stroke-width:1px; stroke:#000000; fill:#ffffff");

        // Draw flat ends
        innerSvg.addRectangle(40, 0, 5, width, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        innerSvg.addRectangle(length + 35, 0, 5, width, "stroke-width:1px; stroke:#000000; fill:#ffffff");

        // Draw poles based on the length of the carport
        if (length <= 420) {
            innerSvg.addRectangle(60, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length + 5, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(60, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length + 5, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        } else {
            innerSvg.addRectangle(60, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length / 2 + 40, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length + 5, 20, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(60, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length / 2 + 40, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvg.addRectangle(length + 5, width - 35, 15, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        }

        // Draw diagonal support wires
        innerSvg.addLine(length / 10 + 40, 30, (int) (length / 1.4), width - 30, "stroke:#000000; stroke-dasharray:5 5");
        innerSvg.addLine(length / 10 + 40, width - 30, (int) (length / 1.4), 30, "stroke:#000000; stroke-dasharray:5 5");

        // Draw additional vertical beams
        for (int cm = 40; cm < length + 40; cm += 55) {
            innerSvg.addRectangle(cm, 0, 5, width, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            cm += 5;
        }

        // Add the inner SVG to the top view SVG
        carportSvgTop.addSvg(innerSvg);

        // Create SVG objects for the side view of the carport
        Svg carportSvgSide = new Svg(0, 0, viewBoxSide, "100%", "auto");
        Svg innerSvgSide = new Svg(0, 0, viewBoxSide, "100%", "auto");

        // Add horizontal arrow and dimension text to the side view SVG
        carportSvgSide.addArrow(40, height + 30, length + 40, height + 30);
        carportSvgSide.addText(length / 2, height + 25, 0, length + " cm");

        // Add vertical arrow and dimension text to the side view SVG
        carportSvgSide.addArrow(10, 0, 10, height);
        carportSvgSide.addText(15, height / 2, 90, height + " cm");

        // Draw the main rectangle for the side view area
        innerSvgSide.addRectangle(40, 0, length, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");

        // Draw poles based on the length of the carport in the side view
        if (length <= 420) {
            innerSvgSide.addRectangle(60, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvgSide.addRectangle(length + 5, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        } else {
            innerSvgSide.addRectangle(60, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvgSide.addRectangle(length / 2 + 40, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
            innerSvgSide.addRectangle(length + 5, 0, 15, height, "stroke-width:1px; stroke:#000000; fill:#ffffff");
        }

        // Draw horizontal beam at the top
        innerSvgSide.addRectangle(40, 0, length, 15, "stroke-width:1px; stroke:#000000; fill:#ffffff");

        // Add the inner SVG to the side view SVG
        carportSvgSide.addSvg(innerSvgSide);

        // Add the generated SVGs to the context attributes
        ctx.attribute("carportSvgTop", carportSvgTop.toString());
        ctx.attribute("carportSvgSide", carportSvgSide.toString());

        // Render the order summary page with the SVG drawings
        ctx.render("order-summary.html");
    }
}
