package com.sellsuki.homework.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

@Data // @Data is lombok plugins to generate getter and setter method
public class CreateCartRequest {

    @Valid // @Valid for validate the object. in this case is CartProductRequest
    @NotEmpty(message = "have no product to select")
    private List<CartProductRequest> products;
}
