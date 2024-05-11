package com.benefitj.mybatisplus.entity.postgresql;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.mybatisplus.entity.postgresql.base.HsUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 跟报告有关的任务
 */
@ApiModel("报告任务")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("hs_report_task")
public class ReportTaskEntity extends HsUuidEntity {

  @ApiModelProperty("与id一致，重新产生报告时，添加新记录为初始目标报告的id")
  private String parentId;       //首次创建报告时，与id一致，重新产生报告时，添加新记录为初始目标报告的id。简单讲就是存储每次失败的记录将呗存储

  @ApiModelProperty("患者ID")
  private String personId;
  @ApiModelProperty("患者就诊id")
  private String patientId;

  @ApiModelProperty("患者当前绑定的设备标识，算法不一定使用，暂时留下")
  private String deviceId;

  @ApiModelProperty("关联的机构ID")
  private String orgId;

  @ApiModelProperty("医院标识，该id由org_id向上找到医院、经销商、root任意一个停止")
  private String rootOrgId;

  // TODO: 2022/2/21 字典
  @ApiModelProperty("报告类型")
  private String item;
  @ApiModelProperty("业务类型，来源于businessType中，用于前端加载报告使用")
  private BusinessType type;

  @ApiModelProperty("报告产生路径")
  private String pdfPath;

  // TODO: 2022/2/21 字典
  @ApiModelProperty("报告状态")
  private ReportStatus status;

  @ApiModelProperty("报告状态描述,当报告产生失败时，存储失败原因")
  private String statusDesc;

  @ApiModelProperty("关联任务表，如果为空时，为手动生成报告")
  private String taskId;

  @ApiModelProperty("开始时间：yyyy-MM-dd HH:mm:ss")
  private Date startTime;

  @ApiModelProperty("结束时间：yyyy-MM-dd HH:mm:ss")
  private Date endTime;

  @ApiModelProperty("报告任务源")
  private String taskSource;

  @ApiModelProperty("执行次数，最多执行5次")
  private Integer times;

  @ApiModelProperty("报告日期，由结束时间确定其报告日期")
  private String reportDate;

  @ApiModelProperty("数据时长(分钟)")
  private Float duration;

  @ApiModelProperty("报告版本")
  private String version;
  @ApiModelProperty(value = "处方来源")
  private String source;
  String extend;
  private String stage; //阶段
  private Integer recording; //是否补录 0：正常，默认 1：补录
  private JSONObject data;

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
    if (endTime != null) {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      this.reportDate = format.format(endTime);
    }
  }
}
