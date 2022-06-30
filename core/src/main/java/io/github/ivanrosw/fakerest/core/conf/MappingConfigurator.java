package io.github.ivanrosw.fakerest.core.conf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.controller.*;
import io.github.ivanrosw.fakerest.core.model.*;
import io.github.ivanrosw.fakerest.core.utils.IdGenerator;
import io.github.ivanrosw.fakerest.core.utils.HttpUtils;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.RestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Main configure class that can register new controllers and routers
 * And it has all information about configurations
 */
@Slf4j
@Component
public class MappingConfigurator {

    /**
     * Collection with active uris
     * Method - List of active uris
     */
    private Map<RequestMethod, List<String>> methodsUrls;
    /**
     * Collection with active controllers configs
     * Config id - controller config
     */
    private Map<String, UriConfigHolder<ControllerConfig>> controllers;
    /**
     * Collection with active routers configs
     * Config id - controller config
     */
    private Map<String, UriConfigHolder<RouterConfig>> routers;
    private IdGenerator controllersIdGenerator;
    private IdGenerator routersIdGenerator;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private ControllerData controllerData;
    @Autowired
    private JsonUtils jsonUtils;
    @Autowired
    private HttpUtils httpUtils;
    @Autowired
    private RestClient restClient;
    @Autowired
    private YamlConfigurator yamlConfigurator;

    @PostConstruct
    private void init() {
        methodsUrls = new EnumMap<>(RequestMethod.class);
        controllers = new HashMap<>();
        routers = new HashMap<>();
        controllersIdGenerator = new IdGenerator();
        routersIdGenerator = new IdGenerator();
    }

    //<-------------------------------- CONTROLLER ---------------------------------->

    /**
     * Method to init and run controller
     * Can be called from api
     *
     * @param conf - config with all necessary info to init controller
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    public void registerController(ControllerConfig conf) throws ConfigException {
        List<String> idParams = httpUtils.getIdParams(conf.getUri());
        ControllerSaveInfoMode mode = identifyMode(conf, idParams);
        beforeInitControllerCheck(conf, mode);

        if (mode == ControllerSaveInfoMode.COLLECTION) conf.setIdParams(idParams);

        UriConfigHolder<ControllerConfig> configHolder;
        switch (conf.getFunctionMode()) {
            case READ:
                configHolder = createReadControllerHolder(conf, mode);
                break;
            case CREATE:
                configHolder = createCreateControllerHolder(conf, mode);
                break;
            case UPDATE:
                configHolder = createUpdateControllerHolder(conf, mode);
                break;
            case DELETE:
                configHolder = createDeleteControllerHolder(conf, mode);
                break;
            case GROOVY:
                configHolder = createGroovyControllerHolder(conf);
                break;
            default:
                throw new ConfigException(String.format("Controller: Function mode [%s] not supported", conf.getFunctionMode()));
        }

        registerMapping(configHolder);
        conf.setId(controllersIdGenerator.generateId(GeneratorPattern.SEQUENCE));
        if (mode == ControllerSaveInfoMode.COLLECTION) loadCollectionAnswerData(conf);

        addUrls(configHolder);

        controllers.put(conf.getId(), configHolder);
        if (!yamlConfigurator.isControllerExist(conf) && !yamlConfigurator.addController(conf)) {
            unregisterController(conf.getId());
        } else {
            log.info("Registered controllers. Method: [{}],  Urls:{}", conf.getMethod(), configHolder.getUsedUrls());
        }
    }

    /**
     * Check configuration before init and run controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    private void beforeInitControllerCheck(ControllerConfig conf, ControllerSaveInfoMode mode) throws ConfigException {
        if (conf.getUri() == null || conf.getUri().isEmpty()) {
            throw new ConfigException("Controller: Uri must be not blank");
        }
        if (conf.getMethod() == null) {
            throw new ConfigException("Controller: Method must be specified");
        }
        if (conf.getFunctionMode() == null) {
            throw new ConfigException("Controller: function mode must be specified");
        }

        List<String> urls = methodsUrls.computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
        if (urls.contains(conf.getUri()) ||
                (conf.getFunctionMode() == ControllerFunctionMode.READ && mode == ControllerSaveInfoMode.COLLECTION && urls.contains(httpUtils.getBaseUri(conf.getUri())))) {
            throw new ConfigException(String.format("Controller: Duplicated urls: %s", conf.getUri()));
        }
    }

    /**
     * Identify save info mode base on identify idParams
     *
     * @param conf - config of controller
     * @param idParams - id params from url if it set
     * @return - groovy, static or collection mode
     */
    private ControllerSaveInfoMode identifyMode(ControllerConfig conf, List<String> idParams) {
        if (conf.getFunctionMode() == ControllerFunctionMode.GROOVY) {
            return ControllerSaveInfoMode.GROOVY;
        }
        return idParams.isEmpty() ? ControllerSaveInfoMode.STATIC : ControllerSaveInfoMode.COLLECTION;
    }

    /**
     * Create config holder for read controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createReadControllerHolder(ControllerConfig conf, ControllerSaveInfoMode mode) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();

        if (mode == ControllerSaveInfoMode.COLLECTION) {
            String baseUri = httpUtils.getBaseUri(conf.getUri());
            RequestMappingInfo getAllMappingInfo = RequestMappingInfo
                    .paths(baseUri)
                    .methods(conf.getMethod())
                    .build();

            FakeController readAllController = ReadController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ALL)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .build();
            requestMappingInfo.put(getAllMappingInfo, readAllController);
            usedUrls.add(baseUri);

            RequestMappingInfo readOneMappingInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController getOneController = ReadController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .build();
            requestMappingInfo.put(readOneMappingInfo, getOneController);
            usedUrls.add(conf.getUri());

        } else {
            RequestMappingInfo readStaticMappingInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController getStaticController =  ReadController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .build();
            requestMappingInfo.put(readStaticMappingInfo, getStaticController);
            usedUrls.add(conf.getUri());
        }
        return new UriConfigHolder<>(conf,requestMappingInfo, usedUrls);
    }

    /**
     * Create config holder for create controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createCreateControllerHolder(ControllerConfig conf, ControllerSaveInfoMode mode) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();
        IdGenerator idGenerator = new IdGenerator();

        if (mode == ControllerSaveInfoMode.COLLECTION) {
            String baseUri = httpUtils.getBaseUri(conf.getUri());
            RequestMappingInfo createOneInfo = RequestMappingInfo
                    .paths(baseUri)
                    .methods(conf.getMethod())
                    .build();

            FakeController createOneController = CreateController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .idGenerator(idGenerator)
                    .build();
            requestMappingInfo.put(createOneInfo, createOneController);
            usedUrls.add(baseUri);

        } else {
            RequestMappingInfo createStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController createStaticController = CreateController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .idGenerator(idGenerator)
                    .build();
            requestMappingInfo.put(createStaticInfo, createStaticController);
            usedUrls.add(conf.getUri());
        }
        return new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);
    }

    /**
     * Create config holder for update controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createUpdateControllerHolder(ControllerConfig conf, ControllerSaveInfoMode mode) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();

        if (mode == ControllerSaveInfoMode.COLLECTION) {
            RequestMappingInfo updateOneInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController updateOneController = UpdateController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .build();
            requestMappingInfo.put(updateOneInfo, updateOneController);
            usedUrls.add(conf.getUri());

        } else {
            RequestMappingInfo updateStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController updateStaticController = UpdateController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .build();
            requestMappingInfo.put(updateStaticInfo, updateStaticController);
            usedUrls.add(conf.getUri());
        }
        return new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);
    }

    /**
     * Create config holder for delete controller
     *
     * @param conf - config with all necessary info to init controller
     * @param mode - static or collection mode
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createDeleteControllerHolder(ControllerConfig conf, ControllerSaveInfoMode mode) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();

        if (mode == ControllerSaveInfoMode.COLLECTION) {
            RequestMappingInfo deleteOneInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController deleteOneController = DeleteController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.COLLECTION_ONE)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .build();
            requestMappingInfo.put(deleteOneInfo, deleteOneController);
            usedUrls.add(conf.getUri());

        } else {
            RequestMappingInfo deleteStaticInfo = RequestMappingInfo
                    .paths(conf.getUri())
                    .methods(conf.getMethod())
                    .build();

            FakeController deleteStaticController = DeleteController.builder()
                    .saveInfoMode(ControllerSaveInfoMode.STATIC)
                    .controllerData(controllerData)
                    .controllerConfig(conf)
                    .jsonUtils(jsonUtils)
                    .httpUtils(httpUtils)
                    .build();
            requestMappingInfo.put(deleteStaticInfo, deleteStaticController);
            usedUrls.add(conf.getUri());
        }
        return new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);
    }

    /**
     * Create config holder for groovy controller
     *
     * @param conf - config with all necessary info to init controller
     * @return - config holder that haven't ran yet
     */
    private UriConfigHolder<ControllerConfig> createGroovyControllerHolder(ControllerConfig conf) {
        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();

        RequestMappingInfo groovyInfo = RequestMappingInfo
                .paths(conf.getUri())
                .methods(conf.getMethod())
                .build();

        GroovyController groovyController = GroovyController.builder()
                .controllerData(controllerData)
                .controllerConfig(conf)
                .jsonUtils(jsonUtils)
                .httpUtils(httpUtils)
                .build();

        requestMappingInfo.put(groovyInfo, groovyController);
        usedUrls.add(conf.getUri());

        return new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);
    }

    /**
     * Load to collection of controller's data from config
     *
     * @param conf - config with all necessary info to init controller
     */
    private void loadCollectionAnswerData(ControllerConfig conf) {
        if (conf.getAnswer() != null && (conf.getAnswer().contains("{") || conf.getAnswer().contains("["))) {
            JsonNode answer = jsonUtils.toJsonNode(conf.getAnswer());

            if (answer instanceof ArrayNode) {
                ArrayNode array = (ArrayNode) answer;
                array.forEach(jsonNode -> addAnswerData(conf, (ObjectNode) jsonNode));
            } else if (answer instanceof ObjectNode) {
                addAnswerData(conf, (ObjectNode) answer);
            } else {
                log.warn("Cant put data [{}] to collection [{}]. Its not json", answer, conf.getUri());
            }
        }
    }

    /**
     * Add data to collection of controller's
     *
     * @param conf - config with all necessary info to init controller
     * @param data - json to add
     */
    private void addAnswerData(ControllerConfig conf, ObjectNode data) {
        String key = controllerData.buildKey(data, conf.getIdParams());
        controllerData.putData(conf.getUri(), key, data);
    }

    /**
     * Delete and stop controllers
     * Can be called from api
     *
     * @param id - id of configuration
     * @throws ConfigException - if configuration with id not exist
     */
    public void unregisterController(String id) throws ConfigException {
        if (!controllers.containsKey(id)) {
            throw new ConfigException(String.format("Controller with id [%s] not exist", id));
        }
        UriConfigHolder<ControllerConfig> configHolder = controllers.get(id);
        ControllerConfig conf = configHolder.getConfig();

        if (yamlConfigurator.isControllerExist(conf)) {
            yamlConfigurator.deleteController(conf);
        }

        unregisterMapping(configHolder);
        List<String> urls = methodsUrls.get(conf.getMethod());
        urls.removeAll(configHolder.getUsedUrls());
        controllers.remove(id);

        ControllerSaveInfoMode mode = identifyMode(conf, conf.getIdParams());
        if (mode == ControllerSaveInfoMode.COLLECTION) controllerData.deleteControllerData(conf.getUri());
        log.info("Unregistered controllers. Method: [{}], Urls: {}", configHolder.getConfig().getMethod(), configHolder.getUsedUrls());
    }

    //<--------------------------------- ROUTER ------------------------------------->


    /**
     * Method to init and run router controller
     * Can be called from api
     *
     * @param conf - config with all necessary info to init router
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    public void registerRouter(RouterConfig conf) throws ConfigException {
        beforeInitRouterCheck(conf);

        Map<RequestMappingInfo, BaseController> requestMappingInfo = new HashMap<>();
        List<String> usedUrls = new ArrayList<>();
        RequestMappingInfo routerInfo = RequestMappingInfo
                .paths(conf.getUri())
                .methods(conf.getMethod())
                .build();

        RouterController routerController = new RouterController(conf, httpUtils, restClient);
        requestMappingInfo.put(routerInfo, routerController);
        usedUrls.add(conf.getUri());
        UriConfigHolder<RouterConfig> configHolder = new UriConfigHolder<>(conf, requestMappingInfo, usedUrls);

        registerMapping(configHolder);
        addUrls(configHolder);

        conf.setId(routersIdGenerator.generateId(GeneratorPattern.SEQUENCE));
        routers.put(conf.getId(), configHolder);

        if (!yamlConfigurator.isRouterExist(conf) && !yamlConfigurator.addRouter(conf)) {
            unregisterRouter(conf.getId());
        } else {
            log.info("Registered router. Method: [{}],  Urls:{}", conf.getMethod(), configHolder.getUsedUrls());
        }
    }

    /**
     * Check configuration before init and run router
     *
     * @param conf - config with all necessary info to init controller
     * @throws ConfigException - if config don't contain all necessary info or url already registered
     */
    private void beforeInitRouterCheck(RouterConfig conf) throws ConfigException {
        if (conf.getUri() == null || conf.getToUrl() == null || conf.getUri().isEmpty() || conf.getToUrl().isEmpty()) {
            throw new ConfigException("Router: Uri and toUrl must be not blank");
        }
        if (conf.getMethod() == null) {
            throw new ConfigException("Router: Method must be specified");
        }

        List<String> urls = methodsUrls.computeIfAbsent(conf.getMethod(), key -> new ArrayList<>());
        if (urls.contains(conf.getUri())) {
            throw new ConfigException(String.format("Router: Duplicated urls: %s", conf.getUri()));
        }
    }

    /**
     * Delete and stop routers
     * Can be called from api
     *
     * @param id - id of configuration
     * @throws ConfigException - if configuration with id not exist
     */
    public void unregisterRouter(String id) throws ConfigException {
        if (!routers.containsKey(id)) {
            throw new ConfigException(String.format("Router with id [%s] not exist", id));
        }
        UriConfigHolder<RouterConfig> configHolder = routers.get(id);

        if (yamlConfigurator.isRouterExist(configHolder.getConfig())) {
            yamlConfigurator.deleteRouter(configHolder.getConfig());
        }

        unregisterMapping(configHolder);
        List<String> urls = methodsUrls.get(configHolder.getConfig().getMethod());
        urls.removeAll(configHolder.getUsedUrls());
        routers.remove(id);
        log.info("Unregistered router. Method: [{}], Urls: {}", configHolder.getConfig().getMethod(), configHolder.getUsedUrls());
    }

    //<------------------------------------ UTILS ----------------------------------->

    /**
     * Register and run mappings
     *
     * @param configHolder - config holder with initiated request mappings and controllers
     * @throws ConfigException - if can't register mapping
     */
    private void registerMapping(UriConfigHolder<?> configHolder) throws ConfigException {
        try {
            for (Map.Entry<RequestMappingInfo, BaseController> entry : configHolder.getRequestMappingInfo().entrySet()) {
                handlerMapping.registerMapping(entry.getKey(), entry.getValue(),
                        BaseController.class.getMethod("handle", HttpServletRequest.class));
            }
        } catch (Exception e) {
            unregisterMapping(configHolder);
            throw new ConfigException(String.format("Error while register mapping with url [%s] and method [%s]",
                    configHolder.getConfig().getUri(), configHolder.getConfig().getMethod()), e);
        }
    }

    /**
     * Unregister and stop mappings
     *
     * @param configHolder - config holder with initiated request mappings and controllers
     */
    private void unregisterMapping(UriConfigHolder<?> configHolder) {
        configHolder.getRequestMappingInfo().keySet().forEach(requestMappingInfo -> handlerMapping.unregisterMapping(requestMappingInfo));
        if (log.isDebugEnabled()) log.debug("Uri [{}] with method [{}] unregistered", configHolder.getConfig().getUri(), configHolder.getConfig().getMethod());
    }

    /**
     * Add used urls to general collection
     *
     * @param configHolder - config holder with initiated request mappings and controllers
     */
    private void addUrls(UriConfigHolder<?> configHolder) {
        List<String> urls = methodsUrls.computeIfAbsent(configHolder.getConfig().getMethod(), key -> new ArrayList<>());
        urls.addAll(configHolder.getUsedUrls());
    }

    /**
     * Log active methods ands uris
     */
    public void printUrls() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("**** Configured URLs ****\n");

        methodsUrls.forEach((method, urls) -> {
            builder.append(method);
            builder.append(":\n");
            urls.forEach(url -> {
                builder.append("    ");
                builder.append(url);
                builder.append("\n");
            });
        });

        log.info(builder.toString());
    }

    /**
     * Get copy of all controller's configs
     *
     * @return - copy of configs
     */
    public List<ControllerConfig> getAllControllersCopy() {
        List<ControllerConfig> copy = new ArrayList<>(controllers.size());
        controllers.values().forEach(conf -> copy.add(conf.getConfig().copy()));
        return copy;
    }

    /**
     * Get copy of controller's config by id
     *
     * @param id - id of config
     * @return - copy of config
     */
    public ControllerConfig getControllerCopy(String id) {
        return controllers.containsKey(id) ? controllers.get(id).getConfig().copy() : null;
    }

    /**
     * Get copy of all router's configs
     *
     * @return - copy of configs
     */
    public List<RouterConfig> getAllRoutersCopy() {
        List<RouterConfig> copy = new ArrayList<>(routers.size());
        routers.values().forEach(conf -> copy.add(conf.getConfig().copy()));
        return copy;
    }

    /**
     * Get copy of router's config by id
     *
     * @param id - id of config
     * @return - copy of config
     */
    public RouterConfig getRouterCopy(String id) {
        return routers.containsKey(id) ? routers.get(id).getConfig().copy() : null;
    }
}
