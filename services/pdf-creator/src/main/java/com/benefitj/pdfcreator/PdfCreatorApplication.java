package com.benefitj.pdfcreator;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableHttpLoggingHandler
@SpringBootApplication
public class PdfCreatorApplication {
  public static void main(String[] args) {
    SpringApplication.run(PdfCreatorApplication.class, args);
  }
}
