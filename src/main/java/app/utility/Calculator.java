package app.utility;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.FunctionalDescriptionMapper;
import app.persistence.MaterialMapper;
import app.persistence.MaterialVariantMapper;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    // Constants defining the number of posts, rafters, and beams
    private static final int POSTS = 3;
    private static final int RAFTERS = 4;
    private static final int BEAMS = 4;


    // List to store bill of material lines
    private List<BillOfMaterialLine> bomLine = new ArrayList<>();

    // Width and length of the carport, and connection pool for database operations
    private int width;
    private int length;
    private double beamPrice;
    private double rafterPrice;
    private double postPrice;
    private double  totalMaterialPrice;
    private ConnectionPool connectionPool;

    // Constructor to initialize width, length, and connection pool
    public Calculator(int width, int length, ConnectionPool connectionPool) {
        this.width = width;
        this.length = length;
        this.connectionPool = connectionPool;
    }



    // Method to calculate bill of material for the carport
    public void calcCarport(Order order) throws DatabaseException {
        // Calculate bill of material for posts, beams, and rafters
        calcPost(order);
        calcBeams(order);
        calcRafters(order);
    }

    // Method to calculate quantity of posts
    private void calcPost(Order order) throws DatabaseException {
        // Calculate quantity of posts based on carport length
        int quantity;
        if (length <= 420) {
            quantity = 4;
        } else {
            quantity = 6;
        }

        // Get material variants for posts from the database
        List<MaterialVariant> materialVariants = MaterialMapper.getMaterialsByProductIdAndMinLength(0, POSTS, connectionPool);
        MaterialVariant materialVariant = materialVariants.get(0);


        postPrice=(quantity*3)*82;

        String functionalDescription = FunctionalDescriptionMapper.getFunctionalDescriptionById(1, connectionPool);

        BillOfMaterialLine billOfMaterialLine = new BillOfMaterialLine(order, materialVariant, quantity, 1, functionalDescription);
        bomLine.add(billOfMaterialLine);
    }

    // Method to calculate quantity of beams
    private void calcBeams(Order order) throws DatabaseException{
        // Calculate quantity of beams based on carport length
        //Remme
        int quantity;
        int variantId = 0;
        MaterialVariant foundVariant = null;

        int length = order.getCpLength();

        if (length > 600) {
            quantity = 4;
        } else {
            quantity = 2;
        }

        int variantLength = Integer.MAX_VALUE;

        // Get all variants of beams from the database
        //List<MaterialVariant> materialVariants = MaterialVariantMapper.getAllVariantsByMaterialId(4, connectionPool);

        // Get all variants of beams from the database
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getAllVariantsByMaterialId(4, connectionPool);

        // Find suitable beam variant based on carport length
        if (length > 600) {
            for (MaterialVariant m : materialVariants) {
                if (m.getLength() >= length / 2 && m.getLength() < variantLength) {
                    variantLength = m.getLength();
                    variantId = m.getMaterialVariantId();
                    foundVariant = m;
                }
            }
        } else {
            for (MaterialVariant m : materialVariants) {
                if (m.getLength() >= length && m.getLength() < variantLength) {
                    variantLength = m.getLength();
                    variantId = m.getMaterialVariantId();
                    foundVariant = m;
                }
            }
        }

        MaterialVariant materialVariant = foundVariant;

        beamPrice=(foundVariant.getLength()/100)*(quantity*37);

        String functionalDescription = FunctionalDescriptionMapper.getFunctionalDescriptionById(2, connectionPool);

        BillOfMaterialLine billOfMaterialLine = new BillOfMaterialLine(order, materialVariant, quantity, 2, functionalDescription);
        bomLine.add(billOfMaterialLine);


    }

    // Method to calculate quantity of rafters
    private void calcRafters(Order order) throws DatabaseException{
        // Calculate quantity of rafters based on carport length
        //spær
        length = order.getCpWidth();
        int variantLength = Integer.MAX_VALUE;
        int variantId=0;
        MaterialVariant foundVariant = null;

        int quantity = length / 55; // Assuming each rafter has a length of 55

        // Get all variants of rafters from the database
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getAllVariantsByMaterialId(4, connectionPool);

        // Find suitable rafter variant based on carport length
        for (MaterialVariant m : materialVariants) {
            if (m.getLength() >= length && m.getLength() < variantLength) {
                variantLength = m.getLength();
                variantId = m.getMaterialVariantId();
                foundVariant = m;
            }
        }

       rafterPrice= (variantLength/100)*(quantity*37);

        MaterialVariant materialVariant = foundVariant;
        String functionalDescription = FunctionalDescriptionMapper.getFunctionalDescriptionById(3, connectionPool);


        BillOfMaterialLine billOfMaterialLine = new BillOfMaterialLine(order, materialVariant, quantity, 3, functionalDescription);
        bomLine.add(billOfMaterialLine);

    }


    public double getTotalMaterialPrice() {

        totalMaterialPrice = postPrice + beamPrice + rafterPrice;
        return  totalMaterialPrice;

    }

    // Method to retrieve the list of bill of material lines
    public List<BillOfMaterialLine> getBomLine() {
        return bomLine;
    }
}
