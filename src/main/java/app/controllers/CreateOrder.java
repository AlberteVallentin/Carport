package app.controllers;

import app.entities.Materials;
import app.persistence.ConnectionPool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.javalin.http.Context;




public class CreateOrder  {


    private static void index(Context ctx , ConnectionPool connectionPool){


    }

    protected void GetCarportData(HttpServletRequest request, HttpServletResponse response){

        int widthCarport = Integer.parseInt(request.getParameter("width-carport"));
        int lengthCarport = Integer.parseInt(request.getParameter("length-carport"));
        String roofType= request.getParameter("roof-type");
        int widthShred = Integer.parseInt(request.getParameter("width-shred"));
        int lengthShred = Integer.parseInt(request.getParameter("length-shred"));
        String comment = request.getParameter("comment");

        int width =Integer.parseInt(String.valueOf(widthCarport));
        int depth =Integer.parseInt(String.valueOf(lengthCarport));
        String type = request.getParameter("roof-type");
        double materialPrice=0;
        String unit= "cm";
        String materialDescription="Beskrivelse";

        Materials materials = new Materials(1,width,depth,type,materialPrice,unit,materialDescription);

        // Udskriv data for at bekræfte modtagelse (kan fjernes i produktion)
        System.out.println("Bredde på carport: " + widthCarport);
        System.out.println("Længde på carport: " + lengthCarport);
        System.out.println("Tagtype: " + roofType);
        System.out.println("Bredde på redskabsskur: " + widthShred);
        System.out.println("Længde på redskabsskur: " + lengthShred);
        System.out.println("Kommentar: " + comment);


    }
}
