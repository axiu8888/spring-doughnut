package com.benefitj.mybatisplus.entity.mysql;

import com.benefitj.mybatisplus.entity.mysql.base.MysqlBase;
import com.benefitj.mybatisplus.entity.postgresql.ReportStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuperBuilder
@NoArgsConstructor
@Data
@javax.persistence.Table(name = "HS_REPORT_TASK")
public class HsReportTaskEntity extends MysqlBase implements Serializable {
  @Id
  String zid;              //唯一标识，与在mongodb中报告的id一致，由报告类型查询对应的表

  String parentZid;       //首次创建报告时，与zid一致，重新产生报告时，添加新记录为初始目标报告的zid。简单讲就是存储每次失败的记录将呗存储
  String type;    //报告类型
  String pdfPath;         //报告产生路径
  ReportStatus status;//报告状态
  String statusDesc;      //报告状态描述,当报告产生失败时，存储失败原因
  String taskZid;         //关联任务表，如果为空时，为手动生成报告。
  Date startTime;         //开始时间
  Date endTime;           //结束
  String personZid;       //患者标识
  String deviceId;        //患者当前绑定的设备标识，算法不一定使用，暂时留下
  String orgZid;          //组织唯一标识,当前是哪一个组织下的医嘱，如果是手动生成的报告，则需要将手动执行报告人的组织标识记录
  String taskSource;     //报告任务源

  @Builder.Default
  @Column(name = "times")
  Integer times = 0;          //执行次数，任务在执行时可能会失败，或者超时等情况，在意外情况发生之后任务进入重试状态，最多执行5次

  String reportDate;                    //报告日期，由结束时间确定其报告日期
  Float duration;          //数据时长(分钟)
  String version; //报告版本
  String extend;  //扩展模块，该字段来源于触发器，获取HS_CUSTOM_REPORT_TASK表的extend权限,该字段以逗号隔开，提供前台展示权限

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
    if (endTime == null) return;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    this.reportDate = format.format(endTime);
  }
}
