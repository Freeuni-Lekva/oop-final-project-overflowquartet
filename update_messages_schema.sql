-- Update messages table to add quiz_id column for challenge messages
USE quiz_db;

-- Add quiz_id column to messages table
ALTER TABLE messages ADD COLUMN quiz_id INT NULL;
ALTER TABLE messages ADD CONSTRAINT fk_messages_quiz_id FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id);

-- Update existing challenge messages to extract quiz_id from content if possible
-- This is a safety measure in case there are existing challenge messages
UPDATE messages 
SET quiz_id = CAST(content AS UNSIGNED) 
WHERE message_type = 'challenge' 
  AND content REGEXP '^[0-9]+$' 
  AND quiz_id IS NULL; 