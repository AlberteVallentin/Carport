package app.persistence;

import app.entities.BillOfMaterialLine;
import app.entities.Material;
import app.entities.MaterialVariant;
import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialVariantMapper {
    public static List<MaterialVariant>  getAllVariantsByMaterialId(int materialId, ConnectionPool connectionPool)
    {
        List <MaterialVariant> materialVariantList = new ArrayList<>();

        String sql = "SELECT * FROM material_variant where material_id = ?";

        try(
              Connection connection = connectionPool.getConnection();
              PreparedStatement preparedStatement= connection.prepareStatement(sql);
              )
        {
            preparedStatement.setInt(1,materialId);
            var rs=preparedStatement.executeQuery();
            while(rs.next()){


                int materialVariantId2 = rs.getInt("material_variant_id");
               int length = rs.getInt("length");


                MaterialVariant materialVariant = new MaterialVariant(materialVariantId2,length);

                materialVariantList.add(materialVariant);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return materialVariantList;
    }



}
