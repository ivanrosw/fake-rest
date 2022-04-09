package io.github.ivanrosw.fakerest.core;

import io.github.ivanrosw.fakerest.api.FakeRestApiApplication;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.model.RouterConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.config.location = classpath:configuration-tests.yml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = FakeRestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MappingConfiguratorControllerTest {

	private static final String BASE_CONTROLLER_URL = "/api/conf/mapping/controller";
	private static final String BASE_ONE_CONTROLLER_URL = "/api/conf/mapping/controller/";
	private static final String BASE_ROUTER_URL = "/api/conf/mapping/router";
	private static final String BASE_ONE_ROUTER_URL = "/api/conf/mapping/router/";

	private static final String NEW_URL_PATH = "/newUrl";
	private static final String EXIST_URL_PATH = "/test/";
	private static final String EXIST_ID = "1";
	private static final String NOT_EXIST_ID = "999";

	@LocalServerPort
	private int port;
	private String baseUrl;

	@Autowired
	private RestClient restClient;
	@Autowired
	private JsonUtils jsonUtils;

	@BeforeEach
	void init() {
		baseUrl = "http://localhost:" + port;
	}

	@AfterEach
	void clearConfig() throws Exception{
		File file = new File(getClass().getClassLoader().getResource("configuration-tests.yml").getFile());
		try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
			fileWriter.write("---\n" +
					"rest:\n" +
					"  controllers:\n" +
					"    - uri: '/test/'\n" +
					"      method: GET\n" +
					"  routers:\n" +
					"    - uri: '/test'\n" +
					"      toUrl: '/test/'\n" +
					"      method: GET");
		}
	}

	@Test
	void testGetAllControllers() throws Exception {
		URI uri = new URI(baseUrl + BASE_CONTROLLER_URL);
		ControllerConfig[] controllers = jsonUtils.toObject(restClient.execute(HttpMethod.GET, uri, null, null).getBody(),
				ControllerConfig[].class);
		assertThat(controllers).hasSize(1);
		assertThat(controllers[0].getUri()).isEqualTo(EXIST_URL_PATH);
	}

	@Test
	void testGetOneController() throws Exception {
		URI uri = new URI(baseUrl + BASE_ONE_CONTROLLER_URL + EXIST_ID);
		ControllerConfig controller = jsonUtils.toObject(restClient.execute(HttpMethod.GET, uri, null, null).getBody(),
				ControllerConfig.class);
		assertThat(controller.getUri()).isEqualTo(EXIST_URL_PATH);
	}

	@Test
	void testGetOneControllerNotExist() throws Exception {
		URI uri = new URI(baseUrl + BASE_ONE_CONTROLLER_URL + NOT_EXIST_ID);
		assertThat(restClient.execute(HttpMethod.GET, uri, null, null).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testCreateController() throws Exception {
		ControllerConfig config = new ControllerConfig();
		config.setMethod(RequestMethod.GET);
		config.setUri(NEW_URL_PATH);

		URI uri = new URI(baseUrl + BASE_CONTROLLER_URL);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restClient.execute(HttpMethod.POST, uri, headers, jsonUtils.toObjectNode(config).toString());

		ControllerConfig[] controllers = jsonUtils.toObject(restClient.execute(HttpMethod.GET, uri, null, null).getBody(),
				ControllerConfig[].class);
		assertThat(controllers).hasSize(2);
		assertThat(controllers[1].getUri()).isEqualTo(NEW_URL_PATH);
	}

	@Test
	void testCreateControllerExist() throws Exception {
		ControllerConfig config = new ControllerConfig();
		config.setMethod(RequestMethod.GET);
		config.setUri(EXIST_URL_PATH);

		URI uri = new URI(baseUrl + BASE_CONTROLLER_URL);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		assertThat(restClient.execute(HttpMethod.POST, uri, headers, jsonUtils.toObjectNode(config).toString()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testCreateControllerEmpty() throws Exception {
		URI uri = new URI(baseUrl + BASE_CONTROLLER_URL);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		assertThat(restClient.execute(HttpMethod.POST, uri, headers, "").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testDeleteController() throws Exception {
		URI uri = new URI(baseUrl + BASE_ONE_CONTROLLER_URL + EXIST_ID);
		restClient.execute(HttpMethod.DELETE, uri, null, null);
		uri = new URI(baseUrl + BASE_CONTROLLER_URL);
		ControllerConfig[] controllers = jsonUtils.toObject(restClient.execute(HttpMethod.GET, uri, null, null).getBody(),
				ControllerConfig[].class);
		assertThat(controllers).isEmpty();
	}

	@Test
	void testDeleteControllerNotExist() throws Exception {
		URI uri = new URI(baseUrl + BASE_ONE_CONTROLLER_URL + NOT_EXIST_ID);
		assertThat(restClient.execute(HttpMethod.DELETE, uri, null, null).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testGetAllRouters() throws Exception {
		URI uri = new URI(baseUrl + BASE_ROUTER_URL);
		RouterConfig[] routers = jsonUtils.toObject(restClient.execute(HttpMethod.GET, uri, null, null).getBody(),
				RouterConfig[].class);
		assertThat(routers).hasSize(1);
		assertThat(routers[0].getToUrl()).isEqualTo(EXIST_URL_PATH);
	}

	@Test
	void testGetOneRouter() throws Exception {
		URI uri = new URI(baseUrl + BASE_ONE_ROUTER_URL + EXIST_ID);
		RouterConfig router = jsonUtils.toObject(restClient.execute(HttpMethod.GET, uri, null, null).getBody(),
				RouterConfig.class);
		assertThat(router.getToUrl()).isEqualTo(EXIST_URL_PATH);
	}

	@Test
	void testGetOneRouterNotExist() throws Exception {
		URI uri = new URI(baseUrl + BASE_ONE_ROUTER_URL + NOT_EXIST_ID);
		assertThat(restClient.execute(HttpMethod.GET, uri, null, null).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testCreateRouter() throws Exception {
		RouterConfig config = new RouterConfig();
		config.setMethod(RequestMethod.GET);
		config.setUri(NEW_URL_PATH);
		config.setToUrl(EXIST_URL_PATH);

		URI uri = new URI(baseUrl + BASE_ROUTER_URL);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		restClient.execute(HttpMethod.POST, uri, headers, jsonUtils.toObjectNode(config).toString());

		RouterConfig[] routers = jsonUtils.toObject(restClient.execute(HttpMethod.GET, uri, null, null).getBody(),
				RouterConfig[].class);
		assertThat(routers).hasSize(2);
		assertThat(routers[1].getToUrl()).isEqualTo(EXIST_URL_PATH);
	}

	@Test
	void testCreateRouterExist() throws Exception {
		RouterConfig config = new RouterConfig();
		config.setMethod(RequestMethod.GET);
		config.setUri(EXIST_URL_PATH);
		config.setToUrl(NEW_URL_PATH);

		URI uri = new URI(baseUrl + BASE_ROUTER_URL);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		assertThat(restClient.execute(HttpMethod.POST, uri, headers, jsonUtils.toObjectNode(config).toString()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testCreateRouterEmpty() throws Exception {
		URI uri = new URI(baseUrl + BASE_ROUTER_URL);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		assertThat(restClient.execute(HttpMethod.POST, uri, headers, "").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testDeleteRouter() throws Exception {
		URI uri = new URI(baseUrl + BASE_ONE_ROUTER_URL + EXIST_ID);
		restClient.execute(HttpMethod.DELETE, uri, null, null);
		uri = new URI(baseUrl + BASE_ROUTER_URL);
		RouterConfig[] routers = jsonUtils.toObject(restClient.execute(HttpMethod.GET, uri, null, null).getBody(),
				RouterConfig[].class);
		assertThat(routers).isEmpty();
	}

	@Test
	void testDeleteRouterNotExist() throws Exception {
		URI uri = new URI(baseUrl + BASE_ONE_ROUTER_URL + NOT_EXIST_ID);
		assertThat(restClient.execute(HttpMethod.DELETE, uri, null, null).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

}
