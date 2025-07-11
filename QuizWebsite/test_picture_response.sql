-- Test script for picture-response questions
-- This script adds some example picture-response questions to test the functionality

USE quiz_db;

-- First, let's create a test user if it doesn't exist
INSERT IGNORE INTO users (username, password_hash, display_name) 
VALUES ('testuser', 'hashedpassword', 'Test User');

-- Get the user ID
SET @user_id = (SELECT user_id FROM users WHERE username = 'testuser' LIMIT 1);

-- Create a test quiz for picture-response questions
INSERT INTO quizzes (owner_id, title, description, random_order, multiple_pages, immediate_correction) 
VALUES (@user_id, 'Picture Response Test Quiz', 'A quiz to test picture-response questions', FALSE, TRUE, FALSE);

SET @quiz_id = LAST_INSERT_ID();

-- Add some picture-response questions with example images

-- Question 1: Bird identification
INSERT INTO questions (quiz_id, question_type, question_text, image_url, question_order) 
VALUES (@quiz_id, 'picture_response', 'What type of bird is shown in this image?', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Eopsaltria_australis_-_Mogo_Campground.jpg/800px-Eopsaltria_australis_-_Mogo_Campground.jpg', 1);

SET @question_id = LAST_INSERT_ID();

INSERT INTO question_answers (question_id, answer_text) VALUES 
(@question_id, 'Eastern Yellow Robin'),
(@question_id, 'Yellow Robin'),
(@question_id, 'Robin');

-- Question 2: US President identification
INSERT INTO questions (quiz_id, question_type, question_text, image_url, question_order) 
VALUES (@quiz_id, 'picture_response', 'Which US President is shown in this image?', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/George_Washington_by_John_Trumbull_1780.jpg/800px-George_Washington_by_John_Trumbull_1780.jpg', 2);

SET @question_id = LAST_INSERT_ID();

INSERT INTO question_answers (question_id, answer_text) VALUES 
(@question_id, 'George Washington'),
(@question_id, 'Washington');

-- Question 3: Chemical structure (molecule name)
INSERT INTO questions (quiz_id, question_type, question_text, image_url, question_order) 
VALUES (@quiz_id, 'picture_response', 'What is the name of this molecule?', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Glucose_Fisher_to_Haworth.gif/800px-Glucose_Fisher_to_Haworth.gif', 3);

SET @question_id = LAST_INSERT_ID();

INSERT INTO question_answers (question_id, answer_text) VALUES 
(@question_id, 'Glucose'),
(@question_id, 'D-Glucose'),
(@question_id, 'Dextrose');

-- Question 4: Stanford building (as mentioned in requirements)
INSERT INTO questions (quiz_id, question_type, question_text, image_url, question_order) 
VALUES (@quiz_id, 'picture_response', 'What building at Stanford is shown in this image?', 'http://events.stanford.edu/events/252/25201/Memchu_small.jpg', 4);

SET @question_id = LAST_INSERT_ID();

INSERT INTO question_answers (question_id, answer_text) VALUES 
(@question_id, 'Memorial Church'),
(@question_id, 'Memchu'),
(@question_id, 'Stanford Memorial Church');

-- Add a regular question for comparison
INSERT INTO questions (quiz_id, question_type, question_text, image_url, question_order) 
VALUES (@quiz_id, 'question_response', 'What is the capital of France?', NULL, 5);

SET @question_id = LAST_INSERT_ID();

INSERT INTO question_answers (question_id, answer_text) VALUES 
(@question_id, 'Paris');

-- Display the results
SELECT 'Test quiz created with ID:' as message, @quiz_id as quiz_id;

SELECT 'Picture-response questions added:' as message;
SELECT question_id, question_text, image_url, question_type 
FROM questions 
WHERE quiz_id = @quiz_id 
ORDER BY question_order; 