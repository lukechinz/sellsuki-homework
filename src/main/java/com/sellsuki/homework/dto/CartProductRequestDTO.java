package com.sellsuki.homework.dto;

import lombok.Data;

@Data
public class CartProductRequestDTO {

    private String productId;

    private Integer amount;

    private Double price;
}
