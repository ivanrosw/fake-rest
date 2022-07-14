//package io.github.ivanrosw.fakerest.core;
//
//import io.github.ivanrosw.fakerest.core.conf.ConfigException;
//import io.github.ivanrosw.fakerest.core.conf.ControllerMappingConfigurator;
//import io.github.ivanrosw.fakerest.core.conf.MappingConfiguratorData;
//import io.github.ivanrosw.fakerest.core.conf.RouterMappingConfigurator;
//import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
//import io.github.ivanrosw.fakerest.core.model.ControllerFunctionMode;
//import io.github.ivanrosw.fakerest.core.model.RouterConfig;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@TestPropertySource(properties = {"spring.config.location = classpath:configuration-tests.yml"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//@SpringBootTest(classes = FakeRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class MappingConfiguratorTest {
//
//	private static final String NEW_URL_PATH = "/newUrl";
//	private static final String EXIST_URL_PATH = "/test/";
//	private static final String EXIST_ID = "1";
//	private static final String NOT_EXIST_ID = "999";
//
//	@Autowired
//	private MappingConfiguratorData mappingConfigurator;
//	@Autowired
//	private ControllerMappingConfigurator controllerMappingConfigurator;
//	@Autowired
//	private RouterMappingConfigurator routerMappingConfigurator;
//
//	@AfterEach
//	void clearConfig() throws Exception{
//		File file = new File(getClass().getClassLoader().getResource("configuration-tests.yml").getFile());
//		try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
//			fileWriter.write("---\n" +
//					"rest:\n" +
//					"  controllers:\n" +
//					"    - uri: '/test/'\n" +
//					"      method: GET\n" +
//					"      functionMode: READ\n" +
//					"  routers:\n" +
//					"    - uri: '/test'\n" +
//					"      toUrl: '/test/'\n" +
//					"      method: GET");
//		}
//	}
//
//	@Test
//	void testCreateController() throws Exception {
//		ControllerConfig config = new ControllerConfig();
//		config.setMethod(RequestMethod.GET);
//		config.setUri(NEW_URL_PATH);
//		config.setFunctionMode(ControllerFunctionMode.READ);
//		controllerMappingConfigurator.registerController(config);
//		assertThat(mappingConfigurator.getAllControllersCopy()).hasSize(2);
//	}
//
//	@Test
//	void testDeleteController() throws Exception {
//		assertThat(mappingConfigurator.getAllControllersCopy().size()).isOne();
//		controllerMappingConfigurator.unregisterController(EXIST_ID);
//		assertThat(mappingConfigurator.getAllControllersCopy()).isEmpty();
//	}
//
//	@Test
//	void testCreateControllerExist() {
//		ControllerConfig config = new ControllerConfig();
//		config.setMethod(RequestMethod.GET);
//		config.setUri(EXIST_URL_PATH);
//		config.setFunctionMode(ControllerFunctionMode.READ);
//
//		Exception exception = null;
//		try {
//			controllerMappingConfigurator.registerController(config);
//		} catch (Exception e) {
//			exception = e;
//		}
//		assertThat(exception).isNotNull().isInstanceOf(ConfigException.class);
//	}
//
//	@Test
//	void testDeleteControllerNotExist() {
//		Exception exception = null;
//		try {
//			controllerMappingConfigurator.unregisterController(NOT_EXIST_ID);
//		} catch (Exception e) {
//			exception = e;
//		}
//		assertThat(exception).isNotNull().isInstanceOf(ConfigException.class);
//	}
//
//	@Test
//	void testCreateRouterOk() throws Exception {
//		RouterConfig routerConfig = new RouterConfig();
//		routerConfig.setToUrl(EXIST_URL_PATH);
//		routerConfig.setUri(NEW_URL_PATH);
//		routerConfig.setMethod(RequestMethod.GET);
//		routerMappingConfigurator.registerRouter(routerConfig);
//		assertThat(mappingConfigurator.getAllRoutersCopy()).hasSize(2);
//	}
//
//	@Test
//	void testDeleteRouterOk() throws Exception {
//		assertThat(mappingConfigurator.getAllRoutersCopy().size()).isOne();
//		routerMappingConfigurator.unregisterRouter(EXIST_ID);
//		assertThat(mappingConfigurator.getAllRoutersCopy()).isEmpty();
//	}
//
//	@Test
//	void testCreateRouterExist() {
//		RouterConfig routerConfig = new RouterConfig();
//		routerConfig.setToUrl(NEW_URL_PATH);
//		routerConfig.setUri(EXIST_URL_PATH);
//		routerConfig.setMethod(RequestMethod.GET);
//
//		Exception exception = null;
//		try {
//			routerMappingConfigurator.registerRouter(routerConfig);
//		} catch (Exception e) {
//			exception = e;
//		}
//		assertThat(exception).isNotNull().isInstanceOf(ConfigException.class);
//	}
//
//	@Test
//	void testDeleteRouterNotExist() {
//		Exception exception = null;
//		try {
//			routerMappingConfigurator.unregisterRouter(NOT_EXIST_ID);
//		} catch (Exception e) {
//			exception = e;
//		}
//		assertThat(exception).isNotNull().isInstanceOf(ConfigException.class);
//	}
//}
