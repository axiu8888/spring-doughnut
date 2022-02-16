package com.benefitj.system.hibernate;

import com.benefitj.system.model.SysRoleEntity;
import com.benefitj.system.model.SysRolePermissionEntity;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.EnumSet;
import java.util.Properties;

//@SpringBootApplication
public class EntityTableHelper {
  public static void main(String[] args) {

//    SpringApplication.run(SystemApplication.class, args);

    Properties props = new Properties();
    props.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
    props.put("hibernate.connection.url", "jdbc:mysql://192.168.1.203:53306/test");
    props.put("hibernate.connection.username", "root");
    props.put("hibernate.connection.password", "hsrg8888");
    props.put("hibernate.hbm2ddl.auto", "update");
    //props.put("hibernate.show_sql", "true");
    props.put("hibernate.use_sql_comments", "true");
    props.put("hibernate.format_sql", "true");
    props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");

    Configuration conf = new Configuration();
    conf.setProperties(props);
    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
        //.configure()
        .applySettings(props)
        .build();

    Metadata metadata = new MetadataSources(serviceRegistry)
        .addAnnotatedClass(SysRolePermissionEntity.class)
        .addAnnotatedClass(SysRoleEntity.class)
        .buildMetadata();

    // 工具类
    SchemaExport export = new SchemaExport();
    // 打到控制台，输出到数据库
    // 第一个参数 输出DDL到控制台
    // 第二个参数 执行DDL语言创建表
    export.create(EnumSet.of(TargetType.DATABASE), metadata);

  }

}
