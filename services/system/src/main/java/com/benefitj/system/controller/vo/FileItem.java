package com.benefitj.system.controller.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter
@Getter
public class FileItem {

  /**
   * 绝对路径
   */
  private String path;
  /**
   * 父目录
   */
  private String parent;
  /**
   * 文件名
   */
  private String filename;
  /**
   * 是否为目录
   */
  private boolean directory = false;
  /**
   * 文件大小
   */
  private long size = 0;
  /**
   * 后缀
   */
  private String suffix;


  /**
   * 将转文件转换为 FileItem
   *
   * @param file 文件
   * @param root 跟路径
   * @return 返回创建的FileItem
   */
  public static FileItem of(File file, String root) {
    FileItem item = new FileItem();
    int indexOf = file.getAbsolutePath().indexOf(root);
    if (indexOf >= 0) {
      int beginIndex = indexOf + root.length();
      item.setPath(file.getAbsolutePath().substring(beginIndex).replace("\\", "/"));
      item.setParent(file.getParent().substring(beginIndex).replace("\\", "/"));
    } else {
      item.setPath(file.getAbsolutePath().replace("\\", "/"));
      item.setParent(file.getParent().replace("\\", "/"));
    }
    item.setFilename(file.getName());
    item.setDirectory(file.isDirectory());
    item.setSize(file.length());
    String filename = item.getFilename();
    if (!item.isDirectory() && filename.lastIndexOf(".") > 0) {
      item.setSuffix(filename.substring(filename.lastIndexOf(".")));
    }
    return item;
  }
}
