
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

import Bean.QuizAttempt;
import DB.DBConnector;
import DB.QuizAttemptDAO;
import org.junit.jupiter.api.*;
import java.util.*;

/**
 * Unit tests for QuizAttempt bean.
 */
public class QuizAttemptTest {

    @Test
    public void testDefaultConstructor() {
        QuizAttempt attempt = new QuizAttempt();
        assertEquals(0, attempt.getAttemptId());
        assertEquals(0, attempt.getUserId());
        assertEquals(0, attempt.getQuizId());
        assertEquals(0, attempt.getScore());
        assertEquals(0, attempt.getDurationSeconds());
        assertNull(attempt.getAttemptDate());
    }

    @Test
    public void testParamConstructor() {
        int userId = 42;
        int quizId = 100;
        int score = 85;
        int duration = 120;
        QuizAttempt attempt = new QuizAttempt(userId, quizId, score, duration);

        assertEquals(0, attempt.getAttemptId()); // not yet set
        assertEquals(userId, attempt.getUserId());
        assertEquals(quizId, attempt.getQuizId());
        assertEquals(score, attempt.getScore());
        assertEquals(duration, attempt.getDurationSeconds());
        assertNull(attempt.getAttemptDate());
    }

    @Test
    public void testGettersAndSetters() {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setAttemptId(7);
        attempt.setUserId(3);
        attempt.setQuizId(5);
        attempt.setScore(90);
        attempt.setDurationSeconds(300);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        attempt.setAttemptDate(now);

        assertEquals(7, attempt.getAttemptId());
        assertEquals(3, attempt.getUserId());
        assertEquals(5, attempt.getQuizId());
        assertEquals(90, attempt.getScore());
        assertEquals(300, attempt.getDurationSeconds());
        assertEquals(now, attempt.getAttemptDate());
    }

    @Test
    public void testToString() {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setAttemptId(1);
        attempt.setUserId(2);
        attempt.setQuizId(3);
        attempt.setScore(75);
        attempt.setDurationSeconds(150);
        Timestamp time = Timestamp.valueOf("2025-07-10 12:34:56");
        attempt.setAttemptDate(time);

        String str = attempt.toString();
        assertTrue(str.contains("attemptId=1"));
        assertTrue(str.contains("userId=2"));
        assertTrue(str.contains("quizId=3"));
        assertTrue(str.contains("score=75"));
        assertTrue(str.contains("durationSeconds=150"));
        assertTrue(str.contains("attemptDate=" + time.toString()));
    }


}

