package io.github.ivanrosw.fakerest.core;

import io.github.ivanrosw.fakerest.core.conf.MappingConfigurator;
import io.github.ivanrosw.fakerest.core.conf.YamlConfigurator;
import io.github.ivanrosw.fakerest.core.model.ControllerConfig;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"spring.config.location = classpath:configuration-tests.yml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = FakeRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigurationTests {

	private static final String NEW_URL_PATH = "/newUrl";

	@Autowired
	private MappingConfigurator mappingConfigurator;
	@Autowired
	private JsonUtils jsonUtils;

	@MockBean
	private YamlConfigurator yamlConfigurator;

	@Test
	void testCreateController() throws Exception {
		ControllerConfig config = new ControllerConfig();
		config.setMethod(RequestMethod.GET);
		config.setUri(NEW_URL_PATH);
		mappingConfigurator.registerController(config);
		assertThat(mappingConfigurator.getAllControllersCopy()).hasSize(2);
	}

	@Test
	void testDeleteController() throws Exception {
		assertThat(mappingConfigurator.getAllControllersCopy().size()).isOne();
		mappingConfigurator.unregisterController("1");
		assertThat(mappingConfigurator.getAllControllersCopy()).isEmpty();
	}

}
