USE quiz_db;

-- DROP all tables in reverse order of dependencies
DROP TABLE IF EXISTS attempt_answers;
DROP TABLE IF EXISTS quiz_attempts;
DROP TABLE IF EXISTS question_choices;
DROP TABLE IF EXISTS question_answers;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS users;

-- USERS
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       display_name VARCHAR(100),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       last_login TIMESTAMP NULL
);

-- QUIZZES
CREATE TABLE quizzes (
                         quiz_id INT AUTO_INCREMENT PRIMARY KEY,
                         owner_id INT NOT NULL,
                         title VARCHAR(100) NOT NULL,
                         description TEXT,
                         random_order BOOLEAN DEFAULT FALSE,
                         multiple_pages BOOLEAN DEFAULT FALSE,
                         immediate_correction BOOLEAN DEFAULT FALSE,
                         creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

-- QUESTIONS
CREATE TABLE questions (
                           question_id INT AUTO_INCREMENT PRIMARY KEY,
                           quiz_id INT NOT NULL,
                           question_type ENUM('question_response', 'fill_blank', 'multiple_choice', 'picture_response') NOT NULL,
                           question_text TEXT NOT NULL,
                           image_url TEXT,
                           question_order INT,
                           FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id)
);

-- QUESTION ANSWERS
CREATE TABLE question_answers (
                                  answer_id INT AUTO_INCREMENT PRIMARY KEY,
                                  question_id INT NOT NULL,
                                  answer_text TEXT NOT NULL,
                                  FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

-- MULTIPLE CHOICE OPTIONS
CREATE TABLE question_choices (
                                  choice_id INT AUTO_INCREMENT PRIMARY KEY,
                                  question_id INT NOT NULL,
                                  choice_text TEXT NOT NULL,
                                  is_correct BOOLEAN DEFAULT FALSE,
                                  FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

-- QUIZ ATTEMPTS
CREATE TABLE quiz_attempts (
                               attempt_id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               quiz_id INT NOT NULL,
                               score INT,
                               duration_seconds INT,
                               attempt_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id),
                               FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id)
);

-- ATTEMPT ANSWERS
CREATE TABLE attempt_answers (
                                 attempt_answer_id INT AUTO_INCREMENT PRIMARY KEY,
                                 attempt_id INT NOT NULL,
                                 question_id INT NOT NULL,
                                 user_answer_text TEXT,
                                 is_correct BOOLEAN,
                                 FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(attempt_id),
                                 FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

-- FRIENDS
CREATE TABLE friends (
                         user_id INT NOT NULL,
                         friend_id INT NOT NULL,
                         status ENUM('pending', 'accepted', 'rejected') NOT NULL,
                         request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (user_id, friend_id),
                         FOREIGN KEY (user_id) REFERENCES users(user_id),
                         FOREIGN KEY (friend_id) REFERENCES users(user_id)
);

-- MESSAGES
CREATE TABLE messages (
                          message_id INT AUTO_INCREMENT PRIMARY KEY,
                          sender_id INT NOT NULL,
                          receiver_id INT NOT NULL,
                          message_type ENUM('friend_request', 'challenge', 'note') NOT NULL,
                          content TEXT,
                          is_read BOOLEAN DEFAULT FALSE,
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (sender_id) REFERENCES users(user_id),
                          FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);

CREATE TABLE answers (
                         answer_id INT AUTO_INCREMENT PRIMARY KEY,
                         question_id INT NOT NULL,
                         user_id     INT NOT NULL,
                         response_text       TEXT,
                         response_option_id  INT NULL,
                         timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (question_id)       REFERENCES questions(question_id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE,
                         FOREIGN KEY (user_id)           REFERENCES users(user_id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS achievements (
                                            achievement_id INT AUTO_INCREMENT PRIMARY KEY,
                                            name VARCHAR(100) NOT NULL,
                                            description TEXT,
                                            icon_url TEXT
);


CREATE TABLE IF NOT EXISTS user_achievements (
                                                 user_id INT NOT NULL,
                                                 achievement_id INT NOT NULL,
                                                 earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                 PRIMARY KEY (user_id, achievement_id),
                                                 FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                                 FOREIGN KEY (achievement_id) REFERENCES achievements(achievement_id) ON DELETE CASCADE
);


-- Sample users for testing login
INSERT INTO users (username, password_hash, display_name) VALUES
('alice', '4e40e8ffe0ee32fa53e139147ed559229a5930f89c2204706fc174beb36210b3', 'Alice Wonderland'),
('charlie', '2c1743a391305fbf367df8e4f069f9f9a44fbdc7e6a8e6a8e6a8e6a8e6a8e6a8e', 'Charlie Chaplin');
