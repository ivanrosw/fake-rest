package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.FakeRestApplication;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FakeRestApplication.class)
class DeleteControllerTest extends CrudControllerTest {

    void staticController_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        DeleteController deleteController = testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
        ResponseEntity<String> response = handleResponse(deleteController, null, delayMs);
        assertNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        staticController_NullRequest_InternalServerError(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        staticController_NullRequest_InternalServerError(requestMethod, delayMs);
    }

    void staticController_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        DeleteController deleteController = testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(deleteController, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        staticController_StaticAnswer(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        staticController_StaticAnswer(requestMethod, delayMs);
    }

    void staticController_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        DeleteController deleteController = testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
        HttpServletRequest request = createRequest(requestMethod, REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(deleteController, request, delayMs);
        assertEquals(REQUEST_BODY, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        staticController_BodyAnswer(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        staticController_BodyAnswer(requestMethod, delayMs);
    }

    void staticController_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        DeleteController deleteController = testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
        HttpServletRequest request = createRequest(requestMethod, EMPTY_REQUEST_BODY);
        ResponseEntity<String> response = handleResponse(deleteController, request, delayMs);
        assertEquals(createCudBadRequest().toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        staticController_EmptyRequestBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        staticController_EmptyRequestBody_BadRequest(requestMethod, delayMs);
    }

    void staticController_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        DeleteController deleteController = testControllersFabric.createStaticDeleteController(TEST_STATIC_URI, requestMethod, null, delayMs);
        HttpServletRequest request = createRequest(requestMethod, null);
        ResponseEntity<String> response = handleResponse(deleteController, request, delayMs);
        assertEquals(createCudBadRequest().toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void staticController_NoDelay_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        staticController_NullRequestBody_BadRequest(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void staticController_WithDelay_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        staticController_NullRequestBody_BadRequest(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_NotFound(RequestMethod requestMethod, long delayMs) {
        fillData();
        DeleteController deleteController = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequestVariables(requestMethod, null, Collections.singletonMap(FIRST_ID_PARAM, BAD_ID_VALUE));
        ResponseEntity<String> response = handleResponse(deleteController, request, delayMs);
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_NotFound(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NotFound(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_NotFound(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_NotFound(requestMethod, delayMs);
    }

    void collectionOneControllerOneId_Found(RequestMethod requestMethod, long delayMs) {
        fillData();
        DeleteController deleteController = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_ONE_ID, requestMethod, delayMs);
        HttpServletRequest request = createRequestVariables(requestMethod, null, Collections.singletonMap(FIRST_ID_PARAM, FIRST_ID_VALUE));
        ResponseEntity<String> response = handleResponse(deleteController, request, delayMs);
        assertEquals(JSON_ONE_ID_FIRST.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String key = controllerData.buildKey(JSON_ONE_ID_FIRST_BAD, Collections.singletonList(FIRST_ID_PARAM));
        assertFalse(controllerData.containsKey(TEST_COLLECTION_URI_ONE_ID, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerOneId_NoDelay_Found(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_Found(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerOneId_WithDelay_Found(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerOneId_Found(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_NotFound(RequestMethod requestMethod, long delayMs) {
        fillData();
        DeleteController deleteController = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, BAD_ID_VALUE);
        variables.put(SECOND_ID_PARAM, BAD_ID_VALUE);
        HttpServletRequest request = createRequestVariables(requestMethod, null, variables);
        ResponseEntity<String> response = handleResponse(deleteController, request, delayMs);
        String key = controllerData.buildKey(JSON_TWO_ID_BAD, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertEquals(createKeyNotFoundError(key).toString(), response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_NotFound(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NotFound(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_NotFound(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_NotFound(requestMethod, delayMs);
    }

    void collectionOneControllerTwoId_Found(RequestMethod requestMethod, long delayMs) {
        fillData();
        DeleteController deleteController = testControllersFabric.createCollectionOneDeleteController(TEST_COLLECTION_URI_TWO_IDS, requestMethod, delayMs);
        Map<String, String> variables = new HashMap<>();
        variables.put(FIRST_ID_PARAM, FIRST_ID_VALUE);
        variables.put(SECOND_ID_PARAM, SECOND_ID_VALUE);
        HttpServletRequest request = createRequestVariables(requestMethod, null, variables);
        ResponseEntity<String> response = handleResponse(deleteController, request, delayMs);
        assertEquals(JSON_TWO_ID.toString(), response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String key = controllerData.buildKey(JSON_TWO_ID, Arrays.asList(FIRST_ID_PARAM, SECOND_ID_PARAM));
        assertFalse(controllerData.containsKey(TEST_COLLECTION_URI_TWO_IDS, key));
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsNoDelay")
    void collectionOneControllerTwoId_NoDelay_Found(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_Found(requestMethod, delayMs);
    }

    @ParameterizedTest
    @MethodSource("provideAllMethodsWithDelay")
    void collectionOneControllerTwoId_WithDelay_Found(RequestMethod requestMethod, long delayMs) {
        collectionOneControllerTwoId_Found(requestMethod, delayMs);
    }
}
