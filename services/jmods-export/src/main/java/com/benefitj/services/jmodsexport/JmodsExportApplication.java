package com.benefitj.services.jmodsexport;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.SystemProperty;
import com.benefitj.core.cmd.CmdCall;
import com.benefitj.core.cmd.CmdExecutor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@EnableConfigurationProperties
@SpringBootApplication
public class JmodsExportApplication {
  public static void main(String[] args) {
    SpringApplication.run(JmodsExportApplication.class, args);
  }


  @EventListener
  public void onAppStart(ApplicationReadyEvent event) {
    JmodsExportProperty prop = event.getApplicationContext().getBean(JmodsExportProperty.class);

    if (StringUtils.isAnyBlank(prop.getExportDir())) {
      shutdown("导出目录不能为空", prop.getErrorDelay());
      return;
    }

    File jdkDir = null;
    if (StringUtils.isNotBlank(prop.getJdkDir())) {
      jdkDir = new File(prop.getJdkDir());
      if (!jdkDir.exists()) {
        jdkDir = null;
      }

      if (jdkDir == null && !checkJava()) {
        shutdown("没有java环境", prop.getErrorDelay());
        return;
      }
    }

    if (jdkDir == null) {
      boolean support = false;
      try {
        float version = Float.parseFloat(SystemProperty.getJavaVersion());
        support = version > 1.8;
      } catch (Exception ignore) {}
      if (!support) {
        shutdown("不支持的Java版本(" + SystemProperty.getJavaVersion() + ")，请指定具体的JDK路径", prop.getErrorDelay());
        return;
      }
    }

    File exportDir = new File(prop.getExportDir());

    List<String> modules;
    if (jdkDir == null) {
      modules = new ArrayList<>();
      modules.addAll(Arrays.asList(prop.getDefaultJmodes().split(",")));
      modules.addAll(Arrays.asList(prop.getAddJmodes().split(",")));
    } else {
      modules = IOUtils.listFiles(new File(jdkDir, "jmods"))
          .stream()
          .map(File::getName)
          .map(name -> name.endsWith(".jmod") ? name.substring(0, name.length() - ".jmod".length()) : name)
          .collect(Collectors.toList());
      modules.addAll(Arrays.asList(prop.getAddJmodes().split(",")));
    }

    Set<String> ignoreJmods = new HashSet<>(Arrays.asList(prop.getIgnoreJmods().split(",")));
    String mods = modules.stream()
        .filter(StringUtils::isNotBlank)
        .distinct()
        // 忽略JDK的依赖
        .filter(name -> !prop.isIgnoreJdkJmods() || !name.startsWith("jdk."))
        .filter(name -> !ignoreJmods.contains(name))
        .collect(Collectors.joining(","));

    String exportName = prop.getExportName();
    String cmd = String.format("%s --output %s --add-modules %s"
        , jdkDir != null ? String.format("%s%s%s%sjlink.exe", jdkDir.getAbsolutePath(), File.separator, "bin", File.separator) : "jlink"
        , exportName
        , mods
    );
    File exportFile = new File(exportDir, exportName);
    IOUtils.deleteFile(exportFile);
    CmdCall call = CmdExecutor.get().call(cmd, null, exportDir);
    call.setProcess(null);
    System.err.println(JSON.toJSON(call));
    System.err.println("----------------------------------------------");
    System.err.println("cmd: " + call.getCmd());
    System.err.println("message: " + call.getMessage());
    System.err.println("error: " + call.getError());
    System.err.println("successful: " + (call.isSuccessful() || exportFile.exists()));
    System.err.println("----------------------------------------------");

    if (!call.isSuccessful() && !exportFile.exists()) {
      shutdown("导出失败: " + call.getError(), prop.getErrorDelay());
      return;
    }

    System.exit(0);
  }

  private boolean checkJava() {
    CmdCall call;
    call = CmdExecutor.get().call("java -version");
    if (!call.isSuccessful()) {
      call = CmdExecutor.get().call("java --version");
      return call.isSuccessful();
    }
    return true;
  }


  private void shutdown(String error, int seconds) {
    System.err.println(error);
    EventLoop.newSingle(false)
        .schedule(() -> System.exit(0), seconds, TimeUnit.SECONDS);
  }

  public static final String JMODS = "java.base" +
      ",java.compiler" +
      ",java.datatransfer" +
      ",java.desktop" +
      ",java.instrument" +
      ",java.logging" +
      ",java.management" +
      ",java.management.rmi" +
      ",java.naming" +
      ",java.net.http" +
      ",java.prefs" +
      ",java.rmi" +
      ",java.scripting" +
      ",java.se" +
      ",java.security.jgss" +
      ",java.security.sasl" +
      ",java.smartcardio" +
      ",java.sql" +
      ",java.sql.rowset" +
      ",java.transaction.xa" +
      ",java.xml.crypto" +
      ",java.xml" +
      ",jdk.accessibility" +
      ",jdk.aot" +
      ",jdk.attach" +
      ",jdk.charsets" +
      ",jdk.compiler" +
      ",jdk.crypto.cryptoki" +
      ",jdk.crypto.ec" +
      ",jdk.crypto.mscapi" +
      ",jdk.dynalink" +
      ",jdk.editpad" +
      ",jdk.hotspot.agent" +
      ",jdk.httpserver" +
      ",jdk.internal.ed" +
      ",jdk.internal.jvmstat" +
      ",jdk.internal.le" +
      ",jdk.internal.opt" +
      ",jdk.internal.vm.ci" +
      ",jdk.internal.vm.compiler" +
      ",jdk.internal.vm.compiler.management" +
      ",jdk.jartool" +
      ",jdk.javadoc" +
      ",jdk.jcmd" +
      ",jdk.jconsole" +
      ",jdk.jdeps" +
      ",jdk.jdi" +
      ",jdk.jdwp.agent" +
      ",jdk.jfr" +
      ",jdk.jlink" +
      ",jdk.jshell" +
      ",jdk.jsobject" +
      ",jdk.jstatd" +
      ",jdk.localedata" +
      ",jdk.management.agent" +
      ",jdk.management.jfr" +
      ",jdk.management" +
      ",jdk.naming.dns" +
      ",jdk.naming.rmi" +
      ",jdk.net" +
      ",jdk.pack" +
      ",jdk.rmic" +
      ",jdk.scripting.nashorn" +
      ",jdk.scripting.nashorn.shell" +
      ",jdk.sctp" +
      ",jdk.security.auth" +
      ",jdk.security.jgss" +
      ",jdk.unsupported.desktop" +
      ",jdk.unsupported" +
      ",jdk.xml.dom" +
      ",jdk.zipfs";

  @Component
  @Data
  @ConfigurationProperties(prefix = "jmods")
  public static class JmodsExportProperty {

    /**
     * JDK的目录
     */
    private String jdkDir;
    /**
     * JRE的导出目录
     */
    private String exportDir = "./";
    /**
     * 导出的JRE目录名称
     */
    private String exportName = "jre";
    /**
     * 是否忽略JDK模块
     */
    private boolean ignoreJdkJmods = true;
    /**
     * 默认模块，${@link #JMODS}
     */
    private String defaultJmodes = JMODS;
    /**
     * 添加的模块
     */
    private String addJmodes = "";
    /**
     * 忽略的模块
     */
    private String ignoreJmods = "";
    /**
     * 出现错误的延迟关闭时长
     */
    private int errorDelay = 10;
  }

}
