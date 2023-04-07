CREATE TABLE clients (
    id SERIAL PRIMARY KEY not null,
    firstname varchar(50) not null,
    lastname varchar(50) not null,
    credit_score integer not null
);

INSERT INTO clients (firstname, lastname, credit_score) VALUES
    ('John', 'Smith', 80);
INSERT INTO clients (firstname, lastname, credit_score) VALUES
    ('Ann', 'Smith', 50);
INSERT INTO clients (firstname, lastname, credit_score) VALUES
    ('Henry', 'Smith', 67);
INSERT INTO clients (firstname, lastname, credit_score) VALUES
    ('Emile', 'Smith', 82);