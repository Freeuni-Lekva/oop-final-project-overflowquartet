package Bean;

import java.util.List;

/**
 * Represents a question in a quiz. Supports multiple types (question-response, fill-in-the-blank, multiple choice, picture-response, etc.).
 */
public class Question {
    private int questionId;
    private int quizId;
    private String type; // e.g., "QUESTION_RESPONSE", "FILL_IN_BLANK", "MULTIPLE_CHOICE", "PICTURE_RESPONSE"
    private String prompt;
    private String imageUrl; // For picture-response questions
    private List<String> choices; // For multiple choice questions
    private List<String> correctAnswers; // For all types
    private int order; // Order of the question in the quiz

    public Question() {}

    public Question(int questionId, int quizId, String type, String prompt, String imageUrl, List<String> choices, List<String> correctAnswers, int order) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.type = type;
        this.prompt = prompt;
        this.imageUrl = imageUrl;
        this.choices = choices;
        this.correctAnswers = correctAnswers;
        this.order = order;
    }

    public int getQuestionId() {
        return questionId;
    }
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    public int getQuizId() {
        return quizId;
    }
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public List<String> getChoices() {
        return choices;
    }
    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }
    public void setCorrectAnswers(List<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
} 