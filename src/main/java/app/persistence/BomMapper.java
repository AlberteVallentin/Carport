package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BomMapper
{
    public static List<BillOfMaterialLine> getBomLines(int orderId, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "select * from bill_of_materials_view where order_id = ?";
        List<BillOfMaterialLine> getBomLines = new ArrayList<>();

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){

                int userId = rs.getInt("user_id");
                User user = UserMapper.getUserById(userId, connectionPool);
                if (user == null) {
                    throw new DatabaseException("No user found with the provided userId: " + userId);
                }

                int cpLength = rs.getInt("cp_length");
                int cpWidth = rs.getInt("cp_width");
                int shLength = rs.getInt("shed_length");
                int shWidth = rs.getInt("shed_width");
                int statusId = rs.getInt("status_id");
                String cpRoof = rs.getString("cp_roof");
                double price = rs.getDouble("price");

                Order order = new Order(orderId, price, user, cpLength, cpWidth, cpRoof, shLength, shWidth, statusId);

                int materialVariantId = rs.getInt("material_variant_id");
                int material_id = rs.getInt("material_id");
                int length = rs.getInt("length");
                String type = rs.getString("type");
                String unit = rs.getString("unit");

                int width = rs.getInt("width");
                int depth = rs.getInt("depth");
                int materialPrice = rs.getInt("material_price");
                String materialDescription = rs.getString("material_description");

                Material material = new Material(material_id, width, depth, type, materialPrice, unit, materialDescription);
                MaterialVariant materialVariant = new MaterialVariant(materialVariantId, length, material);

                int cpQuantity = rs.getInt("quanity");
                int cpFunctionalDescr = rs.getInt("functional_description_id");
                BillOfMaterialLine billOfMaterialLine = new BillOfMaterialLine(order, materialVariant, cpQuantity, cpFunctionalDescr);
                getBomLines.add(billOfMaterialLine);

            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl i getAllOrders");
        }
        return getBomLines;
    }
}
