package com.sellsuki.homework.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private T data;

    private List<ErrorResponse> errors;

    public Response(T data) {
        this.data = data;
    }

    public Response(List<ErrorResponse> errors) {
        this.errors = errors;
    }
}
