package it.polimi.db2.marketing.entities;

public enum ExpertiseLevel {
    LOW(0), MEDIUM(1), HIGH(2), UNSPECIFIED(-1);

    private int value;
    ExpertiseLevel(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }

    public static ExpertiseLevel getExpertiseLevelFromInt(int value){
        switch (value){
            case 0: return LOW;
            case 1: return MEDIUM;
            case 2: return HIGH;
            default: return null;
        }
    }
    public static ExpertiseLevel getExpertiseLevelFromString(String value){
        switch (value){
            case "LOW": return LOW;
            case "MEDIUM": return MEDIUM;
            case "HIGH": return HIGH;
            default: return null;
        }
    }
}
