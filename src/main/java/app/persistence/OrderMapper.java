package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderMapper {

    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException{
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders inner join users using(user_id)";
        try(
                Connection connection = connectionPool.getConnection();
                var prepareStatement = connection.prepareStatement(sql);
                var resultSet = prepareStatement.executeQuery();
                )
            {
            while(resultSet.next()){

                //Retreiving user attributes from DB
                int userId = resultSet.getInt("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String phoneNumber = resultSet.getString("phone_number");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                boolean isAdmin = resultSet.getBoolean("admin");
                int addressId = resultSet.getInt("address_id");

                //Retreiving order attributes from DB
                int orderId = resultSet.getInt("order_id");
                double price = resultSet.getDouble("price");
                String comment = resultSet.getString("comment");
                int shippingId = resultSet.getInt("shipping_id");
                int cpLength = resultSet.getInt("cp_length");
                int cpWidth = resultSet.getInt("cp_width");
                String cpRoof = resultSet.getString("cp_roof");
                int shedLength = resultSet.getInt("shed_length");
                int shedWidth = resultSet.getInt("shed_width");
                int statusId = resultSet.getInt("status_id");

                User user = new User(userId, firstName, lastName, phoneNumber, email, password, isAdmin, addressId);
                Order order = new Order(orderId, price, user, comment, shippingId, cpLength, cpWidth, cpRoof, shedLength, shedWidth, statusId);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderList;
    }

    public static List<BillOfMaterialLine> getOrderByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException
    {
        List<BillOfMaterialLine> BillOfMaterialLineList = new ArrayList<>();
        String sql = "SELECT * FROM bill_of_materials_view where order_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(sql);
        )
        {
            prepareStatement.setInt(1, orderId);
            var rs = prepareStatement.executeQuery();
            while (rs.next())
            {
                // Order
                int orderId2 = rs.getInt("order_id");
                double price = rs.getDouble("price");
                String comment = rs.getString("comment");
                int shippingId = rs.getInt("shipping_id");
                int cpLength = rs.getInt("cp_length");
                int cpWidth = rs.getInt("cp_width");
                String cpRoof = rs.getString("cp_roof");
                int shedLength = rs.getInt("shed_length");
                int shedWidth = rs.getInt("shed_width");
                int statusId = rs.getInt("status_id");

                Order order = new Order(orderId2, price, null, comment, shippingId, cpLength, cpWidth, cpRoof, shedLength, shedWidth, statusId);

                //Material
                int materialId = rs.getInt("material_id");
                String type = rs.getString("type");
                String unit = rs.getString("unit");
                int materialPrice = rs.getInt("material_price");

                Material material = new Material(materialId, null, null, type, materialPrice, unit, null);

                // material variant
                int materialVariantId = rs.getInt("material_variant_id");
                int functionalDescriptionId = rs.getInt("functional_description_id");
                int length = rs.getInt("length");

               MaterialVariant materialVariant = new MaterialVariant(materialVariantId, length, material);

                // BillOfMaterialLine
                int BillOfMaterialLineId = rs.getInt("bom_line_id");
                int quantity = rs.getInt("quantity");
                BillOfMaterialLine BillOfMaterialLine = new BillOfMaterialLine(BillOfMaterialLineId, order, materialVariant, quantity, functionalDescriptionId);
                BillOfMaterialLineList.add(BillOfMaterialLine);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not get users from the database", e.getMessage());
        }
        return BillOfMaterialLineList;
    }

    public static Order insertOrder(Order order, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO orders (carport_width, carport_length, status, user_id, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection())
        {
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
            {
                ps.setDouble(2, order.getPrice());
                ps.setInt(3, order.getUser().getUserId());
                ps.setString(4, order.getComment());
                ps.setInt(5, order.getShippingId());
                ps.setInt(6, order.getCpLength());
                ps.setInt(7, order.getCpWidth());
                ps.setInt(8, order.getShedLength());
                ps.setInt(9,order.getShedWidth());
                ps.setInt(10, order.getStatusId());
                ps.setString(11, order.getCpRoof());

                ps.executeUpdate();
                ResultSet keySet = ps.getGeneratedKeys();
                if (keySet.next())
                {
                    Order newOrder = new Order(keySet.getInt(1), order.getPrice(), order.getUser(), order.getComment(), order.getShippingId(), order.getCpLength(), order.getCpWidth(), order.getCpRoof(), order.getShedLength(), order.getShedWidth(), order.getStatusId());
                    return newOrder;
                } else
                    return null;
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not create order in the database", e.getMessage());
        }

    }

    public static void insertOrderItems(List<BillOfMaterialLine> billOfMaterialLines, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO bill_of_material_line (bom_line_id, order_id, material_variant_id, functional_description_id) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionPool.getConnection())
        {
            for (BillOfMaterialLine billOfMaterialLine : billOfMaterialLines)
            {
                try (PreparedStatement ps = connection.prepareStatement(sql))
                {
                    ps.setInt(1, billOfMaterialLine.getBillOfMaterialLineId());
                    ps.setInt(2, billOfMaterialLine.getOrder().getOrderId());
                    ps.setInt(3, billOfMaterialLine.getMaterialVariant().getMaterialId());
                    ps.setInt(4, billOfMaterialLine.getFunctionalDescriptionId());
                    ps.executeUpdate();
                }
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not create orderitem in the database", e.getMessage());
        }
    }

}
