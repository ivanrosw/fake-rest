package io.github.ivanrosw.fakerest.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.conf.YamlConfigurator;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import io.github.ivanrosw.fakerest.core.utils.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.config.location = classpath:controller-tests.yml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = FakeRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllersTests {

	private static final String ID_PARAM = "id";
	private static final String BAD_ID = "bad_id";
	private static final String BAD_DATA = "bad data";

	private static final String TEST_CONTROLLER_URI = "/test/";
	private static final String TEST_ROUTER_URI = "/test";
	private static final String TEST_CONTROLLER2_URI = "/test2";
	private static final String TEST_CONTROLLER3_URI_MODIFY = "/test3/";
	private static final String TEST_CONTROLLER3_URI = "/test3";
	private static final String TEST_CONTROLLER4_URI = "/test4";
	private static final String TEST_CONTROLLER5_URI = "/test5";

	@LocalServerPort
	private int port;

	private String baseUrl;

	@Autowired
	private RestClient restClient;
	@Autowired
	private JsonUtils jsonUtils;
	@MockBean
	private YamlConfigurator yamlConfigurator;

	@BeforeEach
	void init() throws Exception {
		baseUrl = "http://localhost:" + port;
	}

	@Test
	void testDynamicGetAllEmpty() throws Exception{
		String expectedArray = "[]";
		assertThat(getAll(TEST_CONTROLLER_URI)).isEqualTo(expectedArray);
	}

	@Test
	void testRouterDynamicGetAllEmpty() throws Exception {
		String expectedArray = "[]";
		assertThat(getAll(TEST_ROUTER_URI)).isEqualTo(expectedArray);
	}

	@Test
	void  testDynamicGetOneNotFound() throws Exception {
		assertThat(restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER_URI + "/" + BAD_ID), null, null).getStatusCode())
				.isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testDynamicPostCreateGenerateId() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");

		createOne(TEST_CONTROLLER_URI, expectedJson);
		assertThat(getOne(TEST_CONTROLLER_URI, jsonUtils.getString(expectedJson, ID_PARAM))).isEqualTo(expectedJson.toString());
	}

	@Test
	void testDynamicPostNullBodyBadRequest() throws Exception {
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER_URI), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testDynamicPostBadBodyBadRequest() throws Exception {
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER_URI), null, BAD_DATA).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testDynamicGetAllData() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		createOne(TEST_CONTROLLER_URI, expectedJson);
		assertThat(getAll(TEST_CONTROLLER_URI)).isEqualTo("[" + expectedJson + "]");
	}

	@Test
	void testRouterDynamicGetAllData() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		createOne(TEST_CONTROLLER_URI, expectedJson);
		assertThat(getAll(TEST_ROUTER_URI)).isEqualTo("[" + expectedJson + "]");
	}

	@Test
	void testDynamicPutUpdate() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		createOne(TEST_CONTROLLER_URI, expectedJson);

		jsonUtils.putString(expectedJson, "data", "new value");
		update(TEST_CONTROLLER_URI, jsonUtils.getString(expectedJson, ID_PARAM), expectedJson);
		assertThat(getOne(TEST_CONTROLLER_URI, jsonUtils.getString(expectedJson, ID_PARAM))).isEqualTo(expectedJson.toString());
	}

	@Test
	void testDynamicPutUpdateNullBodyBadRequest() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		createOne(TEST_CONTROLLER_URI, expectedJson);

		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl +TEST_CONTROLLER_URI + "/" + jsonUtils.getString(expectedJson, ID_PARAM)), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testDynamicPutUpdateBadBodyBadRequest() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		createOne(TEST_CONTROLLER_URI, expectedJson);

		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER_URI + "/" + jsonUtils.getString(expectedJson, ID_PARAM)), null, BAD_DATA).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testDynamicPutUpdateBadIdBadRequest() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		createOne(TEST_CONTROLLER_URI, expectedJson);

		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER_URI + "/" + BAD_ID), null, expectedJson.toString()).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testDynamicDeleteOk() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		createOne(TEST_CONTROLLER_URI, expectedJson);
		String expectedArray = "[]";

		delete(TEST_CONTROLLER_URI, jsonUtils.getString(expectedJson, ID_PARAM));
		assertThat(getAll(TEST_CONTROLLER_URI)).isEqualTo(expectedArray);
		assertThat(getAll(TEST_ROUTER_URI)).isEqualTo(expectedArray);
	}

	@Test
	void testDynamicDeleteBadIdBadRequest() throws Exception {
		ResponseEntity<String> response = restClient.execute(HttpMethod.DELETE, new URI(baseUrl + TEST_CONTROLLER_URI + "/" + BAD_ID), null, null);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testRouterDynamicPostWithGenerateId() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");

		createOne(TEST_ROUTER_URI, expectedJson);
		assertThat(getOne(TEST_ROUTER_URI, jsonUtils.getString(expectedJson, ID_PARAM))).isEqualTo(expectedJson.toString());
	}

	@Test
	void testRouterDynamicPostWithoutGenerateId() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		jsonUtils.putString(expectedJson, ID_PARAM, "1");

		createOne(TEST_CONTROLLER3_URI_MODIFY, expectedJson);
		assertThat(jsonUtils.getString(expectedJson, ID_PARAM)).isEqualTo("1");
	}

	@Test
	void testDynamicPostWithoutGenerateIdAlreadyExist() throws Exception {
		ObjectNode expectedJson = jsonUtils.createJson();
		jsonUtils.putString(expectedJson, "data", "value");
		createOne(TEST_CONTROLLER3_URI_MODIFY, expectedJson);

		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER3_URI_MODIFY), null, expectedJson.toString()).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testStaticGetWithoutAnswer() throws Exception {
		String actualAnswer = restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER2_URI), null, null).getBody();
		assertThat(actualAnswer).isNull();
	}

	@Test
	void testStaticGetWithAnswer() throws Exception {
		String expectedAnswer = "expected answer";
		String actualAnswer = restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER3_URI), null, null).getBody();
		assertThat(actualAnswer).isEqualTo(expectedAnswer);
	}

	@Test
	void testStaticPostWithoutConfiguredAnswer() throws Exception {
		String expectedAnswer = "expected answer";
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER2_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer);
	}

	@Test
	void testStaticPostWithConfiguredAnswer() throws Exception {
		String postBody = "expected answer";
		String expectedAnswer = "expected answer2";
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER3_URI), null, postBody).getBody()).isEqualTo(expectedAnswer);
	}

	@Test
	void testStaticPostNullBodyBadRequest() throws Exception {
		assertThat(restClient.execute(HttpMethod.POST, new URI(baseUrl + TEST_CONTROLLER2_URI), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testStaticPutWithoutConfiguredAnswer() throws Exception {
		String expectedAnswer = "expected answer";
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER2_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer);
	}

	@Test
	void testStaticPutWithConfiguredAnswer() throws Exception {
		String putBody = "expected answer";
		String expectedAnswer = "expected answer2";
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER3_URI), null, putBody).getBody()).isEqualTo(expectedAnswer);
	}

	@Test
	void testStaticPutNullBodyBadRequest() throws Exception {
		assertThat(restClient.execute(HttpMethod.PUT, new URI(baseUrl + TEST_CONTROLLER2_URI), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testStaticDeleteWithoutConfiguredAnswer() throws Exception {
		String expectedAnswer = "expected answer";
		assertThat(restClient.execute(HttpMethod.DELETE, new URI(baseUrl + TEST_CONTROLLER2_URI), null, expectedAnswer).getBody()).isEqualTo(expectedAnswer);
	}

	@Test
	void testStaticDeleteWithConfiguredAnswer() throws Exception {
		String deleteBody = "expected answer";
		String expectedAnswer = "expected answer2";
		assertThat(restClient.execute(HttpMethod.DELETE, new URI(baseUrl + TEST_CONTROLLER3_URI), null, deleteBody).getBody()).isEqualTo(expectedAnswer);
	}

	@Test
	void testStaticDeleteNullBodyBadRequest() throws Exception {
		assertThat(restClient.execute(HttpMethod.DELETE, new URI(baseUrl + TEST_CONTROLLER2_URI), null, null).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void testDelay() throws Exception {
		long now = System.currentTimeMillis();
		restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER4_URI), null, null).getBody();
		long processMs = System.currentTimeMillis() - now;
		assertThat(processMs).isGreaterThanOrEqualTo(10);
	}

	@Test
	void testRouterTimeout() throws URISyntaxException {
		ResponseEntity<String> response = restClient.execute(HttpMethod.GET, new URI(baseUrl + TEST_CONTROLLER5_URI), null, null);
		assertThat(response.getStatusCodeValue()).isEqualTo(408);
	}

	//UTILS

	private String getAll(String uri) throws Exception {
		return restClient.execute(HttpMethod.GET, new URI(baseUrl + uri), null, null).getBody();
	}

	private String getOne(String uri, String id) throws Exception {
		return restClient.execute(HttpMethod.GET, new URI(baseUrl + uri + "/" + id), null, null).getBody();
	}

	private void createOne(String uri, ObjectNode body) throws Exception {
		String result = restClient.execute(HttpMethod.POST, new URI(baseUrl + uri), null, body.toString()).getBody();
		ObjectNode resultJson = jsonUtils.toObjectNode(result);
		jsonUtils.putString(body, ID_PARAM, jsonUtils.getString(resultJson, ID_PARAM));
	}

	private void update(String uri, String id, ObjectNode body) throws Exception {
		String result = restClient.execute(HttpMethod.PUT, new URI(baseUrl + uri + "/" + id), null, body.toString()).getBody();
		ObjectNode resultJson = jsonUtils.toObjectNode(result);
		jsonUtils.putString(body, ID_PARAM, jsonUtils.getString(resultJson, ID_PARAM));
	}

	private void delete(String uri, String id) throws Exception {
		restClient.execute(HttpMethod.DELETE, new URI(baseUrl + uri + "/" + id), null, null);
	}

}
