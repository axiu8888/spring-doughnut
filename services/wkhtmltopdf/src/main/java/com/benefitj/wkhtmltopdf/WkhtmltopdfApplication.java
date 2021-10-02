package com.benefitj.wkhtmltopdf;

import com.benefitj.core.cmd.CmdCall;
import com.benefitj.core.cmd.CmdExecutor;
import com.benefitj.core.cmd.CmdExecutorHolder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class WkhtmltopdfApplication {
  public static void main(String[] args) {
//    SpringApplication.run(WkhtmltopdfApplication.class, args);

    CmdExecutor executor = CmdExecutorHolder.getInstance();

    String path = getClasspathDir().replace("\\", "/");
    path = path.endsWith("/classes/java/main") ? path.substring(0, path.lastIndexOf("/classes/java/main")) : path;

    System.err.println("path ==>: " + path);

    File envDir = new File(path);

    CmdCall versionCall = executor.call("wkhtmltopdf --version", null, envDir);
    printCall(versionCall, "version");

    // wkhtmltopdf https://weishu.me/2021/09/26/start-to-use-Rust/  start-to-use-Rust.pdf
    String createPdfCmd = "wkhtmltopdf https://weishu.me/2021/09/26/start-to-use-Rust/  start-to-use-Rust.pdf";
    CmdCall createPdfCall = executor.call(createPdfCmd, null, envDir);
    printCall(createPdfCall, "createPdf");


    System.err.println("supportSpring: " + supportSpring());
    System.err.println("path: " + getClasspathDir());

  }

  private static String getClasspathDir() {
    if (supportSpring()) {
      try {
        ClassPathResource resource = new ClassPathResource(".");
        return new File(resource.getURL().getPath()).getAbsolutePath();
      } catch (IOException ignore) {}
    }
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    return loader.getResource("").getPath();
  }

  public static boolean supportSpring() {
    try {
      Class.forName("org.springframework.core.io.ClassPathResource");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  private static void printCall(CmdCall call, String name) {
    System.err.println("\n----------------- cmd[" + name + "] -----------------");
    System.err.println("successful: " + call.isSuccessful());
    if (call.getMessage().endsWith("\n")) {
      call.setMessage(call.getMessage().substring(0, call.getMessage().length() - 1));
    }
    System.err.println("message: " + call.getMessage());
    System.err.println("error: " + call.getError());
    System.err.println("cmd: " + call.getCmd());
    System.err.println("exitCode: " + call.getCode());
    System.err.println("----------------- cmd[" + name + "] -----------------\n");
  }


  //--allow <path>  允许加载从指定的文件夹中的文件或文件（可重复）
  //--book*  设置一会打印一本书的时候，通常设置的选项
  //--collate  打印多份副本时整理
  //--cookie <name> <value>  设置一个额外的cookie（可重复）
  //--cookie-jar <path>  读取和写入的Cookie，并在提供的cookie jar文件
  //--copies <number>  复印打印成pdf文件数（默认为1）
  //--cover* <url>  使用HTML文件作为封面。它会带页眉和页脚的TOC之前插入
  //--custom-header <name> <value>  设置一个附加的HTTP头（可重复）
  //--debug-javascript  显示的javascript调试输出
  //--default-header*  添加一个缺省的头部，与页面的左边的名称，页面数到右边，例如： --header-left '[webpage]' --header-right '[page]/[toPage]'  --header-line
  //--disable-external-links*  禁止生成链接到远程网页
  //--disable-internal-links*  禁止使用本地链接
  //--disable-javascript  禁止让网页执行JavaScript
  //--disable-pdf-compression*  禁止在PDF对象使用无损压缩
  //--disable-smart-shrinking*  禁止使用WebKit的智能战略收缩，使像素/ DPI比没有不变
  //--disallow-local-file-access  禁止允许转换的本地文件读取其他本地文件，除非explecitily允许用 --allow
  //--dpi <dpi>  显式更改DPI（这对基于X11的系统没有任何影响）
  //--enable-plugins  启用已安装的插件（如Flash
  //--encoding <encoding>  设置默认的文字编码
  //--extended-help  显示更广泛的帮助，详细介绍了不常见的命令开关
  //--forms*  打开HTML表单字段转换为PDF表单域
  //--grayscale  PDF格式将在灰阶产生
  //--help  Display help
  //--htmldoc  输出程序HTML帮助
  //--ignore-load-errors  忽略claimes加载过程中已经遇到了一个错误页面
  //--lowquality  产生低品质的PDF/ PS。有用缩小结果文档的空间
  //--manpage  输出程序手册页
  //--margin-bottom <unitreal>  设置页面下边距 (default 10mm)
  //--margin-left <unitreal>  将左边页边距 (default 10mm)
  //--margin-right <unitreal>  设置页面右边距 (default 10mm)
  //--margin-top <unitreal>  设置页面上边距 (default 10mm)
  //--minimum-font-size <int>  最小字体大小 (default 5)
  //--no-background  不打印背景
  //--orientation <orientation>  设置方向为横向或纵向
  //--page-height <unitreal>  页面高度 (default unit millimeter)
  //--page-offset* <offset>  设置起始页码 (default 1)
  //--page-size <size>  设置纸张大小: A4, Letter, etc.
  //--page-width <unitreal>  页面宽度 (default unit millimeter)
  //--password <password>  HTTP验证密码
  //--post <name> <value>  Add an additional post field (repeatable)
  //--post-file <name> <path>  Post an aditional file (repeatable)
  //--print-media-type*  使用的打印介质类型，而不是屏幕
  //--proxy <proxy>  使用代理
  //--quiet  Be less verbose
  //--read-args-from-stdin  读取标准输入的命令行参数
  //--readme  输出程序自述
  //--redirect-delay <msec>  等待几毫秒为JS-重定向(default 200)
  //--replace* <name> <value>  替换名称,值的页眉和页脚（可重复）
  //--stop-slow-scripts  停止运行缓慢的JavaScripts
  //--title <text>  生成的PDF文件的标题（第一个文档的标题使用，如果没有指定）
  //--toc*  插入的内容的表中的文件的开头
  //--use-xserver*  使用X服务器（一些插件和其他的东西没有X11可能无法正常工作）
  //--user-style-sheet <url>  指定用户的样式表，加载在每一页中
  //--username <username>  HTTP认证的用户名
  //--version  输出版本信息退出
  //--zoom <float>  使用这个缩放因子 (default 1)


}
