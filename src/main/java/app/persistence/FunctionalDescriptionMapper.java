package app.persistence;

import app.entities.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FunctionalDescriptionMapper {

    public static String getFunctionalDescriptionById(int functionalDescriptionId, ConnectionPool connectionPool) {
        String sql = "SELECT functional_description FROM functional_description WHERE functional_description_id = ?";
        String functionalDescription = null;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, functionalDescriptionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                functionalDescription = rs.getString("functional_description");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return functionalDescription;
    }
}