package com.benefitj.mybatisplus.entity.mysql;

import com.alibaba.fastjson2.JSONObject;

import static com.benefitj.mybatisplus.entity.mysql.Algorithm.allReport;
import static com.benefitj.mybatisplus.entity.mysql.Algorithm.prReport;

/**
 * 报告类型
 * <p>
 * 呼吸操和呼吸模式训练（重建都调用呼吸操算法）
 */
public enum ReportType {

  @Deprecated
  all("所有报告"),                           //所有报告，不做为分类使用。
  //******监护******************************************
  jsyl("精神压力", allReport),                         //精神压力
  mse("整体健康度", allReport),                          //整体健康度
  //@Deprecated
  //sleep("睡眠"),                        //睡眠
  activity("活动量", allReport),                     //活动量
  hrv("心率变异性", allReport),                          //心率变异性
  physical("身心并护", allReport),                     //身心病护
  mmhg("动态血压"),                         //动态血压 TODO 没有mongodb数据如何处理
  holter("动态心电"),                       //动态心电 TODO 没有mongodb数据如何处理
  //holterAuto("动态心电",allReport),
  /**
   * 已废弃，对应到新兴阳升的床垫(已不再使用)：建议使用 {@link #sleepMattress}
   */
  @Deprecated
  mattress("睡眠床垫", allReport),              //睡眠床垫 TODO 没有mongodb数据如何处理
  sleepMattress("睡眠床垫", allReport),        //睡眠床垫(大耳马)
  sleepStageAhi("睡眠监测", allReport),        //睡眠监测
  sleepAhi("睡眠过筛", allReport),            //睡眠过筛
  //******肺康复******************************************
  smst("6分钟踏阶", prReport),                   //6分钟踏阶
  smwt("6分钟步行", prReport),                   //6分钟步行


  respTrainNormal("呼吸训练普通模式", prReport),                //呼吸训练普通模式
  respTrainExpert("呼吸训练专家模式", prReport),                 //呼吸训练专家模式

  sportRecory("运动康复", prReport),                //呼吸训练
  walk("步行", sportRecory, prReport),
  hurry("快走", sportRecory, prReport),
  jog("慢跑", sportRecory, prReport),
  powerCar("功率车", sportRecory, prReport),
  rehabTrainingVehicle("上下肢康复训练车", sportRecory, prReport),
  crossEerciseTrainer("上下肢交叉运动训练器", sportRecory, prReport),
  treadmill("跑步机", sportRecory, prReport),
  ellipticalMachine("椭圆机", sportRecory, prReport),
  rowingMachine("划船机", sportRecory, prReport),
  verticalPowerCar("直立式功率车", sportRecory, prReport),
  recliningPowerCar("卧式功率车", sportRecory, prReport),
  activePassiveTraining("上下肢主被动训练车", sportRecory, prReport),
  bedActivePassiveTraining("床上下肢主被动训练车", sportRecory, prReport),

  upperLimb("上肢力量训练", sportRecory, prReport),
  lowerLimb("下肢力量训练", sportRecory, prReport),
  elasticBand("弹力带", sportRecory, prReport),
  dumbbell("哑铃", sportRecory, prReport),
  kettlebell("壶铃", sportRecory, prReport),
  sittingPushRowing("坐姿划船器", sportRecory),
  adjustableKickMachine("调节式蹬腿机", sportRecory),
  twoWayChestThruster("双向推胸器", sportRecory),
  abdominalMuscleTrainer("腹肌训练器", sportRecory),
  dorsalMuscleTrainer("背肌训练器", sportRecory),
  legExtensionTrainer("大腿伸展训练器", sportRecory),

  legAddAbd("腿部内收外展训练器", sportRecory, prReport),
  shoulderUpDown("肩部上推下拉训练器", sportRecory, prReport),
  flatPushRowing("平推划船训练器", sportRecory, prReport),
  legFlexionExtension("腿部屈伸训练器", sportRecory, prReport),
  chestClampExpand("夹胸扩胸训练器", sportRecory, prReport),
  backFlexExtens("腰背屈伸训练器", sportRecory, prReport),
  waistBackRotation("腰背旋转训练器", sportRecory, prReport),

  lsRespExercise("李氏呼吸操", sportRecory, prReport),
  respExercise("呼吸操", sportRecory, prReport),              //呼吸操
  baDuanJin("八段锦", sportRecory, prReport),
  sixFormula("六字诀", sportRecory, prReport),
  respRebuild("呼吸模式优化", prReport),
  mentalRecory("身心健康监测", prReport), //精神心理

  selfMonitoring("自我监测"), //虚拟类型，用于产生任务时调用监护所有报告任务
  //********问卷*************************************************
  cat("慢性阻塞性肺部疾病（COPD）评估测试问卷"),
  sgrq("圣乔治医院呼吸问题调查问卷"),
  sds("抑郁自评量表"),
  sas("焦虑自评量表"),
  psqi("匹兹堡睡眠质量指数"),
  mna("微型营养评价"),

  adl("日常生活能力评定量表"),
  fim("FIM功能独立性评定量表"),
  gad("焦虑症筛查量表"),
  phq("抑郁症筛查量表"),
  mmrc("mMRC问卷"),
  nrs("数字疼痛评分量表"),
  hamd("汉密尔顿抑郁量表（HAMD）"),
  hama("汉密尔顿焦虑量表（HAMA）"),
  gds("老年抑郁量表（GDS）"),
  hads("医院焦虑抑郁情绪测量表（HADS）"),
  act("哮喘控制测试评分表（ACT）"),
  hds("MRC"),
  sf36("SF-36"),
  fps("FPS-R"),
  ftnd("尼古丁依赖检验量表"),
  e5q5d5l("欧洲五维健康量表"),
  sss("SSS躯体化症状量表"),

  medication("用药处方"),
  entrustment("嘱托医嘱"),
  nutrition("营养膳食"),
  selfScreen("健康监测", prReport),
  mindrelax("放松冥想"),
  mindsleep("催眠冥想"),
  mindfulness("正念冥想");

  private String reportName;
  private ReportType type;
  //是否调用算法，默认调用，对于问卷类不调用
  private Algorithm algo;

  ReportType(String reportName) {
    this.reportName = reportName;
  }

  ReportType(String reportName, ReportType type) {
    this.reportName = reportName;
    this.type = type;
  }

  ReportType(String reportName, Algorithm algo) {
    this.reportName = reportName;
    this.algo = algo;
  }

  ReportType(String reportName, ReportType type, Algorithm algo) {
    this.reportName = reportName;
    this.type = type;
    this.algo = algo;
  }

  public String getReportName() {
    return reportName;
  }

  public void setReportName(String reportName) {
    this.reportName = reportName;
  }

  public ReportType getType() {
    return type == null ? this : type;
  }

  public void setType(ReportType type) {
    this.type = type;
  }

  public Algorithm getAlgo() {
    if (algo == null) return Algorithm.ques;
    return algo;
  }

  public void setAlgo(Algorithm algo) {
    this.algo = algo;
  }

  public static JSONObject toJson() {
    JSONObject json = new JSONObject();
    for (ReportType type : ReportType.values()) {
      if (type == ReportType.all) continue;
      json.put(type.toString(), type.getReportName());
    }
    return json;
  }
}
