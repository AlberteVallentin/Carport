package app.persistence;

import app.entities.Address;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressMapper {

    /**
     * Creates a new address in the database.
     *
     * @param streetName            The street name of the address.
     * @param houseNumber           The house number of the address.
     * @param floorAndDoorDescription The floor and door description of the address.
     * @param postalCode            The postal code of the address.
     * @param city                  The city of the address.
     * @param connectionPool        The connection pool for database connections.
     * @return The generated address ID.
     * @throws SQLException         If a SQL error occurs.
     * @throws DatabaseException    If a database error occurs.
     */
    public static int createAddress(String streetName, String houseNumber, String floorAndDoorDescription, int postalCode, String city, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "INSERT INTO address (street_name, house_number, floor_and_door_description, postal_code, city) VALUES (?, ?, ?, ?, ?) RETURNING address_id";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            ps.setString(1, streetName);
            ps.setString(2, houseNumber);
            ps.setString(3, floorAndDoorDescription);
            ps.setInt(4, postalCode);
            ps.setString(5, city);

            // Execute the query and retrieve the generated address ID
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Return the generated address ID
            } else {
                throw new DatabaseException("Fejl ved oprettelse af adresse - ingen ID returneret");
            }
        } catch (SQLException | DatabaseException e) {
            // Handle SQL and database exceptions
            throw new DatabaseException("Der er sket en fejl ved indsættelse af adresse", e.getMessage());
        }
    }

    /**
     * Retrieves an address from the database by its ID.
     *
     * @param addressId        The ID of the address to retrieve.
     * @param connectionPool   The connection pool for database connections.
     * @return The Address object corresponding to the given ID.
     * @throws SQLException    If a SQL error occurs.
     * @throws DatabaseException If a database error occurs.
     */
    public static Address getAddressById(int addressId, ConnectionPool connectionPool) throws SQLException, DatabaseException {
        String sql = "SELECT * FROM address WHERE address_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Set the address ID parameter for the prepared statement
            ps.setInt(1, addressId);

            // Execute the query and retrieve the address details
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String streetName = rs.getString("street_name");
                String houseNumber = rs.getString("house_number");
                String floorAndDoorDescription = rs.getString("floor_and_door_description");
                int postalCode = rs.getInt("postal_code");
                String city = rs.getString("city");

                // Return the Address object with the retrieved details
                return new Address(addressId, streetName, houseNumber, floorAndDoorDescription, postalCode, city);
            } else {
                throw new DatabaseException("Ingen adresse fundet med ID: " + addressId);
            }
        } catch (SQLException | DatabaseException e) {
            // Handle SQL and database exceptions
            throw new DatabaseException("Der er sket en fejl ved hentning af adresse", e.getMessage());
        }
    }
}
