package app.entities;
// Here we have a object that get the type of materials

import java.util.Objects;

public class Material {
    private int materialId;
    private int width;
    private int depth;
    private String type;
    private double materialPrice;
    private String unit;
    private String materialDescription;



    public Material(int materialId, Integer width, Integer depth, String type, double materialPrice, String unit, String materialDescription) {
        this.materialId = materialId;
        this.width = width;
        this.depth = depth;
        this.type = type;
        this.materialPrice = materialPrice;
        this.unit = unit;
        this.materialDescription = materialDescription;
    }



    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMaterialPrice() {
        return materialPrice;
    }

    public void setMaterialPrice(double materialPrice) {
        this.materialPrice = materialPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }


    @Override
    public String toString() {
        return "Material{" +
                "materialId=" + materialId +
                ", width=" + width +
                ", depth=" + depth +
                ", type='" + type + '\'' +
                ", materialPrice=" + materialPrice +
                ", unit='" + unit + '\'' +
                ", materialDescription='" + materialDescription + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Material material)) return false;
        return materialId == material.materialId && width == material.width && depth == material.depth && Double.compare(materialPrice, material.materialPrice) == 0 && Objects.equals(type, material.type) && Objects.equals(unit, material.unit) && Objects.equals(materialDescription, material.materialDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId, width, depth, type, materialPrice, unit, materialDescription);
    }
}
