package com.hsrg.fileserver;

import com.benefitj.core.EventLoop;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileServerApp.class)
class FileServerAppTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    EventLoop.sleepSecond(30);
  }
}