package com.benefitj.examples;


import co.elastic.clients.elasticsearch._types.mapping.FieldType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@Slf4j
class ElasticsearchTest {

  @Autowired
  ElasticsearchRestTemplate template;

  @Test
  void test_FieldType() {
    String value = Stream.of(FieldType.values())
        .map(FieldType::jsonValue)
        .collect(Collectors.joining(", "));
    log.info("-->: \n\n{}\n", value);
  }


}
