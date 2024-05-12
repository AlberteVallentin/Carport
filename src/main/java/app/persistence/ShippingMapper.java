package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static app.utility.ShippingCalculator.calculateShippingRate;

public class ShippingMapper {

    public static int createShipping(int addressId, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "INSERT INTO shipping (address_id, shipping_rate) VALUES (?, ?) RETURNING shipping_id";
        double shippingRate = calculateShippingRate(addressId, connectionPool);
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setDouble(2, shippingRate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated shipping ID
            } else {
                throw new DatabaseException("Error creating shipping - no ID returned");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Error inserting shipping", e.getMessage());
        }
    }
}