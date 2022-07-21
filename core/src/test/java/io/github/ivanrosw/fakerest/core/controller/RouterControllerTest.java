package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.FakeRestApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FakeRestApplication.class)
class RouterControllerTest extends FakeControllerTest {

    private static final String TEST_URL = "http://localhost";
    private static final String TEST_URI_WITH_BRACKET = "/test";
    private static final String TEST_URI_WITHOUT_BRACKET = "test";

    static Stream<String> provideDifferentUri() {
        return Stream.of(TEST_URI_WITH_BRACKET, TEST_URI_WITHOUT_BRACKET);
    }

    @ParameterizedTest
    @MethodSource("provideDifferentUri")
    void buildUri_SendUri_ReturnUrl(String uri) throws URISyntaxException {
        RouterController subj = testControllersFabric.createRouterController(TEST_COLLECTION_URI_ONE_ID, uri, RequestMethod.GET);
        HttpServletRequest request = createRequest(RequestMethod.GET, TEST_COLLECTION_URI_ONE_ID);
        URI response = invokeBuildUri(subj, request);
        String expectedUrl = "http://localhost:" + request.getServerPort();
        if (uri.startsWith("/")) {
            expectedUrl += uri;
        } else {
            expectedUrl += "/" + uri;
        }
        assertEquals(new URI(expectedUrl), response);
    }

    @Test
    void buildUri_SendUrl_ReturnUrl() throws URISyntaxException {
        RouterController subj = testControllersFabric.createRouterController(TEST_COLLECTION_URI_ONE_ID, TEST_URL, RequestMethod.GET);
        HttpServletRequest request = createRequest(RequestMethod.GET, TEST_COLLECTION_URI_ONE_ID);
        URI response = invokeBuildUri(subj, request);
        assertEquals(new URI(TEST_URL), response);
    }

    @SneakyThrows
    private URI invokeBuildUri(RouterController subj, HttpServletRequest request) {
        Method buildUri = RouterController.class.getDeclaredMethod("buildUri", HttpServletRequest.class);
        buildUri.setAccessible(true);
        URI result = (URI) buildUri.invoke(subj, request);
        buildUri.setAccessible(false);
        return result;
    }



}
