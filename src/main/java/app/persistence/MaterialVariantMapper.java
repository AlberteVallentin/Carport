package app.persistence;

import app.entities.Material;
import app.entities.MaterialVariant;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialVariantMapper {

    /**
     * Retrieves all material variants by material ID from the database.
     *
     * @param materialId      The ID of the material to retrieve variants for.
     * @param connectionPool  The connection pool for database connections.
     * @return A list of material variants associated with the specified material ID.
     * @throws DatabaseException If a database error occurs.
     */
    public static List<MaterialVariant> getAllVariantsByMaterialId(int materialId, ConnectionPool connectionPool) throws DatabaseException {
        List<MaterialVariant> materialVariants = new ArrayList<>();
        String sql = "SELECT * FROM material_variant WHERE material_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int materialVariantId = rs.getInt("material_variant_id");
                int length = rs.getInt("length");

                // Fetch the Material object from the database
                Material material = MaterialMapper.getMaterialById(materialId, connectionPool);

                // Create the MaterialVariant object with the Material object
                MaterialVariant materialVariant = new MaterialVariant(materialVariantId, length, material);
                materialVariants.add(materialVariant);
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Could not get material variants", ex.getMessage());
        }

        return materialVariants;
    }

    /**
     * Retrieves a material variant by its ID from the database.
     *
     * @param materialVariantId The ID of the material variant to retrieve.
     * @param connectionPool    The connection pool for database connections.
     * @return The MaterialVariant object corresponding to the given ID.
     * @throws DatabaseException If a database error occurs.
     */
    public static MaterialVariant getMaterialVariantById(int materialVariantId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM material_variant WHERE material_variant_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, materialVariantId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int length = rs.getInt("length");
                int materialId = rs.getInt("material_id");

                // Fetch the Material object from the database
                Material material = MaterialMapper.getMaterialById(materialId, connectionPool);

                // Create the MaterialVariant object with the Material object
                return new MaterialVariant(materialVariantId, length, material);
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Could not get material variant", ex.getMessage());
        }

        return null;
    }
}




