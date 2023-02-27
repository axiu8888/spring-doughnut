package com.benefitj.spring.freemarker;

import com.benefitj.core.ClasspathUtils;
import com.benefitj.core.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class FreemarkerBuilderTest {

  @BeforeEach
  public void setUp() {
  }

  private ClassDescriptor newUserEntity() {
    return ClassDescriptor.builder()
        .copyright("/*\n" +
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
        .basePackage("com.benefitj")
        .lombok(true)
        .description("用户")
        .author("dingxiuan")
        .className("User")
        .annotations(Arrays.asList(
            AnnotationDescriptor.builder()
                .type(Table.class)
                .value("name=\"sys_user\"")
                .build()
        ))
        .fields(Arrays.asList(
            FieldDescriptor.builder()
                .modifier(ModiferType.PRIVATE)
                .name("id")
                .type(String.class)
                .description("ID")
                .annotations(Arrays.asList(
                    AnnotationDescriptor.builder()
                        .type(Id.class)
                        .build()
                    , AnnotationDescriptor.builder()
                        .type(Column.class)
                        .value("name=\"id\", columnDefinition=\"varchar(32) comment '主键'\"")
                        .build()
                ))
                .build()
            , FieldDescriptor.builder()
                .modifier(ModiferType.PRIVATE)
                .name("username")
                .type(String.class)
                .description("用户名")
                .annotations(Arrays.asList(
                    AnnotationDescriptor.builder()
                        .type(Column.class)
                        .value("name=\"username\", columnDefinition=\"varchar(30) comment '用户名'\"")
                        .build()
                ))
                .build()
            , FieldDescriptor.builder()
                .modifier(ModiferType.PRIVATE)
                .name("gender")
                .type(String.class)
                .description("性别")
                .annotations(Arrays.asList(
                    AnnotationDescriptor.builder()
                        .type(Column.class)
                        .value("name=\"gender\", columnDefinition=\"tinyint comment '性别'\"")
                        .build()
                ))
                .build()
            , FieldDescriptor.builder()
                .modifier(ModiferType.PRIVATE)
                .name("birthday")
                .type(Date.class)
                .description("出生日期")
                .annotations(Arrays.asList(
                    AnnotationDescriptor.builder()
                        .type(Column.class)
                        .value("name=\"birthday\", columnDefinition=\"date comment '出生日期'\"")
                        .build()
                ))
                .build()
            , FieldDescriptor.builder()
                .modifier(ModiferType.PRIVATE)
                .name("creator")
                .type(String.class)
                .description("创建者")
                .annotations(Arrays.asList(
                    AnnotationDescriptor.builder()
                        .type(Column.class)
                        .value("name=\"creator\", columnDefinition=\"varchar(32) comment '创建者'\"")
                        .build()
                ))
                .build()
            , FieldDescriptor.builder()
                .modifier(ModiferType.PRIVATE)
                .name("createTime")
                .type(Date.class)
                .description("创建时间")
                .annotations(Arrays.asList(
                    AnnotationDescriptor.builder()
                        .type(Column.class)
                        .value("name=\"create_time\", columnDefinition=\"datetime comment '创建时间'\"")
                        .build()
                ))
                .build()
            , FieldDescriptor.builder()
                .modifier(ModiferType.PRIVATE)
                .name("updateTime")
                .type(Date.class)
                .description("更新时间")
                .annotations(Arrays.asList(
                    AnnotationDescriptor.builder()
                        .type(Column.class)
                        .value("name=\"update_time\", columnDefinition=\"datetime comment '更新时间'\"")
                        .build()
                ))
                .build()
        ))
        .build();
  }

  @Test
  public void testEntity() {
    ClassDescriptor cd = newUserEntity();
    File dir = ClasspathUtils.getFile("test.ftl").getParentFile();
    File userJava = CodeGenerator.getInstance().writeEntity(cd, dir);
    System.err.println(IOUtils.readFileAsString(userJava, StandardCharsets.UTF_8));
  }

  @Test
  public void testMapper() {
    ClassDescriptor cd = newUserEntity();
    File dir = ClasspathUtils.getFile("test.ftl").getParentFile();
    File userJava = CodeGenerator.getInstance().writeMapper(cd, dir);
    System.err.println(IOUtils.readFileAsString(userJava, StandardCharsets.UTF_8));
  }

  @Test
  public void testBusiness() {
    ClassDescriptor cd = newUserEntity();
    File dir = ClasspathUtils.getFile("test.ftl").getParentFile();
    Map<String, File> userJava = CodeGenerator.getInstance().writeBusiness(cd, dir);
    userJava.forEach((key, file) -> {
      System.err.println("\n-------------------------- " + key + " -----------------------------\n");
      System.err.println(IOUtils.readFileAsString(file, StandardCharsets.UTF_8));
      System.err.println("\n-------------------------- " + key + " -----------------------------\n");
    });
  }

  public void tearDown() throws Exception {
  }
}