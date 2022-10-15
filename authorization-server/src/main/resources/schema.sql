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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS roles;
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    built_in TINYINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_roles_role_name UNIQUE (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

DROP TABLE IF EXISTS permissions;
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名,格式为资源_操作',
    display_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_permissions_permission_name UNIQUE (permission_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

DROP TABLE IF EXISTS users_roles;
CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT(20) NOT NULL,
    role_id BIGINT(20) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user_id_users_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_users_roles_role_id_roles_id FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';

DROP TABLE IF EXISTS roles_permissions;
CREATE TABLE IF NOT EXISTS roles_permissions (
    role_id BIGINT(20) NOT NULL,
    permission_id BIGINT(20) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_roles_permissions_role_id_roles_id FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_roles_permissions_permission_id_permissions_id FOREIGN KEY (permission_id) REFERENCES permissions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关系表';

DROP TABLE IF EXISTS oauth_client_details;
CREATE TABLE oauth_client_details (
    client_id VARCHAR(255) COMMENT '客户端id',
    client_name VARCHAR(50) COMMENT '客户端名称',
    resource_ids VARCHAR(255) COMMENT '客户端可访问的资源id',
    client_secret VARCHAR(255) COMMENT '客户端访问密钥',
    scope VARCHAR(255) COMMENT '客户端权限范围,如read,write',
    authorized_grant_types VARCHAR(255) COMMENT '授权方式,如authorization_code,refresh_token',
    web_server_redirect_uri VARCHAR(255) COMMENT '重定向地址或回调地址',
    authorities VARCHAR(255) COMMENT '客户端在授权服务器上的权限,如ROLE_ADMIN',
    access_token_validity INTEGER COMMENT '访问令牌有效期',
    refresh_token_validity INTEGER COMMENT '刷新令牌有效期',
    autoapprove VARCHAR(255) COMMENT '用于授权码方式,为true会跳过确认授权页面,直接授权',
    additional_information VARCHAR(4096)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='oauth客户端信息表';

INSERT INTO permissions(id, permission_name, display_name)
VALUES (1, 'USER_READ', '查询用户信息'),
       (2, 'USER_CREATE', '新建用户'),
       (3, 'USER_UPDATE', '编辑用户信息'),
       (4, 'USER_ADMIN', '用户管理');

INSERT INTO users(id, username, mobile, password_hash, enabled, account_non_expired, account_non_locked, credentials_non_expired, email)
VALUES (1, 'root', '00000000000', '{bcrypt}$2a$10$V7Pd6zlCA8EDefp3nTOvwO3IT7UgZ9UGjJ0Lb/ttUXb4SS948qroe', 1, 1, 1, 1, 'dorohedoro@163.com'),
       (2, 'jiaozi', '11111111111', '{bcrypt}$2a$10$V7Pd6zlCA8EDefp3nTOvwO3IT7UgZ9UGjJ0Lb/ttUXb4SS948qroe', 1, 1, 1, 1, 'jiaozi@163.com');
INSERT INTO roles(id, role_name, display_name, built_in)
VALUES (1, 'ROLE_USER', '客户端用户', true),
       (2, 'ROLE_ADMIN', '超级管理员', true),
       (3, 'ROLE_STAFF', '管理后台用户', true);
INSERT INTO users_roles(user_id, role_id) values (1, 1), (1, 2), (1, 3), (2, 1);
INSERT INTO roles_permissions(role_id, permission_id) values (1, 1), (2, 1), (2, 2), (2, 3), (2, 4);
INSERT INTO oauth_client_details (client_id, client_name, client_secret, scope, authorized_grant_types, web_server_redirect_uri, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES ('web-client', '第三方客户端', '{noop}secret', 'todo.read,todo.write', 'authorization_code,password,refresh_token,client_credentials', 'http://localhost:20000/login/oauth2/code/web-client-auth-code', '900', '31536000', '{}', null),
       ('admin-client', '后台管理前端(无服务器)', '{noop}secret', 'user.admin,client.admin', 'authorization_code,password,refresh_token,client_credentials', 'http://localhost:4001', '60', '31536000', '{}', true),
       ('ios-client', 'IOS客户端', '{noop}secret', 'todo.read,todo.write', 'authorization_code,password,refresh_token,client_credentials', 'com.example.app://action', '900', '31536000', '{}', null),
       ('android-client', 'Android客户端', '{noop}secret', 'todo.read,todo.write', 'authorization_code,password,refresh_token,client_credentials', 'com.example.app://action', '900', '31536000', '{}', null),
       ('todo-service', 'todo服务', '{noop}secret', 'todo.read,todo.write', 'authorization_code,refresh_token,client_credentials', 'http://localhost:30000/authorized', '900', '31536000', '{}', true );

# root的密码为dorohedoro1994
# 生成公钥私钥对:keytool -genkeypair -alias oauth-jwks -keyalg算法 RSA -keystore存储文件 oauth-jwks.keystore -keypass通行短语 dorohedoro -storepass dorohedoro
# 前端代码oauth-ui在百度云的代码包文件夹里
# 抓包查看oauth2授权码流程:无痕窗口访问http://localhost:4001(也是重定向地址),开发者工具勾选Preserve log,选中ALL