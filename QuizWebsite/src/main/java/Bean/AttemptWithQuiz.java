package Bean;

public class AttemptWithQuiz {
    private QuizAttempt attempt;
    private Quiz quiz;
    public AttemptWithQuiz(QuizAttempt attempt, Quiz quiz) {
        this.attempt = attempt;
        this.quiz = quiz;
    }
    public QuizAttempt getAttempt() { return attempt; }
    public Quiz getQuiz() { return quiz; }
} 