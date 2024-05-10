package app.entities;

public class MaterialVariant {
private int materialVariantId;
private int length;
private int materialId;

    public MaterialVariant(int materialVariantId, int length, int materialId) {
        this.materialVariantId = materialVariantId;
        this.length = length;
        this.materialId = materialId;
    }

    public int getMaterialVariantId() {
        return materialVariantId;
    }

    public int getLength() {
        return length;
    }

    public int getMaterialId() {
        return materialId;
    }
}
