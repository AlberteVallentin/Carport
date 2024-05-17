package app.persistence;

import app.entities.Order;
import app.entities.Material;
import app.entities.MaterialVariant;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper
{
    public static List<MaterialVariant> getMaterialsByProductIdAndMinLength(int minLength, int materialId, ConnectionPool connectionPool) throws DatabaseException
    {
        List<MaterialVariant> materialVariants = new ArrayList<>();
        String sql = "SELECT * FROM material_variant " +
                "INNER JOIN material p USING(material_id) " +
                "WHERE material_id = ? AND length >= ?";
        try (Connection connection = connectionPool.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, materialId);
            ps.setInt(2, minLength);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next())
            {
                int materialVariantId = resultSet.getInt("material_variant_id");
                int material_id = resultSet.getInt("material_id");
                int length = resultSet.getInt("length");
                String type = resultSet.getString("type");
                String unit = resultSet.getString("unit");

                int width = resultSet.getInt("width");
                int depth = resultSet.getInt("depth");
                int materialPrice = resultSet.getInt("material_price");
                String materialDescription = resultSet.getString("material_description");

                Material material = new Material(material_id, width, depth, type, materialPrice, unit, materialDescription);
                MaterialVariant materialVariant = new MaterialVariant(materialVariantId, length, material);
                materialVariants.add(materialVariant);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not get users from the database", e.getMessage());
        }
        return materialVariants;
    }

    public static Material getMaterialById(int materialId, ConnectionPool connectionPool) {
        Material material = null;
        String sql = "SELECT * FROM material WHERE material_id = ?";
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int width = rs.getInt("width");
                int depth = rs.getInt("depth");
                String type = rs.getString("type");
                double materialPrice = rs.getDouble("material_price");
                String unit = rs.getString("unit");
                String materialDescription = rs.getString("material_description");
                material = new Material(materialId, width, depth, type, materialPrice, unit, materialDescription);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return material;
    }
    public static List<Material> getAllMaterials(ConnectionPool connectionPool) {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT material_id, width, depth, type, material_price, unit, material_description FROM material";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int materialId = resultSet.getInt("material_id");
                int width = resultSet.getInt("width");
                int depth = resultSet.getInt("depth");
                String type = resultSet.getString("type");
                int materialPrice = resultSet.getInt("material_price");
                String unit = resultSet.getString("unit");
                String materialDescription = resultSet.getString("material_description");

                Material material = new Material(materialId, width, depth, type, materialPrice, unit, materialDescription);
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Proper error handling should be implemented
        }

        return materials;
    }

}
