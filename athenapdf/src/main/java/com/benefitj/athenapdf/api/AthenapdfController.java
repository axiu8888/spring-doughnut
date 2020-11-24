package com.benefitj.athenapdf.api;

import com.benefitj.athenapdf.AthenapdfCall;
import com.benefitj.athenapdf.AthenapdfHelper;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.spring.aop.AopWebPointCut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 生成报告的接口
 */
@Slf4j
@ConditionalOnClass(RequestMapping.class)
@AopWebPointCut
@RestController
@RequestMapping("/athenapdf")
public class AthenapdfController {

  @Value("#{ @environment['spring.athenapdf.cache-dir'] ?: '/tmp/athenapdf-pdf/' }")
  private String cacheDir;

  @Autowired
  private AthenapdfHelper athenapdfHelper;

  /**
   * 生成PDF
   *
   * @param response HTTP响应
   * @param url      HTML的路径
   * @param filename 文件名，可选
   */
  @GetMapping("/create")
  public void create(HttpServletResponse response,
                     String url,
                     String filename) throws IOException {
    long start = System.currentTimeMillis();
    try {
      if (StringUtils.isBlank(url)) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().write("缺少url参数");
        return;
      }
      URL ignore = new URL(url);
    } catch (MalformedURLException e) {
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.getWriter().write("错误的url参数");
      return;
    }
    filename = StringUtils.isNoneBlank(filename) ? filename : IdUtils.uuid() + ".pdf";
    AthenapdfCall call = athenapdfHelper.execute(IOUtils.mkDirs(cacheDir)
        , url
        , filename
        , null
    );
    if (call.isSuccessful()) {
      File pdf = call.getPdf();
      response.addHeader("Content-Disposition", "attachment; filename=" +
          new String(filename.getBytes(), StandardCharsets.ISO_8859_1));
      response.addHeader("Content-Length", String.valueOf(pdf.length()));
      // 写入文件
      try (final FileInputStream fis = new FileInputStream(pdf);) {
        IOUtils.write(fis, response.getOutputStream());
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        // 最终删除文件
        IOUtils.deleteFile(pdf);
        log.info("{}, 使用时长: {}", filename, (System.currentTimeMillis() - start));
      }
    }
  }

}
