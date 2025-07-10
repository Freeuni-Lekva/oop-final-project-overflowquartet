package Bean;

public class Choice {

    private int choiceId;
    private String choiceText;
    private boolean isCorrect;
    private int questionId;

    // --- Constructors ---

    /**
     * Default constructor.
     */
    public Choice() {
    }

    /**
     * Constructor to initialize all fields.
     * @param choiceText The text of the choice.
     * @param isCorrect  True if this choice is the correct answer.
     */
    public Choice(String choiceText, boolean isCorrect) {
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
    }


    // --- Getters and Setters ---

    public int getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(int choiceId) {
        this.choiceId = choiceId;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionId() {
        return questionId;
    }
}