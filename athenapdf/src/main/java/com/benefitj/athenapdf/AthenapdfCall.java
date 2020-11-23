package com.benefitj.athenapdf;

import com.benefitj.core.cmd.CmdCall;

import java.io.File;

/**
 * 命令调用对象
 */
public class AthenapdfCall extends CmdCall {

  /**
   * 生成的PDF文件
   */
  private File pdf;

  public AthenapdfCall() {
  }

  public AthenapdfCall(String id) {
    super(id);
  }

  public File getPdf() {
    return pdf;
  }

  public void setPdf(File pdf) {
    this.pdf = pdf;
  }

  @Override
  public boolean isSuccessful() {
    return getPdf() != null;
  }
}
