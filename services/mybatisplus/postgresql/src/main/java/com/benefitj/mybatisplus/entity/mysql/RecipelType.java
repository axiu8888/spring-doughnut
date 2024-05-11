//package com.benefitj.mybatisplus.entity.mysql;
//
//import com.alibaba.fastjson2.JSONObject;
//
///**
// * 类型类型
// */
//public enum RecipelType {
//
//  aerobics("有氧运动"),
//  resistance("抗阻运动"),
//  respExercise("呼吸操"),
//  respTrain("呼吸训练"),
//  question("问卷评估"),
//  custody("监测筛查"),
//  prSix("六分钟"),
//  //无报告分类
//  psychological("心理处方"),
//  diet("营养膳食"),
//  entrust("嘱托医嘱"),
//  prescription("用药处方"),
//  smokeCessation("戒烟"),
//  examination("检测项目"),
//  healthGuide("健康指导"),
//  remind("消息"),
//
//  ;
//
//  private String name;
//
//  RecipelType(String name) {
//    this.name = name;
//  }
//
//  public String getName() {
//    return name;
//  }
//
//  public void setName(String name) {
//    this.name = name;
//  }
//
//  public static JSONObject toJson() {
//    JSONObject json = new JSONObject();
//    for (RecipelType type : RecipelType.values()) {
//      json.put(type.toString(), type.getName());
//    }
//    return json;
//  }
//}
