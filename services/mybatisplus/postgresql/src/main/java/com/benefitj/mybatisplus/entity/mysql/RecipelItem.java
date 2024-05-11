//package com.benefitj.mybatisplus.entity.mysql;
//
//
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static com.benefitj.mybatisplus.entity.mysql.RecipelType.*;
//
//
///**
// * 处方 item
// */
//public enum RecipelItem {
//  walk(aerobics, "步行"),
//  hurry(aerobics, "快走"),
//  jog(aerobics, "慢跑"),
//  powerCar(aerobics, "功率车"),
//  rehabTrainingVehicle(aerobics, "智能电动训练车"),
//  crossEerciseTrainer(aerobics, "上下肢交叉运动训练器"),
//  treadmill(aerobics, "跑步机"),
//  ellipticalMachine(aerobics, "椭圆机"),
//  rowingMachine(aerobics, "划船机"),
//  verticalPowerCar(aerobics, "直立式功率车"),
//  recliningPowerCar(aerobics, "卧式功率车"),
//  activePassiveTraining(aerobics, "上下肢主被动训练车"),
//  bedActivePassiveTraining(aerobics, "床上下肢主被动训练车"),
//  upperLimb(resistance, "上肢力量训练"),
//  lowerLimb(resistance, "下肢力量训练"),
//  elasticBand(resistance, "弹力带"),
//  dumbbell(resistance, "哑铃"),
//  kettlebell(resistance, "壶铃"),
//  sittingPushRowing(resistance, "坐姿划船器"),//Flat push rowing trainer
//  adjustableKickMachine(resistance, "调节式蹬腿机"),//Flat push rowing trainer
//  twoWayChestThruster(resistance, "双向推胸器"),//Flat push rowing trainer
//  abdominalMuscleTrainer(resistance, "腹肌训练器"),//Flat push rowing trainer
//  dorsalMuscleTrainer(resistance, "背肌训练器"),//Flat push rowing trainer
//  legExtensionTrainer(resistance, "大腿伸展训练器"),//Flat push rowing trainer
//
//  legAddAbd(resistance, "腿部内收外展训练器"),   //Leg adduction and abduction trainer
//  shoulderUpDown(resistance, "肩部上推下拉训练器"), //Shoulder up and down trainer
//  flatPushRowing(resistance, "平推划船训练器"),  //Flat push rowing trainer
//  legFlexionExtension(resistance, "腿部屈伸训练器"),  //Leg flexion and extension trainer
//  chestClampExpand(resistance, "夹胸扩胸训练器"),  //Chest clamping and expanding trainer
//  backFlexExtens(resistance, "腰背屈伸训练器"),      //Low back flexion and extension trainer
//  waistBackRotation(resistance, "腰背旋转训练器"),  //Waist back rotation trainer
//
//  baDuanJin(respExercise, "八段锦"),
//  sixFormula(respExercise, "六字诀"),
//  lsRespExercise(respExercise, "李氏呼吸操"),
//  respRebuild(respTrain, "呼吸模式重建"),
//  selfMonitoring(custody, "身心评估"), //自我监测 改成 身心评估
//  sleepStageAhi(custody, "睡眠监测"), //睡眠，此版本只能这样硬添加
//  selfScreen(custody, "自我监测"),
//  smwt(prSix, "六分钟步行"),
//  smst(prSix, "六分钟踏阶"),
//  cat(question, "CAT"),            //慢性阻塞性肺疾病评估测试(CAT)
//  sgrq(question, "SGRQ"),          //圣.乔治医院呼吸问题调查问卷(SGRQ)
//  sds(question, "SDS"),            //抑郁自评量表(SDS)
//  sas(question, "SAS"),            //焦虑自评量表(SAS)
//  psqi(question, "PSQI"),          //匹兹堡睡眠质量指数量表(PSQI)
//  mna(question, "MNA"),            //微型营养评价(MNA)
//  adl(question, "ADL"),            //日常生活能力评定量表（ADL）
//  fim(question, "FIM"),            //功能独立性评定（FIM）量表
//  gad(question, "GAD"),            //焦虑症筛查量表（GAD-7)
//  phq(question, "PHQ"),            //抑郁症筛查量表（PHQ-9）
//  mmrc(question, "mMRC"),          //mMRC问卷
//  nrs(question, "NRS"),      //
//  hamd(question, "HAMD"),         //汉密尔顿抑郁量表（HAMD）
//  hama(question, "HAMA"),         //汉密尔顿焦虑量表（HAMA）
//  gds(question, "GDS"),           //老年抑郁量表（GDS）
//  hads(question, "HADS"),         //医院焦虑抑郁情绪测量表（HADS）
//  act(question, "ACT"),           //哮喘控制测试评分表（ACT）
//  hds(question, "MRC"),           //暂不添加
//  sf36(question, "SF-36"),         //暂不添加
//  ftnd(question, "FTND"),         //尼古丁依赖检验量表
//  e5q5d5l(question, "E5Q-5D-5L"), //欧洲五维健康量表
//  rsps(question, "NRS-PS"),      //NRS数字疼痛评分量表(pain score)
//
//  medication(prescription, "用药处方"),
//  mindfulness(psychological, "正念冥想"),
//  mindrelax(psychological, "放松冥想"),
//  mindsleep(psychological, "催眠冥想"),
//  entrustment(entrust, "嘱托医嘱"),
//  nutrition(diet, "营养膳食"),
//  smoke(smokeCessation, "戒烟"),
//  healthEdu(healthGuide, "健康教育"),
//  BmU(examination, "B型超声"),
//  CT(examination, "CT"),
//  UCG(examination, "超声心动图"),
//  NMR(examination, "核磁共振"),
//  ECG(examination, "心电图"),
//  PFT(examination, "肺功能"),
//  GLU(examination, "空腹血糖"),
//  Blood(examination, "血常规"),
//  biochemical(examination, "生化"),
//  LFT(examination, "肝功能"),
//  renalFunction(examination, "肾功能"),
//  Clotting4(examination, "凝血四项"),
//  TM(examination, "肿瘤标志物"),
//  ;
//
//  private RecipelType type;
//  private String name;
//
//  RecipelItem(RecipelType type, String name) {
//    this.type = type;
//    this.name = name;
//  }
//
//  public RecipelType getType() {
//    return type;
//  }
//
//  public void setType(RecipelType type) {
//    this.type = type;
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
//  public ReportType getReportType() {
//    if (this.getType() == question) {
//      return ReportType.valueOf(this.toString().toLowerCase());
//    } else {
//      return ReportType.valueOf(this.toString());
//    }
//  }
//
//  public List<RecipelItem> getTypeItems(RecipelType type) {
//    return Stream.of(RecipelItem.values())
//        .filter(ri -> ri.type == type)
//        .collect(Collectors.toList());
//  }
//
//}
