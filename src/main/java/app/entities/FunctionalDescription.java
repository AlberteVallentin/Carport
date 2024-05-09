package app.entities;

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
}
