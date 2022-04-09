package io.github.ivanrosw.fakerest.core.controller;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface BaseController {

    ResponseEntity<String> handle(HttpServletRequest request);
}
