CREATE TABLE users (
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    token VARCHAR(100),
    token_expired_at BIGINT,
    PRIMARY KEY (username),
    UNIQUE (token)
);

CREATE TABLE todos (
    id BIGSERIAL,
    judul VARCHAR(255) NOT NULL,
    deskripsi TEXT,
    status VARCHAR(50) NOT NULL,
    tenggat_waktu DATE,
    username VARCHAR(100),
    dibuat_pada TIMESTAMP,
    diperbarui_pada TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (username) REFERENCES users(username)
);
