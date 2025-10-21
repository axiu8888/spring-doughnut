package com.benefitj.dataplatform.pg;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class FieldDescriptor {
  String clazz;
  String name;
  String note;
  String type;
}
