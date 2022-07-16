package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.FakeRestApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootTest(classes = FakeRestApplication.class)
public class UpdateControllerTest extends FakeModifyControllerTest<UpdateController> {

    @Override
    UpdateController initStaticController_NullRequest_InternalServerError(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, EMPTY_REQUEST_BODY, delayMs);
    }

    @Override
    UpdateController initStaticController_StaticAnswer(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, REQUEST_BODY, delayMs);
    }

    @Override
    UpdateController initStaticController_BodyAnswer(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    UpdateController initStaticController_EmptyRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }

    @Override
    UpdateController initStaticController_NullRequestBody_BadRequest(RequestMethod requestMethod, long delayMs) {
        return testControllersFabric.createStaticUpdateController(TEST_STATIC_URI, requestMethod, null, delayMs);
    }
}
