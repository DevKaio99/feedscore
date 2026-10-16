package domain.enums;

public enum Urgency {

    CRITICAL(0, 4, "Crítica"),
    MEDIUM(5, 7, "Média"),
    LOW(8, 10, "Baixa");

    private final int minScore;
    private final int maxScore;
    private final String label;

    Urgency(int minScore, int maxScore, String label) {
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.label = label;
    }

    public static Urgency fromScore(int score) {
        for (Urgency urgency : values()) {
            if (score >= urgency.minScore && score <= urgency.maxScore) {
                return urgency;
            }
        }
        throw new IllegalArgumentException("Nota fora da faixa de urgência: " + score);
    }

    public String getLabel() {
        return label;
    }
}
