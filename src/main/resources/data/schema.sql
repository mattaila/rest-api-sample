DROP TABLE IF EXISTS tasks;
CREATE TABLE tasks (
    id serial PRIMARY KEY,
    title VARCHAR(256) NOT NULL,
    comment TEXT,
    progress integer NOT NULL,
    deadline DATE
)