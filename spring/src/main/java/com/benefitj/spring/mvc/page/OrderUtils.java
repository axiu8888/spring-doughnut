package com.benefitj.spring.mvc.page;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 排序
 */
public class OrderUtils {

  public static final String SEPARATOR = " ";
  public static final String COMMA = ",";

  /**
   * 转换为排序字段
   *
   * @param order     排序字段和方向
   * @param direction 默认方向
   * @return 返回排序字段
   */
  @Nullable
  public static OrderField convert(String order, @Nullable OrderDirection direction) {
    if (StringUtils.isNotBlank(order)) {
      order = order.trim();
      if (order.contains(SEPARATOR)) {
        String[] split = order.split(SEPARATOR);
        if (split.length >= 2) {
          return new OrderField(split[0].trim(), split[split.length - 1].trim());
        }
        return new OrderField(split[0].trim(), direction);
      }
      return new OrderField(order, direction);
    }
    return null;
  }

  /**
   * 转换为排序字段
   *
   * @param orderByList 排序字段
   * @return 返回排序字段列表
   */
  @Nonnull
  public static List<OrderField> convertArray(List<String[]> orderByList) {
    if (orderByList != null && !orderByList.isEmpty()) {
      return orderByList.stream()
          .filter(Objects::nonNull)
          .filter(orders -> orders.length > 0)
          .map(Arrays::asList)
          .flatMap(order -> convert(order).stream())
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  /**
   * 转换为排序字段
   *
   * @param orderByList 排序字段
   * @return 返回排序字段列表
   */
  @Nonnull
  public static List<OrderField> convert(List<String> orderByList) {
    if (orderByList != null && !orderByList.isEmpty()) {
      return orderByList.stream()
          .filter(StringUtils::isNotBlank)
          .flatMap(s -> s.contains(COMMA) ? Arrays.stream(s.split(COMMA)) : Stream.of(s))
          .map(s -> convert(s, null))
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

}
