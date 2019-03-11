package com.sellsuki.homework.service;

import com.sellsuki.homework.dto.CartProductRequestDTO;
import com.sellsuki.homework.dto.CartProductResponseDTO;
import com.sellsuki.homework.exceptions.BadRequestException;
import com.sellsuki.homework.model.Book;
import com.sellsuki.homework.request.CreateCartRequest;
import com.sellsuki.homework.request.CartProductRequest;
import com.sellsuki.homework.response.CreateCartResponse;
import com.sellsuki.homework.types.ErrorType;
import com.sellsuki.homework.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    // @Value get value from application.properties file with name is ${promotion.bookid}
    @Value(Constants.PROMOTION_BOOK_ID)
    private String promotionBookId;

    @Autowired
    private BookService bookService;

    public CreateCartResponse createCart(CreateCartRequest request) throws Exception {
        List<String> promotionProductIds = Arrays.asList(promotionBookId.split(","));

        Map<String, Book> bookHash = bookService.getBookListFromAPI();

        List<CartProductRequestDTO> cartProductDTOs = transformCartProduct(request.getProducts(), bookHash);

        List<CartProductRequestDTO> promotionProductDTOs = new ArrayList<>();
        List<CartProductRequestDTO> nonPromotionProductDTOs = new ArrayList<>();

        // separate to 2 type for calculate price with promotion and non-promotion
        cartProductDTOs.forEach(cartProductDTO -> {
            if (promotionProductIds.contains(cartProductDTO.getProductId())) {
                promotionProductDTOs.add(cartProductDTO);
            } else {
                nonPromotionProductDTOs.add(cartProductDTO);
            }
        });

        // get round to calculate promotion book
        Integer maxRound = getMaxAmount(promotionProductDTOs);

        Double totalDiscountPrice = 0D;
        // calculate total price of non-promotion book type
        Double totalPrice = nonPromotionProductDTOs
                .stream()
                .mapToDouble(productDTO -> productDTO.getAmount() * productDTO.getPrice())
                .sum();
        for (int round = 0; round < maxRound; round++) {
            Double sum = 0D;
            Integer uniqueProductAmount = 0;

            for (CartProductRequestDTO productDTO : promotionProductDTOs) {
                if (productDTO.getAmount() > 0) {
                    sum += productDTO.getPrice();
                    productDTO.setAmount(productDTO.getAmount() - 1);
                    uniqueProductAmount++;
                }
            }

            Integer discountPercent = getPercentDiscount(uniqueProductAmount);

            totalPrice += sum;
            totalDiscountPrice += (sum * discountPercent / 100);
        }

        return generateCreateCartResponse(request.getProducts(), bookHash, totalPrice, totalDiscountPrice);
    }

    private Integer getMaxAmount(List<CartProductRequestDTO> promotionProductDTOs) {
        Integer max = promotionProductDTOs
                .stream()
                .max(Comparator.comparing(CartProductRequestDTO::getAmount))
                .orElse(new CartProductRequestDTO())
                .getAmount();

        if (max == null) {
            max = 0;
        }

        return max;
    }

    private Integer getPercentDiscount(Integer uniqueProductAmount) {
        Integer percentDiscount = 0;
        switch (uniqueProductAmount) {
            case 2:
                percentDiscount = 10;
                break;
            case 3:
                percentDiscount = 11;
                break;
            case 4:
                percentDiscount = 12;
                break;
            case 5:
                percentDiscount = 13;
                break;
            case 6:
                percentDiscount = 14;
                break;
            case 7:
                percentDiscount = 15;
                break;
        }

        return percentDiscount;
    }

    private List<CartProductRequestDTO> transformCartProduct(List<CartProductRequest> cartProductRequests,
                                                             Map<String, Book> bookHash) throws Exception {

        List<CartProductRequestDTO> cartProductRequestDTOs = new ArrayList<>();
        for (CartProductRequest cartProductRequest : cartProductRequests) {
            if (!bookHash.containsKey(cartProductRequest.getProductId())) {
                throw new BadRequestException(ErrorType.PRODUCT_NOT_FOUND);
            }

            CartProductRequestDTO cartProductDTO = new CartProductRequestDTO();
            cartProductDTO.setProductId(cartProductRequest.getProductId());
            cartProductDTO.setAmount(Integer.valueOf(cartProductRequest.getAmount()));
            cartProductDTO.setPrice(
                    Double.valueOf(bookHash.get(cartProductDTO.getProductId()).getPrice())
            );

            cartProductRequestDTOs.add(cartProductDTO);
        }

        return cartProductRequestDTOs;
    }

    private CreateCartResponse generateCreateCartResponse(List<CartProductRequest> cartProductRequests,
                                                          Map<String, Book> bookHash,
                                                          Double totalPrice,
                                                          Double totalDiscountPrice) {
        CreateCartResponse response = new CreateCartResponse();
        response.setDiscount(totalDiscountPrice.toString());
        Double net = totalPrice - totalDiscountPrice;
        response.setNet(net.toString());
        response.setProducts(
                cartProductRequests
                        .stream()
                        .map(product -> {
                            CartProductResponseDTO cartProductDTO = new CartProductResponseDTO();
                            cartProductDTO.setProductId(product.getProductId());
                            cartProductDTO.setTitle(bookHash.get(cartProductDTO.getProductId()).getTitle());
                            cartProductDTO.setAmount(product.getAmount());
                            cartProductDTO.setPrice(bookHash.get(cartProductDTO.getProductId()).getPrice());
                            Double sum = Integer.valueOf(cartProductDTO.getAmount()) * Double.valueOf(cartProductDTO.getPrice());
                            cartProductDTO.setTotalPrice(sum.toString());

                            return cartProductDTO;
                        }).collect(Collectors.toList())
        );

        return response;
    }
}
