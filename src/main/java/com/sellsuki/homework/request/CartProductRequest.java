package com.sellsuki.homework.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CartProductRequest {

    // @NotBlank validate productId is not null, not blank, not empty
    @NotBlank(message = "product id is required")
    private String productId;

    // validate pattern must be number
    @NotNull(message = "amount is required")
    @Pattern(regexp = "^([1-9][0-9]{0,8})$", message = "amount is invalid")
    private String amount;
}
