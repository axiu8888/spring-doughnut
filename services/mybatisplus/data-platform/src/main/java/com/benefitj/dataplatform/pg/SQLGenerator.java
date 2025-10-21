package com.benefitj.dataplatform.pg;

import com.benefitj.core.Placeholder2;
import com.benefitj.core.SingletonSupplier;
import com.benefitj.dataplatform.pg.dto.ColumnDefine;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;


/**
 * SQL生成
 */
public class SQLGenerator {

  static final SingletonSupplier<SQLGenerator> singleton = SingletonSupplier.of(SQLGenerator::new);

  public static SQLGenerator get() {
    return singleton.get();
  }


  public static class Tablespace {
    public String createTableSpace(String name, String path) {
      return "SELECT * FROM pg_tablespace;";
    }

    public String dropTableSpace(String name) {
      return "DROP TABLESPACE " + name + ";";
    }
  }

  /**
   * 创建数据库
   *
   * @param db 数据库名
   * @param owner 所有者 postgres
   * @param template 模板：postgres、template0、template1
   * @param encoding 编码 UTF-8、GBK
   * @return 返回SQL
   */
  public String createDatabase(String db, String owner, String template, String encoding) {
    return createDatabase(db, owner, template, encoding, "zh_CN.UTF-8", "zh_CN.UTF-8", "pg_default", -1);
  }

  /**
   * 创建数据库
   *
   * @param db 数据库名
   * @param owner 所有者 postgres
   * @param template 模板：postgres、template0、template1
   * @param encoding 编码 UTF-8、GBK
   * @param lcCollate 定义字符串比较规则 zh_CN.UTF-8 、 en_US.UTF-8
   * @param lcCtype 定义字符集和字符分类 zh_CN.UTF-8 、 en_US.UTF-8
   * @param tablespace 表空间：pg_default、pg_global、或者自己创建的表空间
   * @param connectionLimit 连接数量限制，-1表示不限制
   * @return 返回SQL
   */
  public String createDatabase(String db, String owner, String template, String encoding, String lcCollate, String lcCtype, String tablespace, Integer connectionLimit) {
    //"CREATE DATABASE support
    // WITH OWNER = postgres
    // TEMPLATE = postgres
    // ENCODING = 'UTF8'
    // LC_COLLATE = 'zh_CN.UTF-8'
    // LC_CTYPE = 'zh_CN.UTF-8'
    // TABLESPACE = pg_default
    // CONNECTION LIMIT = -1;";
    return Placeholder2.getDefault1().replace("CREATE DATABASE ${db}" +
            "\nWITH" +
            "\n\tOWNER = ${owner}" +
            "\n\tTEMPLATE = ${template}" +
            "\n\tENCODING = '${encoding}'" +
            "\n\tLC_COLLATE = '{lc_collate}'" +
            "\n\tLC_CTYPE = '${lc_ctype}'" +
            "\n\tTABLESPACE = ${tablespace}" +
            //"\n\tALLOW_CONNECTIONS = true" +
            "\n\tCONNECTION LIMIT = ${connectionLimit};"
        , new Placeholder2.PlaceholderResolver() {
          @Nullable
          @Override
          public String resolvePlaceholder(String placeholderName) {
            switch (placeholderName) {
              case "db": return db;
              case "owner": return owner;
              case "template": return template;
              case "encoding": return encoding;
              case "lc_collate": return lcCollate;
              case "lc_ctype": return lcCtype;
              case "tablespace": return tablespace;
              case "connectionLimit": return String.valueOf(connectionLimit);
              default: return "";
            }
          }
        }
    );
  }

  /**
   * 创建表的SQL
   *
   * @param tableName 表名
   * @param columns   列
   * @return 返回SQL
   */
  public String createTable(String tableName, List<ColumnDefine> columns) {
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");
    for (ColumnDefine column : columns) {
      sb
          .append("\t").append(column.getColumnName())
          .append(" ").append(getColumnType(column))
          .append("YES".equals(uppercase(column.getIsNullable())) ? "" : " NOT NULL")
          .append(StringUtils.isNotBlank(column.getColumnDefault()) ? " DEFAULT " + column.getColumnDefault() : "")
          .append(",\n");
    }
    // 添加主键信息
    columns.stream()
        .filter(c -> Boolean.TRUE.equals(c.getPrimaryKey()))
        .map(ColumnDefine::getColumnName)
        .reduce((a, b) -> a + ", " + b)
        .ifPresent(primaryKey -> sb.append("  PRIMARY KEY (").append(primaryKey).append("),\n"));
    // 去掉最后的逗号
    sb.setLength(sb.length() - 2);
    sb.append("\n);");
    return sb.toString();
  }

  /**
   * 表字段注释的SQL
   *
   * @param tableName 表名
   * @param columns   列
   * @return 返回SQL
   */
  public String columnsComment(String tableName, List<ColumnDefine> columns) {
    return columns
        .stream()
        .filter(c -> StringUtils.isNotBlank(c.getColumnComment()))
        .map(c -> "COMMENT ON COLUMN " + tableName + "." + c.getColumnName() + " IS '" + c.getColumnComment() + "';")
        .collect(Collectors.joining("\n"));
  }

  /**
   * 表字段注释的SQL
   *
   * @param tableName 表名
   * @param comment   注释
   * @return 返回SQL
   */
  public String tableComment(String tableName, String comment) {
    if (StringUtils.isBlank(comment)) return "";
    return "COMMENT ON TABLE " + tableName + " IS '" + comment + "';";
  }


  private String getColumnType(ColumnDefine column) {
    String dataType = column.getDataType().toLowerCase();
    switch (dataType) {
      case "character varying":
      case "varchar":
        return "VARCHAR(" + (column.getCharacterMaximumLength() != null ? column.getCharacterMaximumLength() : 255) + ")";
      case "int4":
        return "INTEGER";
      case "float8":
      case "double":
      case "double precision":
        return "DOUBLE PRECISION";
      case "numeric":
      case "decimal":
        return "NUMERIC(" + column.getNumericPrecision() + ", " + column.getNumericScale() + ")";
      case "timestamp without time zone":
        return "TIMESTAMP";
      default:
        return uppercase(dataType);
    }
  }


  private boolean isTrue(Object value) {
    if (value instanceof CharSequence) {
      String v = (String) value;
      return StringUtils.isNotBlank(v) && (v.equalsIgnoreCase("YES") || Boolean.parseBoolean(v));
    }
    return value != null && Boolean.parseBoolean(value.toString());
  }

  private String lowercase(String v) {
    return v != null ? v.toLowerCase() : null;
  }

  private String uppercase(String v) {
    return v != null ? v.toUpperCase() : null;
  }

}
