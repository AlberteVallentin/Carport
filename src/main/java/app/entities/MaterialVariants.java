package app.entities;

public class MaterialVariants {
private int materialVariantId;
private int length;
private int materialId;

    public MaterialVariants(int materialVariantId, int length, int materialId) {
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
