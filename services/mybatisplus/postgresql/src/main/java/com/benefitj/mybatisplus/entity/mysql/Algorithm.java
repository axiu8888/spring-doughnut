package com.benefitj.mybatisplus.entity.mysql;

public enum Algorithm {
  ques,       //问卷，不调用算法
  prReport("/calculatePRReport"),   //肺康复报告
  allReport("/calculate_all_report");   //监护所有报告

  public final String uri;

  Algorithm() {
    this(null);
  }

  Algorithm(String uri) {
    this.uri = uri;
  }
}