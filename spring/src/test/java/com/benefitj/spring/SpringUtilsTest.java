package com.benefitj.spring;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;


@Slf4j
class SpringUtilsTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void toJson() {
    log.info("\n{}", JsonUtils.toJson(new HashMap<String, Object>(){{
      put("key1", "value1");
      put("key2", "value2");
      put("key3", "value3");
      put("key4", "value4");
    }}, true));
  }

  @Test
  void test_parseToken() {
    String[] result = ServletUtils.parseBasicToken("Basic Y3diVGVzdDpoc3JnODg4QA==");
    log.info("{}", JSON.toJSONString(result));
  }

}