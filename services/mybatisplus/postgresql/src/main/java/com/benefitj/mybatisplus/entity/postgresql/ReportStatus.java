package com.benefitj.mybatisplus.entity.postgresql;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: CWB
 * @Date: 2019/7/16 13:27
 * @Description:
 *
 */
@ApiModel("报告状态")
public enum ReportStatus {
    @ApiModelProperty("创建，不允许进入/重新执行报告")
    CREATE,         //创建，不允许进入/重新执行报告
    @ApiModelProperty("执行中，不允许进入/重新执行报告")
    RUNNING,        //执行中，不允许进入/重新执行报告
    @ApiModelProperty("重新执行，等待重新执行任务，用户在点击重新执行后进入retry状态，不允许进入报告和重新执行报告")
    RETRY,          //重新执行，等待重新执行任务，用户在点击重新执行后进入retry状态，不允许进入报告和重新执行报告
    @ApiModelProperty("被重新执行，已过时的状态")
    RETRYED,        //被重新执行，已过时的状态
    @ApiModelProperty("失败完成,可以重新执行报告，不允许进入报告")
    FAIL,           //失败完成,可以重新执行报告，不允许进入报告
    @ApiModelProperty("成功完成，可以重新执行报告，允许进入报告查看网页。pad可以打开")
    FINISH,         //成功完成，可以重新执行报告，允许进入报告查看网页。pad可以打开。
    @ApiModelProperty("确认，只看pdf。确认后的报告，不可以重新执行，只能打开pdf。目前没限制")
    CONFIRM,         //确认，只看pdf。确认后的报告，不可以重新执行，只能打开pdf。目前没限制
    @ApiModelProperty("作废，前置状态为FINISH，医生误判或误操作等导致作废")
    INVALID,         //作废，前置状态为FINISH，医生误判或误操作等导致作废
    @ApiModelProperty("待分析")
    ANALYSIS         //待分析
}
