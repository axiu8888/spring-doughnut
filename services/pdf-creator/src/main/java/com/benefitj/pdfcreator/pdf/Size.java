package com.benefitj.pdfcreator.pdf;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class Size {

  // Docs: https://electronjs.org/docs/api/structures/size

  float height;
  float width;
}
