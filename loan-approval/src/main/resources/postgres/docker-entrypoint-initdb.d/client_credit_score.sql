CREATE TABLE client_credit_score (
    id SERIAL PRIMARY KEY not null,
    firstname varchar(50) not null,
    lastname varchar(50) not null,
    credit_score integer not null
);

INSERT INTO client_credit_score (firstname, lastname, credit_score) VALUES
    ('John', 'Smith', 80);
INSERT INTO client_credit_score (firstname, lastname, credit_score) VALUES
    ('Ann', 'Smith', 50);
INSERT INTO client_credit_score (firstname, lastname, credit_score) VALUES
    ('Henry', 'Smith', 67);
INSERT INTO client_credit_score (firstname, lastname, credit_score) VALUES
    ('Emile', 'Smith', 82);
INSERT INTO client_credit_score (firstname, lastname, credit_score) VALUES
    ('Mike', 'Smith', 57);