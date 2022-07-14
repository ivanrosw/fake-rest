package io.github.ivanrosw.fakerest.core.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.ivanrosw.fakerest.core.FakeRestApplication;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
@SpringBootTest(classes = FakeRestApplication.class)
class ReadControllerTest extends CrudControllerTest {

    void staticController_EmptyAnswer(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        ReadController readController = testControllersFabric.createStaticReadController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(readController, request, delayMs);
        assertEquals(EMPTY_REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_EmptyAnswer(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        staticController_EmptyAnswer(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_EmptyAnswer(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        staticController_EmptyAnswer(requestMethod, delayMs);
    }

    void staticController_NotEmptyAnswer(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        ReadController readController = testControllersFabric.createStaticReadController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
        HttpServletRequest request = createRequest(requestMethod, REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(readController, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NotEmptyAnswer(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        staticController_NotEmptyAnswer(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NotEmptyAnswer(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        staticController_NotEmptyAnswer(requestMethod, delayMs);
    }

    void collectionAllControllerOneId_EmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        clearData();
        ReadController readController = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(readController, request, delayMs);
        assertEquals(jsonUtils.createArray().toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerOneId_NoDelay_EmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionAllControllerOneId_EmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerOneId_WithDelay_EmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionAllControllerOneId_EmptyArray(requestMethod, delayMs);
    }

    void collectionAllControllerOneId_NotEmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        fillData();
        ReadController readController = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(readController, request, delayMs);
        ArrayNode expectedArray = jsonUtils.createArray();
        expectedArray.add(JSON_ONE_ID_FIRST);
        expectedArray.add(JSON_ONE_ID_SECOND);
        assertEquals(expectedArray.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerOneId_NoDelay_NotEmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionAllControllerOneId_NotEmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerOneId_WithDelay_NotEmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionAllControllerOneId_NotEmptyArray(requestMethod, delayMs);
    }

    void collectionAllControllerTwoId_NotEmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        fillData();
        ReadController readController = testControllersFabric.createCollectionAllReadController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(readController, request, delayMs);
        ArrayNode expectedArray = jsonUtils.createArray();
        expectedArray.add(JSON_TWO_ID);
        assertEquals(expectedArray.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionAllControllerTwoId_NoDelay_NotEmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionAllControllerTwoId_NotEmptyArray(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionAllControllerTwoId_WithDelay_NotEmptyArray(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionAllControllerTwoId_NotEmptyArray(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NotFound(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        fillData();
        ReadController readController = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequestVariables(requestMethod, EMPTY_REQUEST_BODY, Collections.singletonMap(FIRST_ID_PARAM, BAD_ID_VALUE));
        ResponseEntity<String> response = handleResponse(readController, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NotFound(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionOneControllerOneId_NotFound(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NotFound(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionOneControllerOneId_NotFound(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_Found(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        fillData();
        ReadController readController = testControllersFabric.createCollectionOneReadController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequestVariables(requestMethod, EMPTY_REQUEST_BODY, Collections.singletonMap(FIRST_ID_PARAM, FIRST_ID_VALUE_ONE));
        ResponseEntity<String> response = handleResponse(readController, request, delayMs);
        assertEquals(JSON_ONE_ID_FIRST.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_Found(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionOneControllerOneId_Found(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_Found(RequestMethod requestMethod, long delayMs) throws InterruptedException {
        collectionOneControllerOneId_Found(requestMethod, delayMs);
    }

}
