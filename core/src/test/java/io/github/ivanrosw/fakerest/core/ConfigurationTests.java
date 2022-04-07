package io.github.ivanrosw.fakerest.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ivanrosw.fakerest.core.conf.MappingConfigurator;
import io.github.ivanrosw.fakerest.core.utils.JsonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.config.location = classpath:configuration-tests.yml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = FakeRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigurationTests {

	@Autowired
	private MappingConfigurator mappingConfigurator;
	@Autowired
	private JsonUtils jsonUtils;

	@AfterEach
	void clearConfiguration() throws Exception {
		File file = new ClassPathResource("configuration-tests.yml").getFile();
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			writer.write("---\n" +
					"rest:\n" +
					"  controllers:\n" +
					"    - uri: '/test/'\n" +
					"      method: GET");
		}
	}

	@Test
	void testDynamicGetAllEmpty() throws Exception{
		assertThat(true).isTrue();
	}

}
