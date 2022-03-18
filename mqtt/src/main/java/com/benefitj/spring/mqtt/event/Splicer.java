package com.benefitj.spring.mqtt.event;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Splicer {
  /**
   * 片段
   */
  private String segment;
  /**
   * 片段开始的位置
   */
  private int start;
  /**
   * 片段结束的位置
   */
  private int end;
  /**
   * 占位符名称
   */
  private String name;
  /**
   * 占位符
   */
  private boolean placeholder;
  /**
   * 字段
   */
  @JSONField(serialize = false, deserialize = false)
  @JsonIgnore
  private Field field;
  /**
   * 占位符标记
   */
  @JSONField(serialize = false, deserialize = false)
  @JsonIgnore
  private TopicPlaceholder topicPlaceholder;

  public String getActualPlaceholderName() {
    return getTopicPlaceholder() != null ? getTopicPlaceholder().name() : getName();
  }

}
