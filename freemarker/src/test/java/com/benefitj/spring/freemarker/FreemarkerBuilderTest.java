package com.benefitj.spring.freemarker;

import com.benefitj.core.ClasspathUtils;
import com.benefitj.core.IOUtils;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

public class FreemarkerBuilderTest extends TestCase {

  @BeforeEach
  @Before
  public void setUp() {
  }

  @Test
  public void testEntity() {
    ClassDescriptor cd = new ClassDescriptor()
        .setCopyright("/*\n" +
            " * Licensed to the Apache Software Foundation (ASF) under one\n" +
            " * or more contributor license agreements.  See the NOTICE file\n" +
            " * distributed with this work for additional information\n" +
            " * regarding copyright ownership.  The ASF licenses this file\n" +
            " * to you under the Apache License, Version 2.0 (the\n" +
            " * \"License\"); you may not use this file except in compliance\n" +
            " * with the License.  You may obtain a copy of the License at\n" +
            " *\n" +
            " *   http://www.apache.org/licenses/LICENSE-2.0\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing,\n" +
            " * software distributed under the License is distributed on an\n" +
            " * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n" +
            " * KIND, either express or implied.  See the License for the\n" +
            " * specific language governing permissions and limitations\n" +
            " * under the License.\n" +
            " */\n")
        .setPackageName("com.benefitj.entity")
        .setDescription("用户")
        .setAuthor("dingxiuan")
        .setClassName("User")
        .setFieldDescriptors(Arrays.asList(
            new FieldDescriptor()
                .setName("id")
                .setType(String.class)
                .setDescription("ID")
            , new FieldDescriptor()
                .setName("username")
                .setType(String.class)
                .setDescription("用户名")
            , new FieldDescriptor()
                .setName("gender")
                .setType(String.class)
                .setDescription("性别")
            , new FieldDescriptor()
                .setName("birthday")
                .setType(Date.class)
                .setDescription("出生日期")
            , new FieldDescriptor()
                .setName("creator")
                .setType(String.class)
                .setDescription("创建者")
            , new FieldDescriptor()
                .setName("createTime")
                .setType(Date.class)
                .setDescription("创建时间")
            , new FieldDescriptor()
                .setName("updateTime")
                .setType(Date.class)
                .setDescription("更新时间")
        ));
    File dir = ClasspathUtils.getFile("test.ftl").getParentFile();
    File userJava = ClassGeneratorTemplate.getInstance().writeEntity(cd, dir);
    System.err.println(new String(IOUtils.readFileBytes(userJava), StandardCharsets.UTF_8));

  }



  @Test
  public void testMapper() {
    ClassDescriptor cd = new ClassDescriptor()
        .setCopyright("/*\n" +
            " * Licensed to the Apache Software Foundation (ASF) under one\n" +
            " * or more contributor license agreements.  See the NOTICE file\n" +
            " * distributed with this work for additional information\n" +
            " * regarding copyright ownership.  The ASF licenses this file\n" +
            " * to you under the Apache License, Version 2.0 (the\n" +
            " * \"License\"); you may not use this file except in compliance\n" +
            " * with the License.  You may obtain a copy of the License at\n" +
            " *\n" +
            " *   http://www.apache.org/licenses/LICENSE-2.0\n" +
            " *\n" +
            " * Unless required by applicable law or agreed to in writing,\n" +
            " * software distributed under the License is distributed on an\n" +
            " * \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY\n" +
            " * KIND, either express or implied.  See the License for the\n" +
            " * specific language governing permissions and limitations\n" +
            " * under the License.\n" +
            " */\n")
        .setPackageName("com.benefitj.mapper")
        .setDescription("用户")
        .setAuthor("dingxiuan")
        .setClassName("User");
    File dir = ClasspathUtils.getFile("test.ftl").getParentFile();
    File userJava = ClassGeneratorTemplate.getInstance().writeMapper(cd, dir);
    System.err.println(new String(IOUtils.readFileBytes(userJava), StandardCharsets.UTF_8));

  }

  public void tearDown() throws Exception {
  }
}