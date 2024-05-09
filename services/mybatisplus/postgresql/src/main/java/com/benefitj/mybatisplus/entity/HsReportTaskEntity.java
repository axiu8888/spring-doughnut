package com.benefitj.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(name = "hs_report_task")
@TableName("hs_report_task")
public class HsReportTaskEntity {

  /**
   * 主键ID
   */
  @Id
  private String id;

  /**
   * 与id一致，重新产生报告时，添加新记录为初始目标报告的id
   */
  private String parentId;       //首次创建报告时，与id一致，重新产生报告时，添加新记录为初始目标报告的id。简单讲就是存储每次失败的记录将呗存储

  /**
   * 患者ID
   */
  private String personId;

  /**
   * 患者就诊id
   */
  private String patientId;
  /**
   * 患者当前绑定的设备标识，算法不一定使用，暂时留下
   */
  private String deviceId;
  /**
   * 关联的机构ID
   */
  private String orgId;
  /**
   * 医院标识，该id由org_id向上找到医院、经销商、root任意一个停止
   */
  private String rootOrgId;

  /**
   * 报告类型
   */
  private String item;
  /**
   * 业务类型，来源于businessType中，用于前端加载报告使用
   */
  private String type;
  /**
   * 报告产生路径
   */
  private String pdfPath;
  /**
   * 报告状态
   */
  private String status;
  /**
   * 报告状态描述,当报告产生失败时，存储失败原因
   */
  private String statusDesc;
  /**
   * 关联任务表，如果为空时，为手动生成报告
   */
  private String taskId;
  /**
   * 开始时间：yyyy-MM-dd HH:mm:ss
   */
  private Date startTime;
  /**
   * 结束时间：yyyy-MM-dd HH:mm:ss
   */
  private Date endTime;
  /**
   * 报告任务源
   */
  private String taskSource;
  /**
   * 执行次数，最多执行5次
   */
  private Integer times;

  /**
   * 报告日期，由结束时间确定其报告日期
   */
  private String reportDate;
  /**
   * 数据时长(分钟)
   */
  private Float duration;

  /**
   * 报告版本
   */
  private String version;
  /**
   * 处方来源
   */
  private String source;

  private String extend;
  private String stage; //阶段
  private Integer recording; //是否补录 0：正常，默认 1：补录


  /**
   * 创建人
   */
  @Column(name = "create_by", columnDefinition = "varchar(32) comment '创建人'")
  private String createBy;
  /**
   * 创建时间
   */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "create_time", columnDefinition = "datetime comment '创建时间'")
  private Date createTime;
  /**
   * 更新人
   */
  @Column(name = "update_by", columnDefinition = "varchar(32) comment '更新人'")
  private String updateBy;
  /**
   * 更新时间
   */
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "update_time", columnDefinition = "datetime DEFAULT NULL ON UPDATE current_timestamp() comment '更新时间'")
  private Date updateTime;

}
