CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    email TEXT NOT NULL UNIQUE CHECK (char_length(email) > 0),

    username TEXT NOT NULL CHECK (char_length(username) > 0),

    password TEXT NOT NULL CHECK (char_length(password) >= 8),

    role TEXT DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),

    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,

    name TEXT NOT NULL CHECK (char_length(name) > 0),

    owner_id BIGINT NOT NULL,

    created_at TIMESTAMP DEFAULT now(),

    CONSTRAINT fk_project_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE issues (
    id BIGSERIAL PRIMARY KEY,

    title TEXT NOT NULL CHECK (char_length(title) > 0),

    description TEXT DEFAULT '',

    status TEXT DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'DONE')),

    priority TEXT DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

    project_id BIGINT NOT NULL,

    owner_id BIGINT,

    created_at TIMESTAMP DEFAULT now(),

    CONSTRAINT fk_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_issue_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);