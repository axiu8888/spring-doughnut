package com.benefitj.examples;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

@SpringBootTest
class ExampleTests {

	@Autowired
	ElasticsearchTemplate template;

	@Test
	void test_create() {

//		template.

	}

}
