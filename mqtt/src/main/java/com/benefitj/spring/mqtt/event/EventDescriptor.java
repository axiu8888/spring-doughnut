package com.benefitj.spring.mqtt.event;

import com.benefitj.core.ReflectUtils;
import com.benefitj.mqtt.MqttTopic;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class EventDescriptor {

  private final Class<?> type;

  private final Map<String, List<Splicer>> splicers = new LinkedHashMap<>();

  private final List<MqttTopic> topics = new LinkedList<>();

  private EventTopic eventTopic;
  /**
   * 转换器
   */
  private EventConverter converter;

  /**
   * 获取发送的主题
   *
   * @param msg 消息
   * @return 返回主题
   */
  public List<String> getTopics(Object msg) {
    return getSplicers()
        .values()
        .stream()
        .map(splicers -> splicers.stream()
            .map(splicer -> getSplicerValue(splicer, msg))
            .collect(Collectors.joining("")))
        .collect(Collectors.toList());
  }

  /**
   * 匹配是否符合Topic
   *
   * @param topic 主题
   * @return 返回匹配结果
   */
  public boolean match(String topic) {
    return getTopics().stream().anyMatch(t -> t.match(topic));
  }

  public String getSplicerValue(Splicer splicer, Object msg) {
    if (splicer.isPlaceholder()) {
      Object value = ReflectUtils.getFieldValue(splicer.getField(), msg);
      if (value == null) {
        throw new IllegalStateException(splicer.getName() + " 不能为NULL");
      }
      return String.valueOf(value);
    }
    return splicer.getSegment();
  }

  public byte[] convert(Object event) {
    return getConverter().convert(this, event);
  }

}
