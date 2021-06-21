package com.benefitj.license;

import com.alibaba.fastjson.JSON;
import de.schlichtherle.license.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

/**
 * License生成类
 */
public class LicenseCreator {

  private static Logger logger = LoggerFactory.getLogger(LicenseCreator.class);

  private static final X500Principal DEFAULT_HOLDER_AND_ISSUER
      = new X500Principal("CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN");

  private LicenseCreatorProperty property;

  public LicenseCreator(LicenseCreatorProperty property) {
    this.property = property;
  }

  /**
   * 生成License证书
   */
  public boolean generateLicense() {
    try {
      LicenseManager licenseManager = new DefaultLicenseManager(initLicenseParam());
      LicenseContent licenseContent = initLicenseContent();
      licenseManager.store(licenseContent, new File(property.getLicensePath()));
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(MessageFormat.format("证书生成失败：{0}", JSON.toJSONString(property)), e);
      return false;
    }
  }

  /**
   * 初始化证书生成参数
   */
  private LicenseParam initLicenseParam() {
    Preferences preferences = Preferences.userNodeForPackage(LicenseCreator.class);

    //设置对证书内容加密的秘钥
    CipherParam cipherParam = new DefaultCipherParam(property.getStorePassword());

    KeyStoreParam privateStoreParam = new DefaultKeyStoreParam(LicenseCreator.class
        , property.getPrivateKeysStorePath()
        , property.getPrivateAlias()
        , property.getStorePassword()
        , property.getKeyPassword());

    LicenseParam licenseParam = new DefaultLicenseParam(property.getSubject()
        , preferences
        , privateStoreParam
        , cipherParam);

    return licenseParam;
  }

  /**
   * 设置证书生成正文信息
   */
  private LicenseContent initLicenseContent() {
    LicenseContent lc = new LicenseContent();
    lc.setHolder(DEFAULT_HOLDER_AND_ISSUER);
    lc.setIssuer(DEFAULT_HOLDER_AND_ISSUER);

    lc.setSubject(property.getSubject());
    lc.setIssued(property.getIssuedTime());
    lc.setNotBefore(property.getIssuedTime());
    lc.setNotAfter(property.getExpiryTime());
    lc.setConsumerType(property.getConsumerType());
    lc.setConsumerAmount(property.getConsumerAmount());
    lc.setInfo(property.getDescription());

    //扩展校验服务器硬件信息
    lc.setExtra(property.getLicenseModel());

    return lc;
  }

}
