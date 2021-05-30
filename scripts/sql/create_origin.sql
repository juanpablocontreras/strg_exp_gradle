CREATE DATABASE IF NOT EXISTS origin;
USE origin;
CREATE TABLE IF NOT EXISTS transfer_data(
	id INT,
    content TEXT
);
SELECT COUNT(*) FROM transfer_data
