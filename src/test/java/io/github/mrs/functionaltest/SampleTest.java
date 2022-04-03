package io.github.mrs.functionaltest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.AssertJUnit.assertEquals;

import io.restassured.response.ValidatableResponse;
import java.util.Properties;

import org.testng.internal.collections.Pair;

import org.testng.annotations.Test;

public class SampleTest extends FunctionalTest {
	// how do you test the testing framework? You write a test using the testing framework!

	Properties properties;

	@Test
	public void simpleExercise() throws Exception {
		SampleService service = new SampleService();
		Pair<SampleSerializer, ValidatableResponse> pair = service.fetchUsers();
		assertThat(pair.first().getData().size()).isGreaterThan(0); // always more than 10 permissions
	}

	@Test
	public void readExtraProperties () {
		assertEquals("bar", properties.get("foo"));
	}

	@Override
	protected String getExtraPropertyFileName(String redwoodEnv) {
		return "test.properties";
	}

	@Override
	protected void assignExtraProperties(Properties props) {
		this.properties = props;
	}

}
