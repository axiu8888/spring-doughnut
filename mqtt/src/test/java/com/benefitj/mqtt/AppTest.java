package com.benefitj.mqtt;


import com.benefitj.core.ShutdownHook;
import com.benefitj.mqtt.publisher.MqttPublisherApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

@SpringBootTest(classes = {MqttPublisherApplication.class, MqttPublisherApplication.Example.class})
@Slf4j
class AppTest {

  @Test
  void test() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    ShutdownHook.register(latch::countDown);
    latch.await();
  }

}
