package app.utility;

import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ShippingCalculator {

    public static double calculateShippingRate(int addressId, ConnectionPool connectionPool) {
        String sql = "SELECT a.postal_code FROM address a JOIN shipping s ON a.address_id = s.address_id WHERE s.address_id = ?";
        double shippingRate = 0;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int postalCode = rs.getInt("postal_code"); // Retrieve the postal code from the result set
                shippingRate = determineShippingRate(postalCode); // Calculate shipping cost based on postal code
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Better error handling could be implemented depending on your application's requirements
        }

        return shippingRate;
    }

    private static double determineShippingRate(int postalCode) {
        if (postalCode >= 0 && postalCode <= 4999) {
            return 0;
        } else if (postalCode >= 5000 && postalCode <= 5999) {
            return 199;
        } else if (postalCode >= 6000 && postalCode <= 9999) {
            return 299;
        } else {
            return 0;
        }
    }
}
