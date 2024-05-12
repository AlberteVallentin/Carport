package app.services;

import app.entities.BillOfMaterialLine;
import app.entities.MaterialVariant;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

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

    public int calcPostQuantity(){
        int quantity;
        if (length<=420){
            quantity = 4;
        }
        else {
            quantity = 6;
        }
        return quantity;
    }
    //Remme
    private void calcBeams(Order order){
        //Beregn antal remme

        //Find variant

    }
    //Spær
    private void calcRafters(Order order){
        //Beregn antal remme

        //Find variant

    }

    public List<BillOfMaterialLine> getBomLine() {
        return bomLine;
    }
}
