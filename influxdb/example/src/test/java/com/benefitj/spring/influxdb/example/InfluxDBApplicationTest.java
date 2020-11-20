package com.benefitj.spring.influxdb.example;

import com.alibaba.fastjson.JSON;
import com.benefitj.spring.influxdb.template.RxJavaInfluxDBTemplate;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InfluxDBApplicationTest {

  private static final Logger log = LoggerFactory.getLogger(InfluxDBApplicationTest.class);

  @Autowired
  private RxJavaInfluxDBTemplate template;

  @Test
  public void testWrite() {
    AllRates allRates = new AllRates();
    allRates.setDeviceId("2333");
    allRates.setTime(System.currentTimeMillis() / 1000);
    allRates.setHeartRate((short)60);
    allRates.setSpo2((byte)94);
    allRates.setRespRate((short)16);
    allRates.setGesture(1);
    allRates.setEnergy(1.0);
    String line = template.convert(allRates).lineProtocol();
    System.err.println(line);

    template.write(allRates);
    //template.write(line);
  }

  @Test
  public void testQueryForExcel() {
  }

  /**
   * 删除数据表
   */
  @Test
  public void testDropMeasurements() {
    String database = template.getDatabase();
    QueryResult result = template.postQuery(database, "DROP MEASUREMENT hs_all_rates");
    System.err.println(JSON.toJSONString(result));
  }

  /**
   * 删除数据库
   */
  @Test
  public void testDropDB() {
    String database = template.getDatabase();
    QueryResult result = template.postQuery(database, "DROP DATABASE " + database);
    System.err.println(JSON.toJSONString(result));
  }

}
