package com.benefitj.spring.freemarker;

import com.benefitj.core.ClasspathUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.SingletonSupplier;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 代码生成
 */
public class CodeGenerator {

  private static final SingletonSupplier<CodeGenerator> singleton = SingletonSupplier.of(()
      -> new CodeGenerator(ClasspathUtils.getFile("ftls")));

  public static CodeGenerator getInstance() {
    return singleton.get();
  }

  private Configuration configuration;

  public CodeGenerator(File templateDir) {
    this.configuration = new FreemarkerBuilder()
        // 模板目录
        .setDirectoryForTemplateLoading(templateDir)
        // 3.设置字符集
        .setDefaultEncoding("utf-8")
        .getConfiguration();
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * 获取模板
   *
   * @param name 模板名
   * @return 返回模板
   */
  public Template getTemplate(String name) {
    return IOUtils.tryThrow(() -> getConfiguration().getTemplate(name));
  }

  /**
   * 写入
   *
   * @param templateName 模板名
   * @param dataModel    数据模型
   * @return 返回结果
   */
  public String write(String templateName, Object dataModel) {
    return write(getTemplate(templateName), dataModel);
  }

  /**
   * 写入
   *
   * @param template  模板
   * @param dataModel 数据模型
   * @return 返回结果
   */
  public String write(Template template, Object dataModel) {
    StringWriter writer = new StringWriter();
    IOUtils.tryThrow(() -> template.process(dataModel, writer));
    return writer.toString();
  }

  /**
   * 生成类
   *
   * @param descriptor 类描述
   * @param javaFile   Java文件
   * @return 返回保存的Java文件
   */
  public File writeFile(ClassDescriptor descriptor, File javaFile, String ftl) {
    String data = write(ftl, descriptor);
    FileOutputStream fos = IOUtils.newFOS(javaFile);
    IOUtils.write(fos, data);
    return javaFile;
  }

  /**
   * 写入module
   *
   * @param entity 描述符
   * @param dir    目录
   * @return 返回保存的Java文件
   */
  public File writeModule(ClassDescriptor entity, File dir, String module) {
    String path = dir.getAbsolutePath() + File.separator
        + entity.getBasePackage().replace(".", File.separator) + File.separator + module.toLowerCase();
    File javaFile = IOUtils.createFile(path, entity.getClassName() + module + ".java");
    return writeFile(entity, javaFile, module + ".ftl");
  }

  /**
   * 生成类
   *
   * @param entity 类描述
   * @param dir    目录
   * @return 返回保存的Java文件
   */
  public File writeEntity(ClassDescriptor entity, File dir) {
    return writeModule(entity, dir, "Entity");
  }

  /**
   * 写入Mapper
   *
   * @param entity 描述符
   * @param dir    目录
   * @return 返回保存的Java文件
   */
  public File writeMapper(ClassDescriptor entity, File dir) {
    return writeModule(entity, dir, "Mapper");
  }

//  /**
//   * 写入Service
//   *
//   * @param entity 描述符
//   * @param dir    目录
//   * @return 返回保存的Java文件
//   */
//  public File writeService(ClassDescriptor entity, File dir) {
//    return writeModule(entity, dir, "Service");
//  }

  /**
   * 写入业务代码 entity & mapper & service
   *
   * @param descriptor 描述符
   * @param dir        目录
   * @return 返回保存的Java文件
   */
  public Map<String, File> writeBusiness(ClassDescriptor descriptor, File dir) {
    Map<String, File> files = new LinkedHashMap<>();
    files.put("entity", writeEntity(descriptor, dir));
    files.put("mapper", writeMapper(descriptor, dir));
    //files.put("service", writeService(descriptor, dir));
    return files;
  }

}
