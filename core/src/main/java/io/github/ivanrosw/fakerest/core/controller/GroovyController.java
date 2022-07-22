package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.ControllerData;
import io.github.ivanrosw.fakerest.core.model.GroovyAnswer;
import io.github.ivanrosw.fakerest.core.utils.HttpUtils;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class GroovyController extends FakeController {

    private static final String DEFAULT_GROOVY_IMPORT = "import io.github.ivanrosw.fakerest.core.model.GroovyAnswer \n" +
                                                        "import org.springframework.http.HttpStatus \n" +
                                                        "import io.github.ivanrosw.fakerest.core.utils.JsonUtils \n" +
                                                        "import io.github.ivanrosw.fakerest.core.model.ControllerData \n";

    private final GroovyShell groovyShell;

    @Builder
    public GroovyController(ControllerData controllerData, ControllerConfig controllerConfig, JsonUtils jsonUtils, HttpUtils httpUtils) {
        Binding groovyBinding = new Binding();
        groovyShell = new GroovyShell(groovyBinding);
        groovyShell.setVariable("uri", controllerConfig.getUri());
        groovyShell.setVariable("controllerData", controllerData);
        groovyShell.setVariable("jsonUtils", jsonUtils);
        this.controllerConfig = controllerConfig;
        this.jsonUtils = jsonUtils;
        this.httpUtils = httpUtils;
    }

    @Override
    public ResponseEntity<String> handle(HttpServletRequest request) {
        delay();

        try {
            String body = httpUtils.readBody(request);
            groovyShell.setVariable("body", body);

            GroovyAnswer groovyAnswer = (GroovyAnswer) groovyShell.evaluate(DEFAULT_GROOVY_IMPORT +
                    controllerConfig.getGroovyScript());

            return processGroovyAnswer(groovyAnswer);
        } catch (Exception e) {
            log.error("Controller: something went wrong", e);
            ObjectNode answer = jsonUtils.createJson();
            jsonUtils.putString(answer, DESCRIPTION_PARAM, e.getMessage());
            return new ResponseEntity<>(answer.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> processGroovyAnswer(GroovyAnswer groovyAnswer) {
        if (groovyAnswer.getHttpStatus() == null) {
            groovyAnswer.setHttpStatus(HttpStatus.OK);
        }
        return new ResponseEntity<>(groovyAnswer.getAnswer(), groovyAnswer.getHttpStatus());
    }


}
