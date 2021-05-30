CREATE DATABASE IF NOT EXISTS destination;
USE destination;
CREATE TABLE IF NOT EXISTS transfer_data(
	id INT,
    content TEXT
);
SELECT COUNT(*) FROM transfer_data