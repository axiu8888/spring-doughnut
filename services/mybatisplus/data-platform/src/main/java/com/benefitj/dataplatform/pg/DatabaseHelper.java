package com.benefitj.dataplatform.pg;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.functions.IFunction;
import com.benefitj.dataplatform.pg.dto.IndexDefine;
import com.benefitj.dataplatform.pg.dto.TableDefine;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * 数据库操作
 */
public interface DatabaseHelper {

  /**
   * 数据库连接
   */
  Connection getConnection();

  /**
   * 创建 Statement
   */
  default Statement createStatement(Connection conn) {
    try {
      return conn.createStatement();
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  /**
   * 创建 PreparedStatement
   */
  default PreparedStatement prepareStatement(Connection conn, String sql) {
    try {
      return conn.prepareStatement(sql);
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  /**
   * 执行查询
   *
   * @param conn 连接
   * @param sql  查询的SQL
   * @return 返回查询结果
   */
  default List<JSONObject> stmtQuery(Connection conn, String sql) {
    return stmtQuery(conn, sql, record -> record);
  }

  /**
   * 执行查询
   *
   * @param conn      连接
   * @param sql       查询的SQL
   * @param mappedFun 查询结果转换
   * @param <T>       返回的对象
   * @return 返回查询结果
   */
  default <T> List<T> stmtQuery(Connection conn, String sql, Function<JSONObject, T> mappedFun) {
    try (final Statement stmt = createStatement(conn);
         final ResultSet rs = stmt.executeQuery(sql);) {
      return doRead(rs, mappedFun);
    } catch (SQLException e) {
      throw new SqlException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 执行查询
   *
   * @param conn 连接
   * @param sql  查询语句
   * @param args 参数
   * @return 返回查询结果
   */
  default List<JSONObject> pstmtQuery(Connection conn, String sql, Object... args) {
    try (PreparedStatement ps = prepareStatement(conn, sql)) {
      for (int i = 0; i < args.length; i++) {
        ps.setObject(i + 1, args[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        return doRead(rs);
      }
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  /**
   * 执行查询
   *
   * @param conn      连接
   * @param sql       查询语句
   * @param args      参数
   * @param mappedFun 查询结果转换
   * @return 返回查询结果
   */
  default <T> List<T> pstmtQuery(Connection conn, String sql, Object[] args, Function<JSONObject, T> mappedFun) {
    try (PreparedStatement ps = prepareStatement(conn, sql)) {
      for (int i = 0; i < args.length; i++) {
        ps.setObject(i + 1, args[i]);
      }
      try (ResultSet rs = ps.executeQuery()) {
        List<T> list = new LinkedList<>();
        doRead(rs, record -> {//单条记录
          list.add(mappedFun.apply(record));
        });
        return list;
      }
    } catch (SQLException e) {
      throw new SqlException(e);
    }
  }

  /**
   * 读取结果集
   *
   * @param rs 查询结果
   * @return 返回读取的集合
   */
  default List<JSONObject> doRead(ResultSet rs) throws SQLException {
    return doRead(rs, record -> record);
  }

  /**
   * 读取结果集
   *
   * @param rs        查询结果
   * @param mappedFun 转换器
   */
  default <T> List<T> doRead(ResultSet rs, Function<JSONObject, T> mappedFun) throws SQLException {
    List<T> list = new LinkedList<>();
    doRead(rs, record -> {
      list.add(mappedFun.apply(record));
    });
    return list;
  }

  /**
   * 读取结果集
   *
   * @param rs       查询结果
   * @param consumer 转换器
   */
  default void doRead(ResultSet rs, Consumer<JSONObject> consumer) throws SQLException {
    try (rs) {
      ResultSetMetaData metaData = rs.getMetaData();
      while (rs.next()) {
        JSONObject record = new JSONObject();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
          record.put(metaData.getColumnName(i), rs.getObject(i));
        }
        consumer.accept(record);
      }
    }
  }

  /**
   * 执行更新
   *
   * @param conn 连接
   * @param sql  SQL
   * @return 返回
   */
  default int stmtUpdate(Connection conn, String sql) {
    return stmtUpdate(conn, false, sql);
  }

  /**
   * 执行更新
   *
   * @param conn          连接
   * @param transactional 是否开启事务
   * @param sql           SQL
   * @return 返回
   */
  default int stmtUpdate(Connection conn, boolean transactional, String sql) {
    return stmtUpdate(conn, transactional, stmt -> stmt.executeUpdate(sql));
  }

  /**
   * 执行更新
   *
   * @param conn          连接
   * @param transactional 是否开启事务
   * @param executor      执行
   * @return 返回
   */
  default int stmtUpdate(Connection conn, boolean transactional, IFunction<Statement, Integer> executor) {
    try {
      if (transactional) {
        conn.setAutoCommit(false);
      }
      try (final Statement stmt = createStatement(conn)) {
        int row = executor.apply(stmt);
        if (transactional) {
          conn.commit();//提交事务
        }
        return row;
      } catch (Exception e) {
        if (transactional) {
          conn.rollback();//回滚事务
        }
        throw new SqlException(CatchUtils.findRoot(e));
      } finally {
        if (transactional) {
          conn.setAutoCommit(true);
        }
      }
    } catch (SQLException e) {
      throw new SqlException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 判断数据库是否存在
   */
  boolean existDatabase(String dbName);

  /**
   * 创建数据库
   */
  int createDatabase(String dbName);

  /**
   * 获取数据库名称
   */
  List<String> getDatabases();

  /**
   * 是否存在表
   */
  int existTable(String tableName);

  /**
   * 获取表名和字段
   */
  default List<TableDefine> getTables() {
    return getTables(false);
  }

  /**
   * 获取表名和字段
   *
   * @param containsView 是否包含View
   */
  List<TableDefine> getTables(boolean containsView);

  /**
   * 获取主键
   *
   * @param tableName 表名
   * @return 返回主键
   */
  List<String> getPrimaryKeys(String tableName);

  /**
   * 获取表的注释
   */
  String getTableComment(String tableName);

  /**
   * 获取表字段的注释  {"字段名": "注释内容"}
   *
   * @param tableName 表名
   * @param columns   字段名
   * @return 返回查询到的字段注释
   */
  JSONObject getColumnComments(String tableName, String... columns);

  /**
   * 获取表的索引信息
   *
   * @param tableName 表名
   * @return 返回索引信息
   */
  List<IndexDefine> getIndexes(String tableName);

}
