
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(30) UNIQUE,
    password VARCHAR(30) NOT NULL,
    email VARCHAR(60) UNIQUE,
    reputation INT,
    date_joined DATE,
    icon bytea
);

CREATE TABLE IF NOT EXISTS language (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE
);

CREATE TABLE IF NOT EXISTS tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE
);

CREATE TABLE IF NOT EXISTS snippets (
    id SERIAL PRIMARY KEY ,
    user_id BIGINT UNSIGNED REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL,
    title VARCHAR(50),
    description VARCHAR(300),
    code VARCHAR(500),
    date_created DATE,
    language_id BIGINT UNSIGNED REFERENCES language(id) ON UPDATE CASCADE ON DELETE SET NULL,
);

CREATE TABLE IF NOT EXISTS votes_for (
    user_id BIGINT UNSIGNED REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL,
    snippet_id BIGINT UNSIGNED REFERENCES snippets(id) ON UPDATE CASCADE ON DELETE CASCADE,
    type INT,
    PRIMARY KEY(user_id, snippet_id)
);

CREATE TABLE IF NOT EXISTS favorites (
    snippet_id BIGINT UNSIGNED REFERENCES snippets(id) ON UPDATE CASCADE ON DELETE CASCADE,
    user_id BIGINT UNSIGNED REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(snippet_id, user_id)
);

CREATE TABLE IF NOT EXISTS follows (
    user_id BIGINT UNSIGNED REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    tag_id BIGINT UNSIGNED REFERENCES tags(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(user_id, tag_id)
);

CREATE TABLE IF NOT EXISTS snippet_tags (
    snippet_id BIGINT UNSIGNED REFERENCES snippets(id) ON UPDATE CASCADE ON DELETE CASCADE,
    tag_id BIGINT UNSIGNED REFERENCES tags(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(snippet_id, tag_id)
);

