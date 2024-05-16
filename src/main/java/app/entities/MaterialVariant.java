package app.entities;

public class MaterialVariant {
private int materialVariantId;
private int length;
private Material material;


    public MaterialVariant(int materialVariantId, int length, Material material) {
        this.materialVariantId = materialVariantId;
        this.length = length;
        this.material = material;
    }

    public MaterialVariant(int materialVariantId, int length){
        this.materialVariantId = materialVariantId;
        this.length = length;
    }

    public int getMaterialVariantId() {
        return materialVariantId;
    }

    public int getLength() {
        return length;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public String toString() {
        return "MaterialVariant{" +
                "materialVariantId=" + materialVariantId +
                ", length=" + length +
                ", material=" + material +
                '}';
    }
}
