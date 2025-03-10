//package com.benefitj.dataplatform.config;
//
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.SuperBuilder;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//@SuperBuilder
//@NoArgsConstructor
//@Data
//@ConfigurationProperties(prefix = "spring.datasource")
//public class DruidDataSourceOptions {
//
//  private String driverName;
//
//  private String url;
//
//  private String username;
//
//  private String password;
//
//  /**
//   * 连接池初始化大小
//   */
//  private int initialSize;
//
//  /**
//   * 连接池最小值
//   */
//  private int minIdle;
//
//  /**
//   * 连接池最大值
//   */
//  private int maxActive;
//
//  /**
//   * 配置获取连接等待超时的时间
//   */
//  private int maxWait;
//
//  /**
//   * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
//   */
//  private int timeBetweenEvictionRunsMillis;
//
//  /**
//   * 配置一个连接在池中最小生存的时间，单位是毫秒
//   */
//  private int minEvictableIdleTimeMillis;
//
//  /**
//   * 用来验证数据库连接的查询语句,这个查询语句必须是至少返回一条数据的SELECT语句
//   */
//  private String validationQuery;
//
//  /**
//   * 检测连接是否有效
//   */
//  private boolean testWhileIdle;
//
//  /**
//   * 申请连接时执行validationQuery检测连接是否有效。做了这个配置会降低性能。
//   */
//  private boolean testOnBorrow;
//
//  /**
//   * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
//   */
//  private boolean testOnReturn;
//
//  /**
//   * 是否缓存preparedStatement，也就是PSCache。
//   */
//  private boolean poolPreparedStatements;
//
//  /**
//   * 指定每个连接上PSCache的大小
//   */
//  private int maxPoolPreparedStatementPerConnectionSize;
//
//  /**
//   * 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
//   */
//  private String filters;
//
//  /**
//   * 通过connectProperties属性来打开mergeSql功能；慢SQL记录
//   */
//  private String connectionProperties;
//
//  /**
//   * Druid控制台配置：记录慢SQL
//   */
//  private String logSlowSql;
//
//  private boolean removeAbandoned;
//
//  private int removeAbandonedTimeout;
//
//  private boolean logAbandoned;
//
//
//}
