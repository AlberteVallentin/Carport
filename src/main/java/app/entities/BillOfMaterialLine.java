package app.entities;

public class BillOfMaterialLine {
    private int billOfMaterialLineId;
    private Order order;
    private MaterialVariant materialVariant;
    private int quantity;
    private int functionalDescriptionId;

    public BillOfMaterialLine(Order order, MaterialVariant materialVariant, int quantity, int functionalDescriptionId) {
        this.order = order;
        this.materialVariant = materialVariant;
        this.quantity = quantity;
        this.functionalDescriptionId = functionalDescriptionId;
    }

    public BillOfMaterialLine(int billOfMaterialLineId, Order order, MaterialVariant materialVariant, int quantity, int functionalDescriptionId) {
        this.billOfMaterialLineId = billOfMaterialLineId;
        this.order = order;
        this.materialVariant = materialVariant;
        this.quantity = quantity;
        this.functionalDescriptionId = functionalDescriptionId;
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

    public int getFunctionalDescriptionId() {
        return functionalDescriptionId;
    }

    public void setFunctionalDescriptionId(int functionalDescriptionId) {
        this.functionalDescriptionId = functionalDescriptionId;
    }
}
