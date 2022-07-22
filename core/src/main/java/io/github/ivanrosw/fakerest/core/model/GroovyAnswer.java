package io.github.ivanrosw.fakerest.core.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Answer that returned groovy script in {@link io.github.ivanrosw.fakerest.core.controller.GroovyController}
 */
@Getter
@Setter
public class GroovyAnswer {

    private HttpStatus httpStatus;

    private String answer;
}
