package com.benefitj.system.utils;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.benefitj.core.ClasspathUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.core.TryCatchUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 将实体类对应的表导出到excel
 */
public class TableExcelUtils {

  @SuperBuilder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class TableDescriptor {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 索引
     */
    private List<IndexDescriptor> indexs;
    /**
     * 字段
     */
    private List<ColumnDescriptor> columns;
  }

  @SuperBuilder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class IndexDescriptor {

    @ExcelProperty("索引名")
    private String name;

    @ExcelProperty("索引列")
    private String columns;

  }

  @SuperBuilder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class ColumnDescriptor {

    @ExcelProperty("字段")
    private String name;

    @ExcelProperty("类型")
    private String type;

    @ExcelProperty("长度")
    private Integer length;

    @ExcelProperty("描述")
    private String comment;

    @ExcelProperty("是否为空")
    private String nullable;

    @ExcelProperty("是否唯一")
    private String unique;

    @ExcelProperty("默认值")
    private String defaultValue;

    @ExcelProperty("精度(小数点后位数)")
    private String precision;

  }

  /**
   * 解析字段
   */
  private static ColumnDescriptor parseColumn(Column column) {
    String columnDefinition = column.columnDefinition().trim();
    String type = columnDefinition.trim().split(" ")[0];
    String comment = columnDefinition.trim().split(" comment ")[1];
    String[] defaultSplit = columnDefinition.trim().split(" DEFAULT ");
    String defaultValue = defaultSplit.length > 1 ? defaultSplit[1].split(" comment ")[0] : "";
    return ColumnDescriptor.builder()
        .name(column.name())
        .type(type)
        .length(type.endsWith(")") ? Integer.valueOf(type.substring(type.indexOf("(") + 1, type.lastIndexOf(")"))) : null)
        .defaultValue(StringUtils.isNotBlank(defaultValue) ? "DEFAULT " + defaultValue : "")
        .comment(comment.replace("'", ""))
        .nullable(column.nullable() ? "" : "否")
        .unique(column.unique() ? "是" : "")
        .precision(column.precision() == 0 ? "" : String.valueOf(column.precision()))
        .build();
  }

  /**
   * 解析索引
   */
  private static List<IndexDescriptor> parseIndex(Table table) {
    Index[] indexes = table.indexes();
    return indexes.length <= 0 ? Collections.emptyList() : Stream.of(indexes)
        .map(index -> IndexDescriptor.builder()
            .name(index.name())
            .columns(index.columnList())
            .build())
        .collect(Collectors.toList());
  }

  /**
   * 解析
   *
   * @param classes 类
   * @return 返回解析后的表信息
   */
  public static List<TableDescriptor> parse(List<Class<?>> classes) {
    return classes
        .stream()
        .filter(klass -> klass.isAnnotationPresent(Table.class))
        .map(cls -> TableDescriptor.builder()
            .tableName(cls.getAnnotation(Table.class).name())
            .indexs(parseIndex(cls.getAnnotation(Table.class)))
            .columns(ReflectUtils.getFields(cls
                , f -> f.isAnnotationPresent(Column.class)
                , f -> parseColumn(f.getAnnotation(Column.class))))
            .build())
        .collect(Collectors.toList());
  }

  /**
   * 解析
   *
   * @param basePackages 包名
   * @return 返回解析后的表信息
   */
  public static List<TableDescriptor> parse(String... basePackages) {
    List<Class<?>> classes = Stream.of(basePackages)
        .flatMap(basePackage -> ClasspathUtils.findClasses(basePackage).stream())
        .map(cls -> TryCatchUtils.tryThrow(() -> Class.forName(cls)))
        .collect(Collectors.toList());
    return parse(classes);
  }

  /**
   * 导出Excel
   *
   * @param xlsx   excel文件
   * @param tables 数据表
   * @return 返回写入的文件
   */
  public static File export(File xlsx, List<TableDescriptor> tables) {
    final ExcelWriter writer = EasyExcel.write(xlsx).build();
    try {
      final AtomicInteger index = new AtomicInteger(1);
      tables.forEach(tableDescriptor ->
          writer.write(tableDescriptor.getColumns(), EasyExcel.writerSheet(index.getAndIncrement(), tableDescriptor.getTableName())
              .head(ColumnDescriptor.class)
              .build())
      );
    } finally {
      writer.finish();
    }
    return xlsx;
  }

  /**
   * 导出Excel
   *
   * @param xlsx         excel文件
   * @param basePackages 包名
   * @return 返回写入的文件
   */
  public static File export(File xlsx, String... basePackages) {
    return export(xlsx, parse(basePackages));
  }

}
