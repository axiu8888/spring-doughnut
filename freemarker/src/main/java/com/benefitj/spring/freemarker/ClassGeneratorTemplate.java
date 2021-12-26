package com.benefitj.spring.freemarker;

import com.benefitj.core.ClasspathUtils;
import com.benefitj.core.IOUtils;
import com.benefitj.core.SingletonSupplier;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

/**
 * 类生成模板
 */
public class ClassGeneratorTemplate {

  private static final SingletonSupplier<ClassGeneratorTemplate> singleton = SingletonSupplier.of(()
      -> new ClassGeneratorTemplate(ClasspathUtils.getFile("ftl")));

  public static ClassGeneratorTemplate getInstance() {
    return singleton.get();
  }

  private Configuration configuration;

  public ClassGeneratorTemplate(File templateDir) {
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
   * @return 返回保存的Java文件
   */
  public File writeFile(ClassDescriptor descriptor, File dir, String ftl) {
    String data = write(ftl, descriptor);
    File javaFile = IOUtils.createFile(new File(dir, descriptor.getPackageName().replace(".", "/")), descriptor.getClassName() + ".java");
    FileOutputStream fos = IOUtils.newFOS(javaFile);
    IOUtils.write(fos, data);
    return javaFile;
  }

  /**
   * 生成类
   *
   * @param descriptor 类描述
   * @param dir        目录
   * @return 返回保存的Java文件
   */
  public File writeEntity(ClassDescriptor descriptor, File dir) {
    return writeFile(descriptor, dir, "Entity.ftl");
  }

  /**
   * 写入Mapper
   *
   * @param descriptor 描述符
   * @param dir        目录
   * @return 返回保存的Java文件
   */
  public File writeMapper(ClassDescriptor descriptor, File dir) {
    return writeFile(descriptor, dir, "Mapper.ftl");
  }

}
