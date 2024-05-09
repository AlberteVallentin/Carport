package app.entities;

public class BillOfMaterialLine {
    private int billOfMaterialLineId;
    private Order order;
    private MaterialVariants materialVariant;
    private int quantity;
    private int functionalDescriptionId;

    public BillOfMaterialLine(int billOfMaterialLineId, Order order, MaterialVariants materialVariant, int quantity, int functionalDescriptionId) {
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

    public MaterialVariants getMaterialVariant() {
        return materialVariant;
    }

    public void setMaterialVariant(MaterialVariants materialVariant) {
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
