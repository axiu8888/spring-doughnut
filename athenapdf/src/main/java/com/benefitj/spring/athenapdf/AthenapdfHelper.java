package com.benefitj.spring.athenapdf;

import com.benefitj.core.cmd.CmdExecutor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * athenapdf 调用命令，生成PDF
 * <p>
 * docker: docker pull arachnysdocker/athenapdf
 */
public class AthenapdfHelper extends CmdExecutor {

  public static final AthenapdfHelper INSTANCE = new AthenapdfHelper();

  private final AtomicReference<Boolean> supportDocker = new AtomicReference<>();

  public AthenapdfHelper() {
  }

  public boolean supportDocker() {
    if (supportDocker.get() == null) {
      AthenapdfCall call = (AthenapdfCall) call("docker -v");
      String message = call.getMessage();
      supportDocker.set(StringUtils.isNotBlank(message) && message.contains("Docker version"));
    }
    return supportDocker.get();
  }

  @Override
  public AthenapdfCall createCmdCall(String id) {
    return new AthenapdfCall(id);
  }

  /**
   * 执行命令
   *
   * @param volumeDir Athenapdf映射的外部磁盘目录
   * @param destDir   PDF文件目录
   * @param filename  文件名
   * @param url       URL
   * @param network   网络别名(当在docker容器中执行时，可能会用到别名)
   * @return 返回调用结果（包含PDF文件）
   */
  public AthenapdfCall execute(String volumeDir, File destDir, String filename, String url, @Nullable String network) {
    filename = (filename.endsWith(".pdf") ? filename : filename + ".pdf");
    String cmd = "docker run --rm --privileged=true" +
        " -e TZ=\"Asia/Shanghai\"" + (network != null ? network : "")
        + " -v " + volumeDir + ":/converted/ arachnysdocker/athenapdf athenapdf -D 5000 --ignore-gpu-blacklist --no-cache "
        + url + " " + filename;
    AthenapdfCall call = (AthenapdfCall) call(cmd, null, destDir, 60_000);
    File pdf = new File(destDir, filename);
    if (pdf.exists()) {
      call.setPdf(pdf);
    }
    return call;
  }

}
