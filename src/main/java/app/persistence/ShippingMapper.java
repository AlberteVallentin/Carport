package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static app.utility.ShippingCalculator.calculateShippingRate;

public class ShippingMapper {

    public static int createShipping(int addressId, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "INSERT INTO shipping (address_id, shippping_rate) VALUES (?, ?)";
        double shippingRate = calculateShippingRate(addressId, connectionPool);
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setDouble(2, shippingRate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Returnerer det genererede shipping ID
            } else {
                throw new DatabaseException("Fejl ved oprettelse af shipping - ingen ID returneret");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Der er sket en fejl ved indsættelse af shipping", e.getMessage());
        }
    }
}