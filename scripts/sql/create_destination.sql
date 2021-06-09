CREATE DATABASE IF NOT EXISTS olddestination;
USE olddestination;
CREATE TABLE IF NOT EXISTS transfer_data(
	id INT,
    content TEXT
);
SELECT COUNT(*) FROM transfer_data