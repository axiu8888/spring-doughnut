package com.benefitj.mybatisplus;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.TimeUtils;
import com.benefitj.mybatisplus.dao.mapper.MysqlReportTaskMapper;
import com.benefitj.mybatisplus.entity.mysql.RecipelItem;
import com.benefitj.mybatisplus.entity.mysql.RecipelType;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.listener.EnableAppStateListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.stream.Stream;

@EnableAppStateListener
@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@SpringBootApplication
@Slf4j
public class DCMybatisPlusApp {
  public static void main(String[] args) {
    SpringApplication.run(DCMybatisPlusApp.class, args);
  }

  static {
    AppStateHook.registerStart(evt -> appStart());
    AppStateHook.registerStop(evt -> appStop());
  }

  public static void appStart() {
    log.info("app start...");

    MysqlReportTaskMapper mysqlReportTaskMapper = SpringCtxHolder.getBean(MysqlReportTaskMapper.class);
    List<JSONObject> list = mysqlReportTaskMapper.selectByItems(
        Stream.of(RecipelItem.values())
            .filter(ri -> ri.getType() == RecipelType.prSix)
            .toArray(RecipelItem[]::new),
        TimeUtils.toDate(2024, 1, 1),
        null//new Date()
    );
    log.info("list ==>: \n{}\n", JSON.toJSONString(list));

  }

  public static void appStop() {
  }


}
