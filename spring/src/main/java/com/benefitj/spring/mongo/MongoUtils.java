package com.benefitj.spring.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.stream.Stream;

/**
 * mongo工具
 */
public class MongoUtils {

  /**
   * j检测是否支持查询时使用磁盘
   */
  public static boolean isAllowDiskUse(MongoTemplate template) {
    // 获取服务器信息
    Document serverStatus = template.getMongoDatabaseFactory()
        .getMongoDatabase("admin")
        .runCommand(new Document("buildInfo", 1));
    // 提取并返回 MongoDB 版本信息
    String version = serverStatus.getString("version");
    Integer[] versionSplit = Stream.of(version.split("\\."))
        .map(Integer::parseInt)
        .toArray(Integer[]::new);
    // 支持磁盘查询需要大于等于4.4的版本
    return versionSplit[0] > 4 || (versionSplit[0] == 4 && versionSplit[1] >= 4);
  }

  /**
   * 如果支持，设置磁盘查询
   */
  public static <T extends Query> T setIfAllowDiskUse(MongoTemplate template, T query) {
    if (isAllowDiskUse(template)) query.allowDiskUse(true);
    return query;
  }

  /**
   * 根据ID查询
   * <p/>
   * 低版本支持id等于_id，高版本不支持
   */
  public static Criteria idCriteria(String id) {
    return Criteria.where("_id").is(id).orOperator(Criteria.where("id").is(id));
  }

  /**
   * 根据ID查询
   * <p/>
   * 低版本支持id等于_id，高版本不支持
   */
  public static Query idQuery(String id) {
    return Query.query(idCriteria(id));
  }

}
