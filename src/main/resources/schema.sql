DROP TABLE IF EXISTS roles;
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_roles_role_name UNIQUE (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(80) NOT NULL,
    mobile VARCHAR(11) NOT NULL,
    enabled TINYINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    account_non_expired TINYINT NOT NULL,
    account_non_locked TINYINT NOT NULL,
    credentials_non_expired TINYINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_mobile UNIQUE (mobile),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS users_roles;
CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user_id_users_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_users_roles_role_id_roles_id FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users(id, username, mobile, password_hash, enabled, account_non_expired, account_non_locked, credentials_non_expired, email)
            VALUES (1, 'root', '00000000000', '{bcrypt}$2a$10$V7Pd6zlCA8EDefp3nTOvwO3IT7UgZ9UGjJ0Lb/ttUXb4SS948qroe', 1, 1, 1, 1, 'dorohedoro@163.com'),
                   (2, 'jiaozi', '11111111111', '{SHA-1}7ce0359f12857f2a90c7de465f40a95f01cb5da9', 1, 1, 1, 1, 'jiaozi@163.com');
INSERT INTO roles(id, role_name) VALUES (1, 'ROLE_USER'), (2, 'ROLE_ADMIN');
INSERT INTO users_roles(user_id, role_id) VALUES (1, 1), (1, 2), (2, 1);