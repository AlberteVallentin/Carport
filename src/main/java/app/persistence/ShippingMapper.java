package app.persistence;

import app.entities.Shipping;
import app.exceptions.DatabaseException;
import app.utility.ShippingCalculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static app.utility.ShippingCalculator.calculateShippingRate;

public class ShippingMapper {

    /**
     * Creates a new shipping entry in the database.
     *
     * @param addressId      The ID of the address associated with the shipping.
     * @param connectionPool The connection pool for database connections.
     * @return The generated shipping ID.
     * @throws SQLException       If a SQL error occurs.
     * @throws DatabaseException  If a database error occurs.
     */
    public static int createShipping(int addressId, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        double shippingRate = ShippingCalculator.calculateShippingRate(addressId, connectionPool);
        String sql = "INSERT INTO shipping (address_id, shipping_rate) VALUES (?, ?) RETURNING shipping_id";


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

    /**
     * Retrieves the shipping rate for a given shipping ID from the database.
     *
     * @param shippingId     The ID of the shipping to retrieve the rate for.
     * @param connectionPool The connection pool for database connections.
     * @return The shipping rate.
     * @throws SQLException       If a SQL error occurs.
     * @throws DatabaseException  If a database error occurs.
     */
    public static double getShippingRate(int shippingId, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "SELECT shipping_rate FROM shipping WHERE shipping_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, shippingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            } else {
                throw new DatabaseException("Error getting shipping rate - no rate returned");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Error getting shipping rate", e.getMessage());
        }
    }

    /**
     * Retrieves a shipping entry by its ID from the database.
     *
     * @param shippingId     The ID of the shipping to retrieve.
     * @param connectionPool The connection pool for database connections.
     * @return The Shipping object corresponding to the given ID.
     * @throws SQLException       If a SQL error occurs.
     * @throws DatabaseException  If a database error occurs.
     */
    public static Shipping getShippingById(int shippingId, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "SELECT * FROM shipping WHERE shipping_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, shippingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Shipping(rs.getInt("shipping_id"), rs.getInt("address_id"), rs.getDouble("shipping_rate"));
            } else {
                throw new DatabaseException("Error getting shipping - no shipping returned");
            }
        } catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Error getting shipping", e.getMessage());
        }
    }
}
