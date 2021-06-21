package com.benefitj.license;

import com.benefitj.core.DateFmtter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * License生成类需要的参数
 */
public class LicenseCreatorProperty implements Serializable {

  private static final long serialVersionUID = -7793154252684580872L;
  /**
   * 证书subject
   */
  private String subject;
  /**
   * 密钥别称
   */
  private String privateAlias;
  /**
   * 密钥密码（需要妥善保管，不能让使用者知道）
   */
  private String keyPassword;
  /**
   * 访问秘钥库的密码
   */
  private String storePassword;
  /**
   * 证书生成路径
   */
  private String licensePath;
  /**
   * 密钥库存储路径
   */
  private String privateKeysStorePath;
  /**
   * 证书生效时间
   */
  private Date issuedTime = new Date();
  /**
   * 证书失效时间
   */
  private Date expiryTime;
  /**
   * 用户类型
   */
  private String consumerType = "user";
  /**
   * 用户数量
   */
  private Integer consumerAmount = 1;
  /**
   * 描述信息
   */
  private String description = "";
  /**
   * 额外的服务器硬件校验信息
   */
  private HardwareInfo hardwareInfo = new HardwareInfo();

  public LicenseCreatorProperty() {
  }

  public LicenseCreatorProperty(ResourceBundle conf) {
    init(conf);
  }

  protected LicenseCreatorProperty init(ResourceBundle bundle) {
    this.setSubject(bundle.getString("subject"));
    this.setPrivateAlias(bundle.getString("privateAlias"));
    this.setKeyPassword(bundle.getString("keyPass"));
    this.setStorePassword(bundle.getString("storePass"));
    this.setLicensePath(System.getProperty("user.dir") + bundle.getString("licPath"));
    this.setPrivateKeysStorePath(System.getProperty("user.dir") + bundle.getString("priPath"));
    this.setLicensePath(bundle.getString("licPath"));
    this.setPrivateKeysStorePath(bundle.getString("priPath"));
    this.issuedTime = DateFmtter.parse(bundle.getString("issuedTime"), "yyyy-MM-dd");
    this.expiryTime = DateFmtter.parse(bundle.getString("expiryTime"), "yyyy-MM-dd");
    this.consumerAmount = Integer.parseInt(bundle.getString("consumerAmount"));
    this.description = bundle.getString("description");
    this.hardwareInfo.setCpuSerial(bundle.getString("cpuSerial"));
    if (!bundle.getString("macAddress").isEmpty()) {
      this.hardwareInfo.setMacAddress(Arrays.asList(bundle.getString("macAddress").split(",")));
    }
    if (!bundle.getString("ipAddress").isEmpty()) {
      this.hardwareInfo.setIpAddress(Arrays.asList(bundle.getString("ipAddress").split(",")));
    }
    this.hardwareInfo.setMainBoardSerial(bundle.getString("mainBoardSerial"));
    this.hardwareInfo.setSerialNumber(bundle.getString("serialNumber"));
    return this;
  }


  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getPrivateAlias() {
    return privateAlias;
  }

  public void setPrivateAlias(String privateAlias) {
    this.privateAlias = privateAlias;
  }

  public String getKeyPassword() {
    return keyPassword;
  }

  public void setKeyPassword(String keyPassword) {
    this.keyPassword = keyPassword;
  }

  public String getStorePassword() {
    return storePassword;
  }

  public void setStorePassword(String storePassword) {
    this.storePassword = storePassword;
  }

  public String getLicensePath() {
    return licensePath;
  }

  public void setLicensePath(String licensePath) {
    this.licensePath = licensePath;
  }

  public String getPrivateKeysStorePath() {
    return privateKeysStorePath;
  }

  public void setPrivateKeysStorePath(String privateKeysStorePath) {
    this.privateKeysStorePath = privateKeysStorePath;
  }

  public Date getIssuedTime() {
    return issuedTime;
  }

  public void setIssuedTime(Date issuedTime) {
    this.issuedTime = issuedTime;
  }

  public Date getExpiryTime() {
    return expiryTime;
  }

  public void setExpiryTime(Date expiryTime) {
    this.expiryTime = expiryTime;
  }

  public String getConsumerType() {
    return consumerType;
  }

  public void setConsumerType(String consumerType) {
    this.consumerType = consumerType;
  }

  public Integer getConsumerAmount() {
    return consumerAmount;
  }

  public void setConsumerAmount(Integer consumerAmount) {
    this.consumerAmount = consumerAmount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public HardwareInfo getLicenseModel() {
    return hardwareInfo;
  }

  public void setLicenseModel(HardwareInfo hardwareInfo) {
    this.hardwareInfo = hardwareInfo;
  }

}
