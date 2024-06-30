package com.myorg.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private String id;

    private String title;

    private String description;

    private Double price;

    private Integer count;
}
