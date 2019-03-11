package com.sellsuki.homework.response;

import com.sellsuki.homework.dto.CartProductResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateCartResponse {

    private List<CartProductResponseDTO> products;

    private String discount;

    private String net;
}
