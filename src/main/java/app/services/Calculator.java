package app.services;

import app.entities.BillOfMaterialLine;
import app.entities.FunctionalDescription;
import app.entities.MaterialVariant;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;
import app.persistence.MaterialVariantMapper;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private static final int POSTS = 3;
    private static final int RAFTERS = 4;
    private static final int BEAMS = 4;
    private List<BillOfMaterialLine> bomLine= new ArrayList<>();
    private int widht;
    private int length;
    private ConnectionPool connectionPool;

    public Calculator(int widht, int length, ConnectionPool connectionPool) {
        this.widht = widht;
        this.length = length;
        this.connectionPool = connectionPool;
    }

    public void calcCarport(Order order) throws DatabaseException {
        calcPost(order);
        calcBeams(order);
        calcRafters(order);
    }

    //Stolper
    private void calcPost(Order order) throws DatabaseException {
        //Beregn antal stolper
        int quantity;
        if (length<=420){
            quantity = 4;
        }
        else {
            quantity = 6;
        }
        List<MaterialVariant> materialVariants = MaterialMapper.getMaterialsByProductIdAndMinLength(0, POSTS, connectionPool);
        MaterialVariant materialVariant = materialVariants.get(0);
        BillOfMaterialLine billOfMaterialLine = new BillOfMaterialLine(0, order, materialVariant, quantity,7);
        bomLine.add(billOfMaterialLine);
    }

    //Remme
    public void calcBeams(Order order){
        int quantity;
        int variantId=0;
        //TODO: find functional description id for the material variant
        int length = order.getCpLength();

        //Beregn antal remme
        if(length>600){
            quantity = 4;
        }
        else{
            quantity = 2;
        }
        int variantLengt = Integer.MAX_VALUE;

        //Find variant
        List <MaterialVariant> materialVariants = MaterialVariantMapper.getAllVariantsByMaterialId(4, connectionPool);

        if(length>600) {
            for (MaterialVariant m : materialVariants) {
                if (m.getLength() >= length/2 && m.getLength() < variantLengt) {
                    variantLengt = m.getLength();
                    variantId = m.getMaterialVariantId();
                }
            }
        }
        else{
            for (MaterialVariant m : materialVariants) {
                if (m.getLength() >= length && m.getLength() < variantLengt) {
                    variantLengt = m.getLength();
                    variantId = m.getMaterialVariantId();
                }
            }
        }
        System.out.println(variantLengt);
        System.out.println(variantId);
    }

    //Spær
    private void calcRafters(Order order){
        //Beregn antal remme
        int quantity = length/60;
        //Find variant

    }

    public List<BillOfMaterialLine> getBomLine() {
        return bomLine;
    }
}
