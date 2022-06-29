package io.github.ivanrosw.fakerest.core.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class GroovyAnswer {

    private HttpStatus httpStatus;

    private String answer;
}
