package com.benefitj.examples.vo;

import com.benefitj.spring.eventbus.EventName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EventName("id")
public class IdEvent {

  private String id;

}
