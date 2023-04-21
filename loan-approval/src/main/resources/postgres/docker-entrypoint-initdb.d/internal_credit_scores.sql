CREATE TABLE credit_scores (
    id SERIAL PRIMARY KEY not null,
    firstname varchar(50) not null,
    lastname varchar(50) not null,
    credit_score integer not null,
    social_secure_number varchar(10) not null,
    timestamp timestamp default current_timestamp not null
);

INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('John', 'Smith', 80, '123456789');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('Ann', 'Johnson', 50, '123456788');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('Henry', 'Smith', 67, '123456787');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('Emile', 'Miller', 82, '123456786');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('Mike', 'Smith', 57, '123456785');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('John', 'Johnson', 83, '123456784');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('Ann', 'Miller', 51, '123456783');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('Henry', 'Miller', 72, '123456782');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('Emile', 'Smith', 82, '123456781');
INSERT INTO credit_scores (firstname, lastname, credit_score, social_secure_number) VALUES
    ('Mike', 'Johnson', 64, '123456780');