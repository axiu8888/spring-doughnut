package com.benefitj.license;

import com.benefitj.core.DateFmtter;
import de.schlichtherle.license.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.MessageFormat;
import java.util.prefs.Preferences;

/**
 * License校验类
 */
public class LicenseVerify {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 安装License证书
   */
  public synchronized LicenseContent install(LicenseVerifyProperty property) {
    LicenseContent result = null;
    //1. 安装证书
    try {
      LicenseParam licenseParam = initLicenseParam(property);
      LicenseManager licenseManager = LicenseManagerHolder.getInstance(licenseParam);
      licenseManager.uninstall();

      result = licenseManager.install(new File(property.getLicensePath()));
      logger.info(MessageFormat.format("证书安装成功，证书有效期：{0} - {1}"
          , DateFmtter.fmt(result.getNotBefore(), "yyyy-MM-dd HH:mm:ss")
          , DateFmtter.fmt(result.getNotAfter(), "yyyy-MM-dd HH:mm:ss")
      ));
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("证书安装失败！", e);
    }
    return result;
  }

  /**
   * 校验License证书
   */
  public boolean verify() {
    LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
    //2. 校验证书
    try {
      LicenseContent licenseContent = licenseManager.verify();
      logger.info(MessageFormat.format("证书校验通过，证书有效期：{0} - {1}"
          , DateFmtter.fmt(licenseContent.getNotBefore(), "yyyy-MM-dd HH:mm:ss")
          , DateFmtter.fmt(licenseContent.getNotAfter(), "yyyy-MM-dd HH:mm:ss")
      ));
      return true;
    } catch (Exception e) {
      logger.error("证书校验失败！", e);
      return false;
    }
  }

  /**
   * 初始化证书生成参数
   */
  private LicenseParam initLicenseParam(LicenseVerifyProperty property) {
    Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);
    CipherParam cipherParam = new DefaultCipherParam(property.getStorePass());
    KeyStoreParam publicStoreParam = new DefaultKeyStoreParam(LicenseVerify.class
        , property.getPublicKeysStorePath()
        , property.getPublicAlias()
        , property.getStorePass()
        , null);
    return new DefaultLicenseParam(property.getSubject()
        , preferences
        , publicStoreParam
        , cipherParam);
  }

}
