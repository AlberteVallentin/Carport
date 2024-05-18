package app.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FunctionalDescriptionMapper {

    /**
     * Retrieves a functional description by its ID from the database.
     *
     * @param functionalDescriptionId The ID of the functional description to retrieve.
     * @param connectionPool          The connection pool for database connections.
     * @return The functional description as a string.
     */
    public static String getFunctionalDescriptionById(int functionalDescriptionId, ConnectionPool connectionPool) {
        String sql = "SELECT functional_description FROM functional_description WHERE functional_description_id = ?";
        String functionalDescription = null;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Set the functional description ID parameter for the prepared statement
            ps.setInt(1, functionalDescriptionId);
            ResultSet rs = ps.executeQuery();

            // Retrieve the functional description from the result set
            if (rs.next()) {
                functionalDescription = rs.getString("functional_description");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return functionalDescription;
    }

    /**
     * Retrieves all functional descriptions from the database.
     *
     * @param connectionPool The connection pool for database connections.
     * @return A list of all functional descriptions as strings.
     */
    public static List<String> getAllFunctionalDescriptions(ConnectionPool connectionPool) {
        String sql = "SELECT functional_description FROM functional_description";
        List<String> functionalDescriptions = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            // Iterate over the result set and add each functional description to the list
            while (rs.next()) {
                String functionalDescription = rs.getString("functional_description");
                functionalDescriptions.add(functionalDescription);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return functionalDescriptions;
    }
}

