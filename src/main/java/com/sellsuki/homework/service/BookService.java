package com.sellsuki.homework.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sellsuki.homework.exceptions.InternalServerErrorException;
import com.sellsuki.homework.model.Book;
import com.sellsuki.homework.model.Product;
import com.sellsuki.homework.types.ErrorType;
import com.sellsuki.homework.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private RestTemplate restTemplate;

    // @Value get value from application.properties file with name is ${booklist.endpoint}
    @Value(Constants.BOOK_LIST_ENDPOINT)
    private String url;

    public Map<String, Book> getBookListFromAPI() throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("user-agent", "Mozilla/5.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Product product = mapper.readValue(response.getBody(), Product.class);

            return product.getBooks()
                    .stream()
                    .collect(
                            Collectors.toMap(Book::getId, book -> book)
                    );
        } catch (Exception ex) {
            throw new InternalServerErrorException(ErrorType.CANNOT_GET_BOOK_LIST);
        }
    }
}
