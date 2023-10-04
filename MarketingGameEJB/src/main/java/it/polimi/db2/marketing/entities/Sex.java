package it.polimi.db2.marketing.entities;

public enum Sex {
    F('F'), M('M'), U('U');

    private final char value;
    Sex(char c) {
        this.value = c;
    }

    public char getValue() {
        return value;
    }

    public static Sex getSexFromChar(char value) {
        switch (value){
            case 'F': return F;
            case 'M': return M;
            default: return U;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
