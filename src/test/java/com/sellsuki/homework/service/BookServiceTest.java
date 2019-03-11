package com.sellsuki.homework.service;

import com.sellsuki.homework.exceptions.InternalServerErrorException;
import com.sellsuki.homework.model.Book;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private RestTemplate restTemplate;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(bookService, "url", "http://localhost:8080");
    }

    @Test
    public void shouldExceptionWhenGetBookListFromAPI() throws Exception {
        expectedException.expect(InternalServerErrorException.class);
        expectedException.expectMessage("Cannot get book list from api");

        Mockito.doThrow(Exception.class).when(restTemplate).exchange(
                Mockito.anyString(),
                Matchers.eq(HttpMethod.GET),
                Matchers.<HttpEntity<?>>any(),
                Matchers.eq(String.class)
        );

        bookService.getBookListFromAPI();
    }

    @Test
    public void shouldGetBookListSuccess() throws Exception {
        String responseBody = "{\"books\":[{\"cover\":\"cover\",\"price\":\"350\",\"title\":\"Harry Potter\",\"id\":\"1234\"}]}";
        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Matchers.eq(HttpMethod.GET),
                Matchers.<HttpEntity<?>>any(),
                Matchers.eq(String.class)
        )).thenReturn(new ResponseEntity<String>(responseBody, HttpStatus.OK));

        Map<String, Book> bookMap = bookService.getBookListFromAPI();

        Book book = bookMap.get("1234");
        Assert.assertEquals(1, bookMap.size());
        Assert.assertNotNull(book);
        Assert.assertEquals("1234", book.getId());
        Assert.assertEquals("350", book.getPrice());
        Assert.assertEquals("Harry Potter", book.getTitle());
        Assert.assertEquals("cover", book.getCover());
    }
}
