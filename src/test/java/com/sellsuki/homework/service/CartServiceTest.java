package com.sellsuki.homework.service;

import com.sellsuki.homework.dto.CartProductResponseDTO;
import com.sellsuki.homework.exceptions.BadRequestException;
import com.sellsuki.homework.exceptions.InternalServerErrorException;
import com.sellsuki.homework.model.Book;
import com.sellsuki.homework.request.CartProductRequest;
import com.sellsuki.homework.request.CreateCartRequest;
import com.sellsuki.homework.response.CreateCartResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private BookService bookService;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(cartService, "promotionBookId", "1,2,3,4,5,6,7");
    }

    @Test
    public void shouldExceptionWhenCreateCartWithCannotGetBookList() throws Exception {
        expectedException.expect(InternalServerErrorException.class);

        CreateCartRequest request = new CreateCartRequest();

        Mockito.doThrow(InternalServerErrorException.class).when(bookService).getBookListFromAPI();

        cartService.createCart(request);
    }

    @Test
    public void shouldExceptionWhenCreateCartWithProductNotFound() throws Exception {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Product not found");

        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1234", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        cartService.createCart(request);
    }

    @Test
    public void shouldNotDiscountWhenCreateCartWithNonPromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("8", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("0.0", response.getDiscount());
        Assert.assertEquals("800.0", response.getNet());
        Assert.assertEquals(1, response.getProducts().size());
        Assert.assertEquals("8", response.getProducts().get(0).getProductId());
        Assert.assertEquals("Conan", response.getProducts().get(0).getTitle());
        Assert.assertEquals("1", response.getProducts().get(0).getAmount());
        Assert.assertEquals("800", response.getProducts().get(0).getPrice());
        Assert.assertEquals("800.0", response.getProducts().get(0).getTotalPrice());
    }

    @Test
    public void shouldNotDiscountWhenCreateCartWith1UniqueBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("0.0", response.getDiscount());
        Assert.assertEquals("100.0", response.getNet());
        Assert.assertEquals(1, response.getProducts().size());

        CartProductResponseDTO cartProductResponseDTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductResponseDTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductResponseDTO.getTitle());
        Assert.assertEquals("1", cartProductResponseDTO.getAmount());
        Assert.assertEquals("100", cartProductResponseDTO.getPrice());
        Assert.assertEquals("100.0", cartProductResponseDTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount10PercentOfTwoUniqueBookWhenCreateCartWith2UniquePromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("30.0", response.getDiscount());
        Assert.assertEquals("270.0", response.getNet());
        Assert.assertEquals(2, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount11PercentOfTwoUniqueBookWhenCreateCartWith3UniquePromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("66.0", response.getDiscount());
        Assert.assertEquals("534.0", response.getNet());
        Assert.assertEquals(3, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount12PercentOfTwoUniqueBookWhenCreateCartWith4UniquePromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("4", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("120.0", response.getDiscount());
        Assert.assertEquals("880.0", response.getNet());
        Assert.assertEquals(4, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry4DTO = response.getProducts().get(3);
        Assert.assertEquals("4", cartProductHarry4DTO.getProductId());
        Assert.assertEquals("Harry (IV)", cartProductHarry4DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry4DTO.getAmount());
        Assert.assertEquals("400", cartProductHarry4DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry4DTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount13PercentOfTwoUniqueBookWhenCreateCartWith5UniquePromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("4", "1"));
        cartProductRequests.add(mockCartProductRequest("5", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("195.0", response.getDiscount());
        Assert.assertEquals("1305.0", response.getNet());
        Assert.assertEquals(5, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry4DTO = response.getProducts().get(3);
        Assert.assertEquals("4", cartProductHarry4DTO.getProductId());
        Assert.assertEquals("Harry (IV)", cartProductHarry4DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry4DTO.getAmount());
        Assert.assertEquals("400", cartProductHarry4DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry4DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry5DTO = response.getProducts().get(4);
        Assert.assertEquals("5", cartProductHarry5DTO.getProductId());
        Assert.assertEquals("Harry (V)", cartProductHarry5DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry5DTO.getAmount());
        Assert.assertEquals("500", cartProductHarry5DTO.getPrice());
        Assert.assertEquals("500.0", cartProductHarry5DTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount14PercentOfTwoUniqueBookWhenCreateCartWith6UniquePromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("4", "1"));
        cartProductRequests.add(mockCartProductRequest("5", "1"));
        cartProductRequests.add(mockCartProductRequest("6", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("294.0", response.getDiscount());
        Assert.assertEquals("1806.0", response.getNet());
        Assert.assertEquals(6, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry4DTO = response.getProducts().get(3);
        Assert.assertEquals("4", cartProductHarry4DTO.getProductId());
        Assert.assertEquals("Harry (IV)", cartProductHarry4DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry4DTO.getAmount());
        Assert.assertEquals("400", cartProductHarry4DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry4DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry5DTO = response.getProducts().get(4);
        Assert.assertEquals("5", cartProductHarry5DTO.getProductId());
        Assert.assertEquals("Harry (V)", cartProductHarry5DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry5DTO.getAmount());
        Assert.assertEquals("500", cartProductHarry5DTO.getPrice());
        Assert.assertEquals("500.0", cartProductHarry5DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry6DTO = response.getProducts().get(5);
        Assert.assertEquals("6", cartProductHarry6DTO.getProductId());
        Assert.assertEquals("Harry (VI)", cartProductHarry6DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry6DTO.getAmount());
        Assert.assertEquals("600", cartProductHarry6DTO.getPrice());
        Assert.assertEquals("600.0", cartProductHarry6DTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount15PercentOfTwoUniqueBookWhenCreateCartWith7UniquePromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("4", "1"));
        cartProductRequests.add(mockCartProductRequest("5", "1"));
        cartProductRequests.add(mockCartProductRequest("6", "1"));
        cartProductRequests.add(mockCartProductRequest("7", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("420.0", response.getDiscount());
        Assert.assertEquals("2380.0", response.getNet());
        Assert.assertEquals(7, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry4DTO = response.getProducts().get(3);
        Assert.assertEquals("4", cartProductHarry4DTO.getProductId());
        Assert.assertEquals("Harry (IV)", cartProductHarry4DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry4DTO.getAmount());
        Assert.assertEquals("400", cartProductHarry4DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry4DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry5DTO = response.getProducts().get(4);
        Assert.assertEquals("5", cartProductHarry5DTO.getProductId());
        Assert.assertEquals("Harry (V)", cartProductHarry5DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry5DTO.getAmount());
        Assert.assertEquals("500", cartProductHarry5DTO.getPrice());
        Assert.assertEquals("500.0", cartProductHarry5DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry6DTO = response.getProducts().get(5);
        Assert.assertEquals("6", cartProductHarry6DTO.getProductId());
        Assert.assertEquals("Harry (VI)", cartProductHarry6DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry6DTO.getAmount());
        Assert.assertEquals("600", cartProductHarry6DTO.getPrice());
        Assert.assertEquals("600.0", cartProductHarry6DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry7DTO = response.getProducts().get(6);
        Assert.assertEquals("7", cartProductHarry7DTO.getProductId());
        Assert.assertEquals("Harry (VII)", cartProductHarry7DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry7DTO.getAmount());
        Assert.assertEquals("700", cartProductHarry7DTO.getPrice());
        Assert.assertEquals("700.0", cartProductHarry7DTO.getTotalPrice());
    }

    @Test
    public void shouldNotDiscountWhenCreateCartWith1UniqueBookAndNonPromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("8", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("0.0", response.getDiscount());
        Assert.assertEquals("900.0", response.getNet());
        Assert.assertEquals(2, response.getProducts().size());

        CartProductResponseDTO cartProductResponseDTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductResponseDTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductResponseDTO.getTitle());
        Assert.assertEquals("1", cartProductResponseDTO.getAmount());
        Assert.assertEquals("100", cartProductResponseDTO.getPrice());
        Assert.assertEquals("100.0", cartProductResponseDTO.getTotalPrice());

        CartProductResponseDTO cartProduct8ResponseDTO = response.getProducts().get(1);
        Assert.assertEquals("8", cartProduct8ResponseDTO.getProductId());
        Assert.assertEquals("Conan", cartProduct8ResponseDTO.getTitle());
        Assert.assertEquals("1", cartProduct8ResponseDTO.getAmount());
        Assert.assertEquals("800", cartProduct8ResponseDTO.getPrice());
        Assert.assertEquals("800.0", cartProduct8ResponseDTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount10PercentOfTwoUniqueBookWhenCreateCartWith2UniquePromotionBookAndNonPromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("8", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("30.0", response.getDiscount());
        Assert.assertEquals("1070.0", response.getNet());
        Assert.assertEquals(3, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProduct8ResponseDTO = response.getProducts().get(2);
        Assert.assertEquals("8", cartProduct8ResponseDTO.getProductId());
        Assert.assertEquals("Conan", cartProduct8ResponseDTO.getTitle());
        Assert.assertEquals("1", cartProduct8ResponseDTO.getAmount());
        Assert.assertEquals("800", cartProduct8ResponseDTO.getPrice());
        Assert.assertEquals("800.0", cartProduct8ResponseDTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount11PercentOfTwoUniqueBookWhenCreateCartWith3UniquePromotionBookAndNonPromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("8", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("66.0", response.getDiscount());
        Assert.assertEquals("1334.0", response.getNet());
        Assert.assertEquals(4, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProduct8ResponseDTO = response.getProducts().get(3);
        Assert.assertEquals("8", cartProduct8ResponseDTO.getProductId());
        Assert.assertEquals("Conan", cartProduct8ResponseDTO.getTitle());
        Assert.assertEquals("1", cartProduct8ResponseDTO.getAmount());
        Assert.assertEquals("800", cartProduct8ResponseDTO.getPrice());
        Assert.assertEquals("800.0", cartProduct8ResponseDTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount12PercentOfTwoUniqueBookWhenCreateCartWith4UniquePromotionBookAndNonPromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("4", "1"));
        cartProductRequests.add(mockCartProductRequest("8", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("120.0", response.getDiscount());
        Assert.assertEquals("1680.0", response.getNet());
        Assert.assertEquals(5, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry4DTO = response.getProducts().get(3);
        Assert.assertEquals("4", cartProductHarry4DTO.getProductId());
        Assert.assertEquals("Harry (IV)", cartProductHarry4DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry4DTO.getAmount());
        Assert.assertEquals("400", cartProductHarry4DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry4DTO.getTotalPrice());

        CartProductResponseDTO cartProduct8ResponseDTO = response.getProducts().get(4);
        Assert.assertEquals("8", cartProduct8ResponseDTO.getProductId());
        Assert.assertEquals("Conan", cartProduct8ResponseDTO.getTitle());
        Assert.assertEquals("1", cartProduct8ResponseDTO.getAmount());
        Assert.assertEquals("800", cartProduct8ResponseDTO.getPrice());
        Assert.assertEquals("800.0", cartProduct8ResponseDTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount13PercentOfTwoUniqueBookWhenCreateCartWith5UniquePromotionBookAndNonPromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("4", "1"));
        cartProductRequests.add(mockCartProductRequest("5", "1"));
        cartProductRequests.add(mockCartProductRequest("8", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("195.0", response.getDiscount());
        Assert.assertEquals("2105.0", response.getNet());
        Assert.assertEquals(6, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry4DTO = response.getProducts().get(3);
        Assert.assertEquals("4", cartProductHarry4DTO.getProductId());
        Assert.assertEquals("Harry (IV)", cartProductHarry4DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry4DTO.getAmount());
        Assert.assertEquals("400", cartProductHarry4DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry4DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry5DTO = response.getProducts().get(4);
        Assert.assertEquals("5", cartProductHarry5DTO.getProductId());
        Assert.assertEquals("Harry (V)", cartProductHarry5DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry5DTO.getAmount());
        Assert.assertEquals("500", cartProductHarry5DTO.getPrice());
        Assert.assertEquals("500.0", cartProductHarry5DTO.getTotalPrice());

        CartProductResponseDTO cartProduct8ResponseDTO = response.getProducts().get(5);
        Assert.assertEquals("8", cartProduct8ResponseDTO.getProductId());
        Assert.assertEquals("Conan", cartProduct8ResponseDTO.getTitle());
        Assert.assertEquals("1", cartProduct8ResponseDTO.getAmount());
        Assert.assertEquals("800", cartProduct8ResponseDTO.getPrice());
        Assert.assertEquals("800.0", cartProduct8ResponseDTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount14PercentOfTwoUniqueBookWhenCreateCartWith6UniquePromotionBookAndNonPromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("4", "1"));
        cartProductRequests.add(mockCartProductRequest("5", "1"));
        cartProductRequests.add(mockCartProductRequest("6", "1"));
        cartProductRequests.add(mockCartProductRequest("8", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("294.0", response.getDiscount());
        Assert.assertEquals("2606.0", response.getNet());
        Assert.assertEquals(7, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry4DTO = response.getProducts().get(3);
        Assert.assertEquals("4", cartProductHarry4DTO.getProductId());
        Assert.assertEquals("Harry (IV)", cartProductHarry4DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry4DTO.getAmount());
        Assert.assertEquals("400", cartProductHarry4DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry4DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry5DTO = response.getProducts().get(4);
        Assert.assertEquals("5", cartProductHarry5DTO.getProductId());
        Assert.assertEquals("Harry (V)", cartProductHarry5DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry5DTO.getAmount());
        Assert.assertEquals("500", cartProductHarry5DTO.getPrice());
        Assert.assertEquals("500.0", cartProductHarry5DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry6DTO = response.getProducts().get(5);
        Assert.assertEquals("6", cartProductHarry6DTO.getProductId());
        Assert.assertEquals("Harry (VI)", cartProductHarry6DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry6DTO.getAmount());
        Assert.assertEquals("600", cartProductHarry6DTO.getPrice());
        Assert.assertEquals("600.0", cartProductHarry6DTO.getTotalPrice());

        CartProductResponseDTO cartProduct8ResponseDTO = response.getProducts().get(6);
        Assert.assertEquals("8", cartProduct8ResponseDTO.getProductId());
        Assert.assertEquals("Conan", cartProduct8ResponseDTO.getTitle());
        Assert.assertEquals("1", cartProduct8ResponseDTO.getAmount());
        Assert.assertEquals("800", cartProduct8ResponseDTO.getPrice());
        Assert.assertEquals("800.0", cartProduct8ResponseDTO.getTotalPrice());
    }

    @Test
    public void shouldDiscount15PercentOfTwoUniqueBookWhenCreateCartWith7UniquePromotionBookAndNonPromotionBook() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "1"));
        cartProductRequests.add(mockCartProductRequest("2", "1"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));
        cartProductRequests.add(mockCartProductRequest("4", "1"));
        cartProductRequests.add(mockCartProductRequest("5", "1"));
        cartProductRequests.add(mockCartProductRequest("6", "1"));
        cartProductRequests.add(mockCartProductRequest("7", "1"));
        cartProductRequests.add(mockCartProductRequest("8", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("420.0", response.getDiscount());
        Assert.assertEquals("3180.0", response.getNet());
        Assert.assertEquals(8, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("100.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("200.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry4DTO = response.getProducts().get(3);
        Assert.assertEquals("4", cartProductHarry4DTO.getProductId());
        Assert.assertEquals("Harry (IV)", cartProductHarry4DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry4DTO.getAmount());
        Assert.assertEquals("400", cartProductHarry4DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry4DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry5DTO = response.getProducts().get(4);
        Assert.assertEquals("5", cartProductHarry5DTO.getProductId());
        Assert.assertEquals("Harry (V)", cartProductHarry5DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry5DTO.getAmount());
        Assert.assertEquals("500", cartProductHarry5DTO.getPrice());
        Assert.assertEquals("500.0", cartProductHarry5DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry6DTO = response.getProducts().get(5);
        Assert.assertEquals("6", cartProductHarry6DTO.getProductId());
        Assert.assertEquals("Harry (VI)", cartProductHarry6DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry6DTO.getAmount());
        Assert.assertEquals("600", cartProductHarry6DTO.getPrice());
        Assert.assertEquals("600.0", cartProductHarry6DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry7DTO = response.getProducts().get(6);
        Assert.assertEquals("7", cartProductHarry7DTO.getProductId());
        Assert.assertEquals("Harry (VII)", cartProductHarry7DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry7DTO.getAmount());
        Assert.assertEquals("700", cartProductHarry7DTO.getPrice());
        Assert.assertEquals("700.0", cartProductHarry7DTO.getTotalPrice());

        CartProductResponseDTO cartProduct8ResponseDTO = response.getProducts().get(7);
        Assert.assertEquals("8", cartProduct8ResponseDTO.getProductId());
        Assert.assertEquals("Conan", cartProduct8ResponseDTO.getTitle());
        Assert.assertEquals("1", cartProduct8ResponseDTO.getAmount());
        Assert.assertEquals("800", cartProduct8ResponseDTO.getPrice());
        Assert.assertEquals("800.0", cartProduct8ResponseDTO.getTotalPrice());
    }

    @Test
    public void shouldCalculateDiscountCorrectWhenCreateCartWith3UniquePromotionBookAndRemainAmount() throws Exception {
        List<CartProductRequest> cartProductRequests = new ArrayList<>();
        cartProductRequests.add(mockCartProductRequest("1", "4"));
        cartProductRequests.add(mockCartProductRequest("2", "2"));
        cartProductRequests.add(mockCartProductRequest("3", "1"));

        CreateCartRequest request = new CreateCartRequest();
        request.setProducts(cartProductRequests);

        Map<String, Book> bookMap = mockBookListFromAPI();

        Mockito.when(bookService.getBookListFromAPI()).thenReturn(bookMap);

        CreateCartResponse response = cartService.createCart(request);

        Assert.assertNotNull(response.getProducts());
        Assert.assertEquals("96.0", response.getDiscount());
        Assert.assertEquals("1004.0", response.getNet());
        Assert.assertEquals(3, response.getProducts().size());

        CartProductResponseDTO cartProductHarry1DTO = response.getProducts().get(0);
        Assert.assertEquals("1", cartProductHarry1DTO.getProductId());
        Assert.assertEquals("Harry (I)", cartProductHarry1DTO.getTitle());
        Assert.assertEquals("4", cartProductHarry1DTO.getAmount());
        Assert.assertEquals("100", cartProductHarry1DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry1DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry2DTO = response.getProducts().get(1);
        Assert.assertEquals("2", cartProductHarry2DTO.getProductId());
        Assert.assertEquals("Harry (II)", cartProductHarry2DTO.getTitle());
        Assert.assertEquals("2", cartProductHarry2DTO.getAmount());
        Assert.assertEquals("200", cartProductHarry2DTO.getPrice());
        Assert.assertEquals("400.0", cartProductHarry2DTO.getTotalPrice());

        CartProductResponseDTO cartProductHarry3DTO = response.getProducts().get(2);
        Assert.assertEquals("3", cartProductHarry3DTO.getProductId());
        Assert.assertEquals("Harry (III)", cartProductHarry3DTO.getTitle());
        Assert.assertEquals("1", cartProductHarry3DTO.getAmount());
        Assert.assertEquals("300", cartProductHarry3DTO.getPrice());
        Assert.assertEquals("300.0", cartProductHarry3DTO.getTotalPrice());
    }

    private CartProductRequest mockCartProductRequest(String id, String amount) {
        CartProductRequest cartProductRequest = new CartProductRequest();
        cartProductRequest.setProductId(id);
        cartProductRequest.setAmount(amount);

        return cartProductRequest;
    }

    private Map<String, Book> mockBookListFromAPI() {
        List<Book> books = new ArrayList<>();
        books.add(mockBook("1", "Harry (I)", "100"));
        books.add(mockBook("2", "Harry (II)", "200"));
        books.add(mockBook("3", "Harry (III)", "300"));
        books.add(mockBook("4", "Harry (IV)", "400"));
        books.add(mockBook("5", "Harry (V)", "500"));
        books.add(mockBook("6", "Harry (VI)", "600"));
        books.add(mockBook("7", "Harry (VII)", "700"));
        books.add(mockBook("8", "Conan", "800"));

        return books
                .stream()
                .collect(
                        Collectors.toMap(Book::getId, book -> book)
                );
    }

    private Book mockBook(String id, String title, String price) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setPrice(price);
        book.setCover("cover");

        return book;
    }
}
