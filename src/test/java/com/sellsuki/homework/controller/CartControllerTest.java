package com.sellsuki.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sellsuki.homework.request.CartProductRequest;
import com.sellsuki.homework.request.CreateCartRequest;
import com.sellsuki.homework.response.CreateCartResponse;
import com.sellsuki.homework.service.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void shouldExceptionWhenCreateCartWithProductsIsNull() throws Exception {
        String url = "/api/cart";

        CreateCartRequest request = new CreateCartRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("have no product to select"));
    }

    @Test
    public void shouldExceptionWhenCreateCartWithProductsIsEmpty() throws Exception {
        String url = "/api/cart";

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(new ArrayList<>());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("have no product to select"));
    }

    @Test
    public void shouldExceptionWhenCreateCartWithProductIdIsNull() throws Exception {
        String url = "/api/cart";

        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setAmount("1");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(cartProductRequest);

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("product id is required"));
    }

    @Test
    public void shouldExceptionWhenCreateCartWithProductIdIsEmpty() throws Exception {
        String url = "/api/cart";

        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setProductId("");
        cartProductRequest.setAmount("1");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(cartProductRequest);

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("product id is required"));
    }

    @Test
    public void shouldExceptionWhenCreateCartWithAmountIsNull() throws Exception {
        String url = "/api/cart";

        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setProductId("123456789");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(cartProductRequest);

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("amount is required"));
    }

    @Test
    public void shouldExceptionWhenCreateCartWithAmountIsEmpty() throws Exception {
        String url = "/api/cart";

        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setProductId("123456789");
        cartProductRequest.setAmount("");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(cartProductRequest);

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("amount is invalid"));
    }

    @Test
    public void shouldExceptionWhenCreateCartWithAmountIsNotNumber() throws Exception {
        String url = "/api/cart";

        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setProductId("123456789");
        cartProductRequest.setAmount("hello");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(cartProductRequest);

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("amount is invalid"));
    }

    @Test
    public void shouldExceptionWhenCreateCartWithAmountIsInvalidLength() throws Exception {
        String url = "/api/cart";

        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setProductId("123456789");
        cartProductRequest.setAmount("1234567890987654321");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(cartProductRequest);

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("amount is invalid"));
    }

    @Test
    public void shouldExceptionWhenCreateCartWithAmountIsZero() throws Exception {
        String url = "/api/cart";

        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setProductId("123456789");
        cartProductRequest.setAmount("0");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(cartProductRequest);

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message").value("amount is invalid"));
    }

    @Test
    public void shouldSuccessWhenCreateCart() throws Exception {
        String url = "/api/cart";

        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setProductId("1234");
        cartProductRequest.setAmount("1");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(cartProductRequest);

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Mockito.when(cartService.createCart(Mockito.any())).thenReturn(new CreateCartResponse());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
