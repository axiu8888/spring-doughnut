package com.benefitj.spring.dynamicdatasource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * JDBC URL
 */
public class JdbcUrl {

  /**
   * URI
   */
  private String uri;
  /**
   * 协议, JDBC
   */
  private String schema;
  /**
   * 数据库类型
   */
  private String type;
  /**
   * 主机
   */
  private String host;
  /**
   * 端口
   */
  private Integer port;
  /**
   * 数据库名称
   */
  private String database;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 参数
   */
  private Map<String, String> parameters = new LinkedHashMap<>();

  public JdbcUrl(String uri) {
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  /**
   * 转换成 JDBC URL
   *
   * @return 返回URL
   */
  public String toJdbc() {
    return toJdbc(Type.of(getType()));
  }

  /**
   * 转换成 JDBC URL
   *
   * @param type 数据库类型
   * @return 返回URL
   */
  public String toJdbc(Type type) {
    return toJdbc(getHost(), getPort(), getDatabase(), type);
  }

  /**
   * 转换成 JDBC URL
   *
   * @param type 数据库类型
   * @return 返回URL
   */
  public String toJdbc(String host, int port, String database, Type type) {
    if (type != null) {
      switch (type) {
        case MariaDB:
          return String.format("jdbc:mariadb://%s:%d/%s%s"
              , host, port, database, toQueries("?", "&"));
        case MySQL:
          return String.format("jdbc:mysql://%s:%d/%s%s"
              , host, port, database, toQueries("?", "&"));
        case PostgresSQL:
          return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        case Oracle:
          return String.format("jdbc:oracle:thin:@%s:%d/%s", host, port, database);
        case H2:
          return String.format("jdbc:h2:~/%s", getDatabase());
        case SQLServer:
          Map<String, String> parameters = new LinkedHashMap<>(getParameters());
          parameters.put("databaseName", database);
          return String.format("jdbc:sqlserver://%s:%d%s;"
              , host, port, toQueries(parameters, ";", ";"));
      }
    }
    return getUri();
  }

  protected String toQueries(String symbol, String separator) {
    return toQueries(getParameters(), symbol, separator);
  }

  /**
   * 解析
   */
  protected JdbcUrl parse() {
    // Oracle = jdbc:oracle:thin:@host:port:SID
    // Oracle = jdbc:oracle:thin:@//host:port/service_name
    // Oracle = jdbc:oracle:thin:@TNSName
    // MySQL = jdbc:mysql://host:port/db_name?queries
    // PostgreSQL = jdbc:postgresql://host:port/db_name?queries
    // H2 = jdbc:h2:~/test
    // SQLServer = "jdbc:sqlserver://localhost:1433;databaseName=test;user=admin;password=123456;"

    String[] split = uri.split(":");
    if (!split[0].equalsIgnoreCase("jdbc")) {
      throw new IllegalArgumentException("不支持的schema！");
    }
    // JDBC
    this.setSchema(split[0]);
    // oracle/mysql/mariadb/postgresql ...
    this.setType(split[1]);

    Type type = Type.of(this.getType());
    if (type != Type.H2) {
      // 主机
      int index = type == Type.Oracle ? 3 : 2;
      String host = split[index].replaceFirst("//", "");
      host = host.startsWith("@") ? host.replaceFirst("@", "") : host;
      host = host.startsWith("@//") ? host.replaceFirst("@//", "") : host;
      this.setHost(host);

      String suffixPart = split[index + 1];
      if (type != Type.SQLServer) {
        // 端口
        this.setPort(Integer.valueOf(substrIndex(suffixPart, "/")));

        String part = find(type == Type.Oracle && !suffixPart.contains("/")
            ? split[index + 2] : suffixPart, "/", 2, 0);
        // 数据库名称
        this.setDatabase(substrIndex(part, "?"));
        // 参数
        int indexOf = part.indexOf("?");
        if (indexOf > 0 && indexOf < part.length() - 1) {
          String[] split2 = find(part, "?", 2, 0).split("&");
          if (split2.length > 0) {
            Stream.of(split2)
                .filter(s -> !isBlank(s))
                .map(s -> s.split("="))
                .forEach(strings -> this.getParameters().put(strings[0], strings[1]));
          }
        }
      } else {
        // 端口
        this.setPort(Integer.valueOf(substrIndex(suffixPart, ";")));
        String[] split2 = find(suffixPart, ";", 2, 0).split(";");
        Stream.of(split2)
            .filter(s -> !isBlank(s))
            .map(s -> s.split("="))
            .forEach(strings -> this.getParameters().put(strings[0], strings[1]));
        // 数据库名称
        this.setDatabase(this.getParameters().get("databaseName"));
        if (isBlank(this.getUsername())) {
          this.setUsername(this.getParameters().get("user"));
        }
      }
    } else {
      // 数据库名称
      this.setDatabase(substrLastIndex(getUri(), "/"));
    }
    if (isBlank(this.getUsername())) {
      this.setUsername(this.getParameters().get("username"));
    }
    if (isBlank(this.getPassword())) {
      this.setPassword(this.getParameters().get("password"));
    }

    return this;
  }

  public static JdbcUrl parse(String uri) {
    return parse(uri, null, null);
  }

  public static JdbcUrl parse(String uri, String username, String password) {
    if (isBlank(uri)) {
      throw new IllegalArgumentException("uri不能为空!");
    }
    JdbcUrl jdbcUrl = new JdbcUrl(uri.trim());
    jdbcUrl.setUsername(username);
    jdbcUrl.setPassword(password);
    return jdbcUrl.parse();
  }


  public enum Type {
    /**
     * MySQL
     */
    MySQL,
    /**
     * MariaDB
     */
    MariaDB,
    /**
     * Oracle
     */
    Oracle,
    /**
     * PostgreSQL
     */
    PostgresSQL,
    /**
     * H2
     */
    H2,
    /**
     * SQL Server
     */
    SQLServer,

    ;

    public static Type of(String name) {
      for (Type value : values()) {
        if (value.name().equalsIgnoreCase(name)) {
          return value;
        }
      }
      return null;
    }
  }


  /**
   * 转换Query
   *
   * @param parameters 参数
   * @param symbol     符号
   * @param separator  分隔符
   * @return 返回拼接的参数
   */
  protected String toQueries(Map<String, String> parameters, String symbol, String separator) {
    if (parameters.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    sb.append(symbol);
    parameters.forEach((key, value) ->
        sb.append(key).append("=").append(value).append(separator));
    return sb.substring(0, sb.length() - 1);
  }


  private static boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }

  private static boolean isType(String type, String required) {
    return type.equalsIgnoreCase(required);
  }

  private static String find(String str, String find, int index, int from) {
    int indexOf = 0;
    while (index > 0) {
      indexOf = from + indexOf < str.length() ? str.indexOf(find, from + indexOf) : -1;
      index--;
      if (index <= 0 && indexOf > 0) {
        return str.substring(indexOf + 1).trim();
      }
    }
    return str;
  }

  /**
   * 截取字符串的前半部分
   *
   * @param str  字符串
   * @param find 查找的子字符串
   * @return 返回截取的字符串
   */
  private static String substrIndex(String str, String find) {
    return substr(str, find, false, false);
  }

  /**
   * 截取字符串的后半部分
   *
   * @param str  字符串
   * @param find 查找的子字符串
   * @return 返回截取的字符串
   */
  private static String substrLastIndex(String str, String find) {
    return substr(str, find, true, true);
  }

  /**
   * 截取字符串
   *
   * @param str       字符串
   * @param find      查找的子字符串
   * @param lastIndex 是否从后查找
   * @param suffix    是否取截取后的后半部分
   * @return 返回截取的字符串
   */
  private static String substr(String str, String find, boolean lastIndex, boolean suffix) {
    int index = lastIndex ? str.lastIndexOf(find) : str.indexOf(find);
    return index >= 0
        ? (suffix ? str.substring(index + 1) : str.substring(0, index))
        : str;
  }

}
