package com.benefitj.pdfcreator.pdf;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class Margins {
  /**
   * Can be `default`, `none`, `printableArea`, or `custom`. If `custom` is chosen,
   * you will also need to specify `top`, `bottom`, `left`, and `right`.
   */
  @Builder.Default
  String marginType = "default";
  /**
   * The top margin of the printed web page, in pixels.
   */
  float top;
  /**
   * The bottom margin of the printed web page, in pixels.
   */
  float bottom;
  /**
   * The left margin of the printed web page, in pixels.
   */
  float left;
  /**
   * The right margin of the printed web page, in pixels.
   */
  float right;
}
