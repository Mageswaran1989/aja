CREATE DATABASE aja;
USE aja;
SHOW DATABASES;

CREATE TABLE corporation
(corp_id SMALLINT,
name VARCHAR(30),
CONSTRAINT pk_corporation PRIMARY KEY (corp_id)
);

INSERT INTO corporation (corp_id, name)
VALUES (27, 'Acme Paper Corporation');

SELECT name FROM corporation WHERE corp_id = 27;
