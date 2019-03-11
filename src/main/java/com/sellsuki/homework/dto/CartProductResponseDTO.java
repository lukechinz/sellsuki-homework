package com.sellsuki.homework.dto;

import lombok.Data;

@Data
public class CartProductResponseDTO {

    private String productId;

    private String title;

    private String amount;

    private String price;

    private String totalPrice;
}
