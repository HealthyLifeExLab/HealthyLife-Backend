CREATE TABLE IF NOT EXISTS users (
	id BIGSERIAL PRIMARY KEY,
	email VARCHAR (50) NOT NULL UNIQUE,
	password VARCHAR (50) NOT NULL,
	deleted BOOLEAN DEFAULT FALSE NOT NULL
);
