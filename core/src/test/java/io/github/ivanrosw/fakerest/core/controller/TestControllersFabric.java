package io.github.ivanrosw.fakerest.core.controller;

import io.github.ivanrosw.fakerest.core.model.*;
import io.github.ivanrosw.fakerest.core.utils.HttpUtils;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Component
public class TestControllersFabric {

    @Autowired
    private ControllerData controllerData;
    @Autowired
    private JsonUtils jsonUtils;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private SystemUtils systemUtils;

    public ReadController createStaticReadController(String uri, RequestMethod method, String answer, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, answer, delayMs,
                false, null);
        return ReadController.builder()
                .saveInfoMode(ControllerSaveInfoMode.STATIC)
                .controllerData(controllerData)
                .controllerConfig(config)
                .jsonUtils(jsonUtils)
                .httpUtils(httpUtils).systemUtils(systemUtils).build();
    }

    public ReadController createCollectionAllReadController(String uri, RequestMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, null);
        return ReadController.builder()
                .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ALL)
                .controllerData(controllerData)
                .controllerConfig(config)
                .jsonUtils(jsonUtils)
                .httpUtils(httpUtils)
                .systemUtils(systemUtils).build();
    }

    public ReadController createCollectionOneReadController(String uri, RequestMethod method, long delayMs) {
        ControllerConfig config = createControllerConfig(uri, method, ControllerFunctionMode.READ, null, delayMs,
                false, null);
        return ReadController.builder()
                .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                .controllerData(controllerData)
                .controllerConfig(config)
                .jsonUtils(jsonUtils)
                .httpUtils(httpUtils)
                .systemUtils(systemUtils).build();
    }

    private ControllerConfig createControllerConfig(String uri, RequestMethod method, ControllerFunctionMode functionMode,
                                                    String answer, long delayMs, boolean generateId, String groovyScript) {
        ControllerConfig config = new ControllerConfig();
        config.setUri(uri);
        config.setMethod(method);
        config.setFunctionMode(functionMode);
        config.setAnswer(answer);
        config.setDelayMs(delayMs);
        config.setGenerateId(generateId);
        config.setGroovyScript(groovyScript);
        List<String> idParams = httpUtils.getIdParams(uri);
        config.setIdParams(idParams);
        idParams.forEach(id -> config.getGenerateIdPatterns().put(id, GeneratorPattern.SEQUENCE));
        return config;
    }

}
