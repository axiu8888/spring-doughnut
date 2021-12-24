package com.benefitj.spring.freemarker;

import com.benefitj.core.ClasspathUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerBuilderTest extends TestCase {

  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testWrite() {
    try {
      File dir = ClasspathUtils.getFile("test.ftl").getParentFile();
      Configuration configuration = new FreemarkerBuilder()
          // 模板目录
          .setDirectoryForTemplateLoading(dir)
          // 3.设置字符集
          .setDefaultEncoding("utf-8")
          .getConfiguration();

      System.err.println("dir: " + dir.getAbsolutePath());

      Template template = configuration.getTemplate("test.ftl");
      StringWriter writer = new StringWriter();
      // 输出数据模型到文件中
      template.process(new HashMap<>(){{
        put("name", "周杰伦");
        put("message", "我是你的老歌迷了");
      }}, writer);
      System.err.println(writer.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void tearDown() throws Exception {
  }
}