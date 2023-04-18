CREATE TABLE credit_scores (
    id SERIAL PRIMARY KEY not null,
    firstname varchar(50) not null,
    lastname varchar(50) not null,
    credit_score integer not null,
    timestamp timestamp default current_timestamp not null
);

INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('John', 'Smith', 80);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('Ann', 'Johnson', 50);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('Henry', 'Smith', 67);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('Emile', 'Miller', 82);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('Mike', 'Smith', 57);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('John', 'Johnson', 83);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('Ann', 'Miller', 51);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('Henry', 'Smith', 72);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('Emile', 'Smith', 82);
INSERT INTO credit_scores (firstname, lastname, credit_score) VALUES
    ('Mike', 'Johnson', 64);