package app.entities;

import java.util.ArrayList;
import java.util.List;

public class OrderLine {
  private List<Order> orderList= new ArrayList<>();

  private List<Order> getOrderList(){
      return orderList;

  }

  public void addOrder(Order order){
      orderList.add(order);
  }


}
