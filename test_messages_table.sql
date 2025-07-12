-- Test script to check messages table structure
USE quiz_db;

-- Check if quiz_id column exists
DESCRIBE messages;

-- Check if there are any existing messages
SELECT COUNT(*) as message_count FROM messages;

-- Check if quiz_id column exists in the table structure
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'quiz_db' 
  AND TABLE_NAME = 'messages' 
  AND COLUMN_NAME = 'quiz_id'; 