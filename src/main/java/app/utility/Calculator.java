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
    private double totalMaterialPrice;
    private ConnectionPool connectionPool;

    /**
     * Constructor to initialize width, length, and connection pool.
     *
     * @param width          The width of the carport.
     * @param length         The length of the carport.
     * @param connectionPool The connection pool for database operations.
     */
    public Calculator(int width, int length, ConnectionPool connectionPool) {
        this.width = width;
        this.length = length;
        this.connectionPool = connectionPool;
    }

    /**
     * Calculates the bill of material for the carport.
     *
     * @param order The order for which to calculate the bill of material.
     * @throws DatabaseException If a database error occurs.
     */
    public void calcCarport(Order order) throws DatabaseException {
        // Calculate bill of material for posts, beams, and rafters
        calcPost(order);
        calcBeams(order);
        calcRafters(order);
    }

    /**
     * Calculates the quantity of posts for the carport.
     *
     * @param order The order for which to calculate the quantity of posts.
     * @throws DatabaseException If a database error occurs.
     */
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

        // Check if the list is not empty
        if (!materialVariants.isEmpty()) {
            MaterialVariant materialVariant = materialVariants.get(0);

            // Calculate price of posts
            double materialPrice = materialVariant.getMaterial().getMaterialPrice();
            double postMeter = materialVariant.getLength();

            postPrice = (postMeter / 100) * quantity * materialPrice;

            String functionalDescription = FunctionalDescriptionMapper.getFunctionalDescriptionById(1, connectionPool);

            BillOfMaterialLine billOfMaterialLine = new BillOfMaterialLine(order, materialVariant, quantity, 1, functionalDescription);
            bomLine.add(billOfMaterialLine);
        } else {
            // Handle the case where the list is empty
            System.out.println("No material variants found.");
        }
    }

    /**
     * Calculates the quantity of beams for the carport.
     *
     * @param order The order for which to calculate the quantity of beams.
     * @throws DatabaseException If a database error occurs.
     */
    private void calcBeams(Order order) throws DatabaseException {
        // Calculate quantity of beams based on carport length
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
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getAllVariantsByMaterialId(BEAMS, connectionPool);

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

        double materialPrice = foundVariant.getMaterial().getMaterialPrice();

        double beamMeter = foundVariant.getLength();

        beamPrice = (beamMeter / 100) * quantity * materialPrice;

        MaterialVariant materialVariant = foundVariant;

        String functionalDescription = FunctionalDescriptionMapper.getFunctionalDescriptionById(2, connectionPool);

        BillOfMaterialLine billOfMaterialLine = new BillOfMaterialLine(order, materialVariant, quantity, 2, functionalDescription);
        bomLine.add(billOfMaterialLine);
    }

    /**
     * Calculates the quantity of rafters for the carport.
     *
     * @param order The order for which to calculate the quantity of rafters.
     * @throws DatabaseException If a database error occurs.
     */
    private void calcRafters(Order order) throws DatabaseException {
        // Calculate quantity of rafters based on carport width
        length = order.getCpWidth();
        int variantLength = Integer.MAX_VALUE;
        int variantId = 0;
        MaterialVariant foundVariant = null;

        int quantity = length / 55; // Assuming each rafter has a length of 55

        // Get all variants of rafters from the database
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getAllVariantsByMaterialId(RAFTERS, connectionPool);

        // Find suitable rafter variant based on carport width
        for (MaterialVariant m : materialVariants) {
            if (m.getLength() >= length && m.getLength() < variantLength) {
                variantLength = m.getLength();
                variantId = m.getMaterialVariantId();
                foundVariant = m;
            }
        }

        double materialPrice = foundVariant.getMaterial().getMaterialPrice();

        double rafterMeter = foundVariant.getLength();

        rafterPrice = (rafterMeter / 100) * quantity * materialPrice;

        MaterialVariant materialVariant = foundVariant;
        String functionalDescription = FunctionalDescriptionMapper.getFunctionalDescriptionById(3, connectionPool);

        BillOfMaterialLine billOfMaterialLine = new BillOfMaterialLine(order, materialVariant, quantity, 3, functionalDescription);
        bomLine.add(billOfMaterialLine);
    }

    /**
     * Gets the total material price for the carport.
     *
     * @return The total material price.
     */
    public double getTotalMaterialPrice() {
        totalMaterialPrice = postPrice + beamPrice + rafterPrice;
        return totalMaterialPrice;
    }

    /**
     * Retrieves the list of bill of material lines.
     *
     * @return The list of bill of material lines.
     */
    public List<BillOfMaterialLine> getBomLine() {
        return bomLine;
    }
}
