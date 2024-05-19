package app.utility;

import app.entities.Shipping;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShippingCalculator {

    private static double shippingPrice;

    /**
     * Calculates the shipping rate based on the address ID.
     *
     * @param addressId      The ID of the address.
     * @param connectionPool The connection pool for database operations.
     * @return The calculated shipping rate.
     */
    public static double calculateShippingRate(int addressId, ConnectionPool connectionPool) {
        String sql = "SELECT postal_code FROM address WHERE address_id = ?";
        double shippingRate = 0;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ResultSet rs = ps.executeQuery();


            if (rs.next()) {
                int postalCode = rs.getInt("postal_code"); // Retrieve the postal code from the result set
                shippingRate = determineShippingRate(postalCode); // Calculate shipping cost based on postal code
            } else {
                System.out.println("No results returned from the SQL query"); // Add a log print to confirm no results returned
            }

            return shippingRate;
        } catch (SQLException e) {
            System.out.println("Error while calculating shipping rate: " + e.getMessage()); // Log the error message
            e.printStackTrace();
        }
        return shippingRate;
    }

    private static double determineShippingRate(int postalCode) {
        if (postalCode >= 0 && postalCode <= 4999) {
            shippingPrice = 0;
        } else if (postalCode >= 5000 && postalCode <= 5999) {
            shippingPrice = 199;
        } else if (postalCode >= 6000 && postalCode <= 9999) {
            shippingPrice = 299;
        } else {
            shippingPrice = 0;
        }
        System.out.println("Determined shipping price for postal code " + postalCode + ": " + shippingPrice); // Add a log print to confirm the determined shipping price
        return shippingPrice;
    }
//
//    /**
//     * Determines the shipping rate based on the postal code.
//     *
//     * @param postalCode The postal code of the address.
//     * @return The determined shipping rate.
//     */
//    private static double determineShippingRate(int postalCode) {
//        if (postalCode >= 0 && postalCode <= 4999) {
//            shippingPrice = 0;
//        } else if (postalCode >= 5000 && postalCode <= 5999) {
//            shippingPrice = 199;
//        } else if (postalCode >= 6000 && postalCode <= 9999) {
//            shippingPrice = 299;
//        } else {
//            shippingPrice = 0;
//        }
//        return shippingPrice;
//    }

    /**
     * Updates the shipping rate for a given shipping object based on the address ID.
     *
     * @param addressId      The ID of the address.
     * @param connectionPool The connection pool for database operations.
     * @param shipping       The shipping object to update.
     */
    public static void updateShippingRateForShipping(int addressId, ConnectionPool connectionPool, Shipping shipping) {
        double shippingRate = calculateShippingRate(addressId, connectionPool);
        shipping.setShippingRate(shippingRate);
    }
}
