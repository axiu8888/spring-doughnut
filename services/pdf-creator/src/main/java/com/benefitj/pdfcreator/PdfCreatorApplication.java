package com.benefitj.pdfcreator;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.listener.AppStateHook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableHttpLoggingHandler
@SpringBootApplication
public class PdfCreatorApplication {
  public static void main(String[] args) {
    SpringApplication.run(PdfCreatorApplication.class, args);
  }

  static {
    AppStateHook.registerStart((evt) -> {




    });
  }

}
