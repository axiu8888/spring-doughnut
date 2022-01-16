package com.benefitj.scaffold.file;

import com.benefitj.core.file.IUserFileManager;
import com.benefitj.core.file.UserFileManager;

import java.io.File;

public class SimpleUserFileManagerFactory implements UserFileManagerFactory {

  /**
   * 创建新的用户文件管理器
   *
   * @param root 根目录
   * @param user 用户
   */
  @Override
  public IUserFileManager create(File root, String user) {
    UserFileManager manager = new UserFileManager();
    manager.setRoot(root, true);
    manager.setUsername(user);
    return manager;
  }

}
