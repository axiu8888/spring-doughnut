package com.benefitj.scaffold.spring;

import com.benefitj.core.IOUtils;
import com.benefitj.scaffold.file.FileManagerFilter;
import com.benefitj.scaffold.file.SimpleUserFileManagerFactory;
import com.benefitj.scaffold.file.SystemFileManager;
import com.benefitj.scaffold.file.UserFileManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 系统文件
 */
@Configuration
public class SystemFileManagerConfiguration {

  @Value("#{ @environment['com.benefitj.file-manager.root'] ?: '/systemRoot/' }")
  private String root;

  /**
   * 系统文件管理
   */
  @ConditionalOnMissingBean
  @Bean
  public SystemFileManager systemFileManager(UserFileManagerFactory userFileManagerCreator) {
    return new SystemFileManager(IOUtils.mkDirs(root, "users/"), userFileManagerCreator);
  }

  /**
   * 用户文件管理创建对象
   */
  @ConditionalOnMissingBean
  @Bean
  public SimpleUserFileManagerFactory userFileManagerFactory() {
    return new SimpleUserFileManagerFactory();
  }

  /**
   * 文件管理的过滤器
   */
  @Order(100)
  @Bean
  public FileManagerFilter fileManagerFilter(SystemFileManager fileManager) {
    return new FileManagerFilter(fileManager);
  }

}
