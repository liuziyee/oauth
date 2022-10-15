package com.dorohedoro.config;

public class Constants {
    public static final String PROBLEM_BASE_URI = "http://dorohedoro.com";
    public static final String PATTERN_MOBILE = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";

    public static final String ROLE_PREFIX = "ROLE_"; // 角色前缀
    public static final String SCOPE_PREFIX = "SCOPE_"; // 客户端权限前缀
    public static final String AUTHORITY_USER = "USER";
    public static final String AUTHORITY_STAFF = "STAFF";
    public static final String AUTHORITY_MANAGER = "MANAGER";
    public static final String AUTHORITY_ADMIN = "ADMIN";
    public static final String ROLE_USER = ROLE_PREFIX + AUTHORITY_USER;
    public static final String ROLE_STAFF = ROLE_PREFIX + AUTHORITY_STAFF;
    public static final String ROLE_MANAGER = ROLE_PREFIX + AUTHORITY_MANAGER;
    public static final String ROLE_ADMIN = ROLE_PREFIX + AUTHORITY_ADMIN;
    public static final String AUTHORITY_USER_ADMIN = "USER_ADMIN";
    public static final String AUTHORITY_USER_UPDATE = "USER_UPDATE";
    public static final String AUTHORITY_USER_CREATE = "USER_CREATE";
    public static final String AUTHORITY_USER_READ = "USER_READ";
    public static final String ROLE_HIERARCHY_INIT_EXPR = "ROLE_ADMIN > ROLE_STAFF\nROLE_STAFF > ROLE_USER\n"; // 用来初始化角色包含关系
    
    public static final Integer PAGE_OFFSET = 1;
    public static final Integer PAGE_SIZE = 10; // 每页记录数
}
