package com.benefitj.fileserver;

import com.benefitj.spring.minio.spring.EnableMinio;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMinio
@SpringBootApplication
public class FileServerApp {
  public static void main(String[] args) {
    SpringApplication.run(FileServerApp.class, args);
  }
}
