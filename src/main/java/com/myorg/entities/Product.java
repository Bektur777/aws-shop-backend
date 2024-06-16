package com.myorg.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Product {

    private Long id;

    private String title;

    private String description;

    private Double price;
}
