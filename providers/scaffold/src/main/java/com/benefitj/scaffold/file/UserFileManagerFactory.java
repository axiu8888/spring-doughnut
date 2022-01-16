package com.benefitj.scaffold.file;

import com.benefitj.core.file.IUserFileManager;

import java.io.File;

/**
 * 用户文件管理器
 */
public interface UserFileManagerFactory {

  /**
   * 创建新的用户文件管理器
   *
   * @param root 根目录
   * @param user 用户
   */
  IUserFileManager create(File root, String user);

}
