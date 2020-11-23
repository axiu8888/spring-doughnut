package com.benefitj.athenapdf.api;

import com.benefitj.athenapdf.AthenapdfCall;
import com.benefitj.athenapdf.AthenapdfHelper;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 生成报告的接口
 */
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
                     String filename) {
    filename = StringUtils.isNoneBlank(filename) ? filename : IdUtils.uuid() + ".pdf";
    AthenapdfCall call = athenapdfHelper.execute(new File(cacheDir)
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
      }
    }
  }

}
