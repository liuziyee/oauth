package com.dorohedoro.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageBean<T> {

    // 页码
    private Long page;
    // 每页记录数
    private Long size;
    
    private Long offset;

    private String sort;

    private List<T> content;

    private Long total;
}
