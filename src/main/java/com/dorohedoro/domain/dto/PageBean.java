package com.dorohedoro.domain.dto;

import lombok.Data;

@Data
public class PageBean<T> {

    private Integer page;
    
    private Integer size;
}
