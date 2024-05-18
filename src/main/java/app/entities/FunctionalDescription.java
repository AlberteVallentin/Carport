package app.entities;

import java.util.Objects;

public class FunctionalDescription {
    private int functionalDescriptionId;
    private String functionalDescription;

    public FunctionalDescription(int functionalDescriptionId, String functionalDescription) {
        this.functionalDescriptionId = functionalDescriptionId;
        this.functionalDescription = functionalDescription;
    }

    public int getFunctionalDescriptionId() {
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
    public String toString() {
        return "FunctionalDescription{" +
                "functionalDescriptionId=" + functionalDescriptionId +
                ", functionalDescription='" + functionalDescription + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionalDescription that)) return false;
        return functionalDescriptionId == that.functionalDescriptionId && Objects.equals(functionalDescription, that.functionalDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionalDescriptionId, functionalDescription);
    }
}
