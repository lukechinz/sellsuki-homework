package com.sellsuki.homework.controller;

import com.sellsuki.homework.request.CreateCartRequest;
import com.sellsuki.homework.response.CreateCartResponse;
import com.sellsuki.homework.response.Response;
import com.sellsuki.homework.service.CartService;
import com.sellsuki.homework.utils.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(Protocol.API) // declare route path of this controller
public class CartController {

    @Autowired
    private CartService cartService;

    /*
     * declare route path if you'd like to call this api you can call this url {domain}/api/cart
     *
     * @Valid for validate the object. in this case is CreateCartRequest
     */
    @PostMapping(Protocol.CART)
    public ResponseEntity<Response<CreateCartResponse>> createCart(@RequestBody @Valid CreateCartRequest request) throws Exception {
        Response<CreateCartResponse> response = new Response<>(cartService.createCart(request));

        return ResponseEntity.ok(response);
    }
}
