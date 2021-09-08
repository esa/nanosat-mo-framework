package esa.mo.apps.autonomy.classify;

public enum InterestClassification {
    VOLCANO("Volcano"),
    REEF("Reef"),
    HUMAN_MADE("Human-made"),
    LAND("Land"),
    WATER("Water"),
    ERROR("Error");

    private String interest;

    InterestClassification(String interest) {
        this.interest = interest;
    }

    @Override
    public String toString() {
        return interest;
    }

    public static InterestClassification fromString(String interest) {
        for (InterestClassification classification : InterestClassification.values()) {
            if (classification.interest.equalsIgnoreCase(interest)) {
                return classification;
            }
        }
        return null;
    }
}
