package com.benefitj.dataplatform.pg;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.CatchUtils;
import com.benefitj.dataplatform.pg.dto.ColumnDefine;
import com.benefitj.dataplatform.pg.dto.IndexDefine;
import com.benefitj.dataplatform.pg.dto.TableDefine;
import com.benefitj.dataplatform.pg.dto.TableType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Postgres 数据库操作
 */
@Slf4j
public class PostgresHelper implements DatabaseHelper {

  String schemaName = "public";
  DataSource dataSource;

  final AtomicReference<Connection> connectionRef = new AtomicReference<>();

  public PostgresHelper() {
  }

  public PostgresHelper(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public PostgresHelper(String schemaName, DataSource dataSource) {
    this.schemaName = schemaName;
    this.dataSource = dataSource;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Connection getConnection() {
    try {
      Connection conn = connectionRef.get();
      if (conn == null || conn.isClosed()) {
        synchronized (this) {
          if ((conn = connectionRef.get()) == null || conn.isClosed()) {
            connectionRef.set(conn = getDataSource().getConnection());
          }
        }
      }
      return conn;
    } catch (SQLException e) {
      throw new SqlException(CatchUtils.findRoot(e));
    }
  }

  @Override
  public boolean existDatabase(String dbName) {
    return pstmtQuery(getConnection()
        , "SELECT 1 AS exist FROM pg_database WHERE datname = ?"
        , new Object[]{dbName}
        , json -> json.getIntValue("exist", 0))
        .stream()
        .mapToInt(Integer::intValue)
        .sum() > 0;
  }

  @Override
  public int createDatabase(String dbName) {
    return createDatabase(dbName, "postgres", "postgres", "UTF-8");
  }

  public int createDatabase(String dbName, String owner, String template, String encoding) {
    return stmtUpdate(getConnection(), SQLGenerator.get().createDatabase(dbName, owner, template, encoding));
  }

  @Override
  public List<String> getDatabases() {
    return stmtQuery(getConnection()
        , "SELECT datname FROM pg_database WHERE datistemplate = false"
        , json -> json.getString("datname"));
  }

  @Override
  public int existTable(String table) {
    String schemaName = getSchemaName(); // 默认模式是 public
    return pstmtQuery(getConnection()
        , "SELECT 1 AS exist FROM pg_tables WHERE schemaname = ? AND tablename = ?"
        , new Object[]{schemaName, table}
        , json -> json.getIntValue("exist", 0)
    )
        .stream()
        .mapToInt(Integer::intValue)
        .sum();
  }

  @Override
  public List<TableDefine> getTables(boolean containsView) {
    // 查询 public schema 下的所有表
    String schemaName = getSchemaName();
    Connection conn = getConnection();
    List<String> tableNames = pstmtQuery(conn
        //SELECT * FROM information_schema.tables WHERE table_schema = 'public'
        , "SELECT table_name FROM information_schema.tables WHERE table_schema = ?"
            + (containsView ? "" : " AND table_type = 'BASE TABLE'") + ";"
        , new Object[]{schemaName}
        , record -> record.getString("table_name"));
    final List<TableDefine> tables = new LinkedList<>();
    for (String tableName : tableNames) {
      List<ColumnDefine> columns = pstmtQuery(conn
          , "SELECT * FROM information_schema.columns WHERE table_schema = ? AND table_name = ?"
          , new Object[]{schemaName, tableName}
          , record -> record.toJavaObject(ColumnDefine.class)
      );
      // 主键
      List<String> primaryKeys = getPrimaryKeys(conn, tableName);
      columns.forEach(c -> c.setPrimaryKey(primaryKeys.contains(c.getColumnName())));
      // 字段注释
      JSONObject comments = getColumnComments(
          tableName, columns.stream().map(ColumnDefine::getColumnName).toArray(String[]::new));
      columns.forEach(c -> c.setColumnComment(comments.getString(c.getColumnName())));
      // 表注释
      String tableComment = getTableComment(tableName);
      // 索引
      List<IndexDefine> indexes = getIndexes(tableName);
      // 表信息对象
      tables.add(TableDefine.builder()
          .tableName(tableName)
          .columns(columns)
          .tableComment(tableComment)
          .primaryKeys(primaryKeys)
          .indexes(indexes)
          .tableType(getTableType(tableName).get(tableName).name())
          .build());
    }
    return tables;
  }

  @Override
  public List<String> getPrimaryKeys(String tableName) {
    return getPrimaryKeys(getConnection(), tableName);
  }

  public List<String> getPrimaryKeys(Connection conn, String tableName) {
    return pstmtQuery(conn, "SELECT \n" +
            "    kcu.column_name\n" +
            "FROM information_schema.table_constraints tc\n" +
            "JOIN information_schema.key_column_usage kcu\n" +
            "    ON tc.constraint_name = kcu.constraint_name\n" +
            "    AND tc.table_schema = kcu.table_schema\n" +
            "    AND tc.table_name = kcu.table_name\n" +
            "WHERE \n" +
            "    tc.constraint_type = 'PRIMARY KEY'\n" +
            "    AND tc.table_schema = '" + schemaName + "'\n" +
            "    AND tc.table_name = '" + tableName + "';"
        , new Object[]{}
        , json -> json.getString("column_name"));
  }

  @Override
  public String getTableComment(String tableName) {
    String schemaName = getSchemaName(); // 默认模式是 public
    List<JSONObject> qr = pstmtQuery(getConnection(), "SELECT obj_description(?::regclass, 'pg_class') AS value", schemaName + "." + tableName);
    return !qr.isEmpty() ? qr.get(0).getString("value") : null;
  }

  @Override
  public JSONObject getColumnComments(String tableName, String... columns) {
    JSONObject queryResult = new JSONObject(columns.length);
    String schemaName = getSchemaName(); // 默认模式是 public
    // 获取表的列信息
    pstmtQuery(getConnection()
        , "SELECT column_name AS name, col_description('" + tableName + "'::regclass, ordinal_position) AS comment" +
            "\nFROM information_schema.columns" +
            "\nWHERE 1=1" +
            "\n\tAND table_schema = ?" +
            "\n\tAND table_name = ?" +
            (columns.length > 0 ? "\n\tAND column_name IN('" + String.join("', '", columns) + "')" : "")
        , new Object[]{schemaName, tableName})
        .forEach(record -> queryResult.put(record.getString("name"), record.getString("comment")));
    return queryResult;
  }

  @Override
  public List<IndexDefine> getIndexes(String tableName) {
    return pstmtQuery(getConnection(), "SELECT * FROM pg_indexes WHERE tablename = '" + tableName + "' AND indexname != '" + tableName + "_pkey';"
        , new Object[]{}
        , json -> json.toJavaObject(IndexDefine.class));
  }

  /**
   * 获取表的类型
   *
   * @param tableNames 表名
   * @return 返回表类型
   */
  public Map<String, TableType> getTableType(String... tableNames) {
    String sql = "SELECT \n" +
        "    relname AS table_name,\n" +
        "    CASE relkind\n" +
        "        WHEN 'r' THEN 'Table'\n" +
        "        WHEN 'v' THEN 'View'\n" +
        "        WHEN 'm' THEN 'Materialized View'\n" +
        "        ELSE 'Other'\n" +
        "    END AS type\n" +
        "FROM pg_class\n" +
        "WHERE relname IN('" + String.join("', '", tableNames) + "');";
    Map<String, TableType> map = new LinkedHashMap<>();
    pstmtQuery(getConnection(), sql).forEach(json -> {
      switch (json.getString("type")) {
        case "View":
          map.put(json.getString("table_name"), TableType.VIEW);
          break;
        case "Materialized View":
          map.put(json.getString("table_name"), TableType.MATERIALIZED_VIEW);
          break;
        case "Table":
        default:
          map.put(json.getString("table_name"), TableType.BASE_TABLE);
          break;
      }
    });
    return map;
  }


  public int createTable(TableDefine table) {
    SQLGenerator sqlGenerator = SQLGenerator.get();
    String tableSQL = sqlGenerator.createTable(table.getTableName(), table.getColumns());
    String tableCommentSQL = sqlGenerator.tableComment(table.getTableName(), table.getTableComment());
    String columnCommentSQL = sqlGenerator.columnsComment(table.getTableName(), table.getColumns());
    String sql = Stream.of(tableSQL, tableCommentSQL, columnCommentSQL)
        .map(String::trim)
        .filter(StringUtils::isNotBlank)
        .map(str -> str.endsWith(";") ? str : str + ";")
        .collect(Collectors.joining("\n"));
    log.info("------------------------> 创建表: \n{}", sql);
    return stmtUpdate(getConnection(), true, stmt -> {
      int row = 0;
      row += stmt.executeUpdate(tableSQL);
      row += StringUtils.isNotBlank(tableCommentSQL) ? stmt.executeUpdate(tableCommentSQL) : 0;
      row += stmt.executeUpdate(columnCommentSQL);
      return row;
    });
  }

}
