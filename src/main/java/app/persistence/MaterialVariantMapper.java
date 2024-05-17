package app.persistence;

import app.entities.BillOfMaterialLine;
import app.entities.Material;
import app.entities.MaterialVariant;
import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialVariantMapper {

    public static List<MaterialVariant> getAllVariantsByMaterialId(int materialId, ConnectionPool connectionPool) throws DatabaseException {
        List<MaterialVariant> materialVariants = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM material_variant WHERE material_id = ?");
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

    public static MaterialVariant getMaterialVariantById(int materialVariantId, ConnectionPool connectionPool) throws DatabaseException {
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM material_variant WHERE material_variant_id = ?");
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




