package app.entities;

import java.util.Objects;

public class BillOfMaterialLine {
    private int billOfMaterialLineId;
    private Order order;
    private MaterialVariant materialVariant;
    private int quantity;
    private static int functionalDescriptionId;
    private String functionalDescription;

    public BillOfMaterialLine(Order order, MaterialVariant materialVariant, int quantity, int functionalDescriptionId) {
        this.order = order;
        this.materialVariant = materialVariant;
        this.quantity = quantity;
        this.functionalDescriptionId = functionalDescriptionId;
    }

    public BillOfMaterialLine(Order order, MaterialVariant materialVariant, int quantity, String functionalDescription) {
        this.order = order;
        this.materialVariant = materialVariant;
        this.quantity = quantity;
        this.functionalDescription = functionalDescription;
    }


    @Override
    public String toString() {
        return "BillOfMaterialLine{" +
                "billOfMaterialLineId=" + billOfMaterialLineId +
                ", order=" + order +
                ", materialVariant=" + materialVariant +
                ", quantity=" + quantity +
                ", functionalDescriptionId=" + functionalDescriptionId +
                ", functionalDescription='" + functionalDescription + '\'' +
                '}';
    }


    public BillOfMaterialLine( Order order, MaterialVariant materialVariant, int quantity, int functionalDescriptionId, String functionalDescription) {
        this.order = order;
        this.materialVariant = materialVariant;
        this.quantity = quantity;
        this.functionalDescriptionId = functionalDescriptionId;
        this.functionalDescription = functionalDescription;
    }


    public BillOfMaterialLine(int billOfMaterialLineId, Order order, MaterialVariant materialVariant, int quantity, String functionalDescription) {
        this.billOfMaterialLineId = billOfMaterialLineId;
        this.order = order;
        this.materialVariant = materialVariant;
        this.quantity = quantity;
        this.functionalDescription = functionalDescription;
    }

    public int getBillOfMaterialLineId() {
        return billOfMaterialLineId;
    }

    public void setBillOfMaterialLineId(int billOfMaterialLineId) {
        this.billOfMaterialLineId = billOfMaterialLineId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MaterialVariant getMaterialVariant() {
        return materialVariant;
    }

    public void setMaterialVariant(MaterialVariant materialVariant) {
        this.materialVariant = materialVariant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static int getFunctionalDescriptionId() {
        return functionalDescriptionId;
    }

    public void setFunctionalDescriptionId(int functionalDescriptionId) {
        this.functionalDescriptionId = functionalDescriptionId;
    }

    public String getFunctionalDescription() {
        return functionalDescription;
    }

    public void setFunctionalDescription(String functionalDescription) {
        this.functionalDescription = functionalDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillOfMaterialLine that)) return false;
        return billOfMaterialLineId == that.billOfMaterialLineId && quantity == that.quantity && Objects.equals(order, that.order) && Objects.equals(materialVariant, that.materialVariant) && Objects.equals(functionalDescription, that.functionalDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billOfMaterialLineId, order, materialVariant, quantity, functionalDescription);
    }
}
