package com.benefitj.scaffold.file;

import com.benefitj.core.file.FileManager;
import com.benefitj.core.file.IUserFileManager;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统文件管理
 */
public class SystemFileManager extends FileManager {

  /**
   * 本地用户文件缓存
   */
  private final ThreadLocal<IUserFileManager> currentUserLocal = new ThreadLocal<>();
  /**
   * 用户文件缓存
   */
  private final Map<String, IUserFileManager> userFileManagers = new ConcurrentHashMap<>();
  /**
   * UserFileManager创建器
   */
  private UserFileManagerFactory managerFactory;

  public SystemFileManager(File root) {
    super(root);
  }

  public SystemFileManager(File root, UserFileManagerFactory managerFactory) {
    super(root);
    this.managerFactory = managerFactory;
  }

  public UserFileManagerFactory getManagerFactory() {
    return managerFactory;
  }

  public void setManagerFactory(UserFileManagerFactory managerFactory) {
    this.managerFactory = managerFactory;
  }

  protected IUserFileManager newUserFileManager(String userId) {
    return getManagerFactory().create(getRoot(), userId);
  }

  public ThreadLocal<IUserFileManager> getCurrentUserLocal() {
    return currentUserLocal;
  }

  public Map<String, IUserFileManager> getUserFileManagers() {
    return userFileManagers;
  }

  public IUserFileManager getUserFileManager(String user) {
    return getUserFileManagers().get(user);
  }

  /**
   * 设置当前用户的 FileManager
   *
   * @param user 用户
   * @return 返回被设备的FileManager
   */
  public IUserFileManager setCurrentUser(String user) {
    IUserFileManager manager = getUserFileManager(user);
    if (manager == null) {
      manager = newUserFileManager(user);
    }
    getCurrentUserLocal().set(manager);
    return manager;
  }

  /**
   * 获取当前用户的文件管理
   */
  public IUserFileManager currentUser() {
    return getCurrentUserLocal().get();
  }

  /**
   * 移除当前用户的文件管理
   */
  public void removeUser() {
    getCurrentUserLocal().remove();
  }

}
