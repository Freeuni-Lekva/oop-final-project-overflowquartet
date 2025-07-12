-- Script to check for and handle the announcements table issue
USE quiz_db;

-- Check if announcements table exists
SELECT COUNT(*) as announcements_table_exists 
FROM information_schema.tables 
WHERE table_schema = 'quiz_db' AND table_name = 'announcements';

-- If the table exists, show its structure
SHOW CREATE TABLE announcements;

-- Show foreign key constraints on announcements table
SELECT 
    CONSTRAINT_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'quiz_db' 
    AND TABLE_NAME = 'announcements' 
    AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Optionally, if you want to drop the announcements table:
-- DROP TABLE IF EXISTS announcements; 