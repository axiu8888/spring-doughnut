package com.benefitj.spring.freemarker;

public enum ModiferType {

  /**
   * private
   */
  PRIVATE("private"),
  /**
   * protected
   */
  PROTECTED("protected"),
  /**
   * default
   */
  DEFAULT(""),
  /**
   * public
   */
  PUBLIC("public"),
  ;

  final String name;

  ModiferType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return getName();
  }

}
