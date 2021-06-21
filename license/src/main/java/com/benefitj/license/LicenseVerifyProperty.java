package com.benefitj.license;

import java.util.ResourceBundle;

/**
 * License校验类需要的参数
 */
public class LicenseVerifyProperty {

  /**
   * 证书subject
   */
  private String subject;

  /**
   * 公钥别称
   */
  private String publicAlias;
  /**
   * 访问公钥库的密码
   */
  private String storePass;

  /**
   * 证书生成路径
   */
  private String licensePath;

  /**
   * 密钥库存储路径
   */
  private String publicKeysStorePath;

  public LicenseVerifyProperty() {
    ResourceBundle bundle = ResourceBundle.getBundle("license");
    this.subject = bundle.getString("license.subject");
    this.publicAlias = bundle.getString("license.publicAlias");
    this.storePass = bundle.getString("license.storePass");
    this.licensePath = bundle.getString("license.licensePath");
    this.publicKeysStorePath = bundle.getString("license.publicKeysStorePath");
  }

  public LicenseVerifyProperty(String subject,
                               String publicAlias,
                               String storePass,
                               String licensePath,
                               String publicKeysStorePath) {
    this.subject = subject;
    this.publicAlias = publicAlias;
    this.storePass = storePass;
    this.licensePath = licensePath;
    this.publicKeysStorePath = publicKeysStorePath;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getPublicAlias() {
    return publicAlias;
  }

  public void setPublicAlias(String publicAlias) {
    this.publicAlias = publicAlias;
  }

  public String getStorePass() {
    return storePass;
  }

  public void setStorePass(String storePass) {
    this.storePass = storePass;
  }

  public String getLicensePath() {
    return licensePath;
  }

  public void setLicensePath(String licensePath) {
    this.licensePath = licensePath;
  }

  public String getPublicKeysStorePath() {
    return publicKeysStorePath;
  }

  public void setPublicKeysStorePath(String publicKeysStorePath) {
    this.publicKeysStorePath = publicKeysStorePath;
  }

}
