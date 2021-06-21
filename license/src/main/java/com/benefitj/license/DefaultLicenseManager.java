package com.benefitj.license;

import com.benefitj.core.cmd.SystemOS;
import de.schlichtherle.license.*;
import de.schlichtherle.xml.GenericCertificate;
import org.apache.commons.lang3.StringUtils;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 自定义LicenseManager，用于增加额外的服务器硬件信息校验
 */
public class DefaultLicenseManager extends LicenseManager {

  // XML编码
  private static final String XML_CHARSET = "UTF-8";
  // 默认BUFSIZE
  private static final int DEFAULT_BUFSIZE = 8 * 1024;

  public DefaultLicenseManager() {
  }

  public DefaultLicenseManager(LicenseParam param) {
    super(param);
  }

  /**
   * 复写create方法
   */
  @Override
  protected byte[] create(LicenseContent content, LicenseNotary notary) throws Exception {
    initialize(content);
    this.validateCreate(content);
    final GenericCertificate certificate = notary.sign(content);
    return getPrivacyGuard().cert2key(certificate);
  }

  /**
   * 复写install方法，其中validate方法调用本类中的validate方法，校验IP地址、Mac地址等其他信息
   */
  @Override
  protected synchronized LicenseContent install(final byte[] key, final LicenseNotary notary) throws Exception {
    final GenericCertificate certificate = getPrivacyGuard().key2cert(key);

    notary.verify(certificate);
    final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
    this.validate(content);
    setLicenseKey(key);
    setCertificate(certificate);

    return content;
  }

  /**
   * 复写verify方法，调用本类中的validate方法，校验IP地址、Mac地址等其他信息
   */
  @Override
  protected synchronized LicenseContent verify(final LicenseNotary notary) throws Exception {
    GenericCertificate certificate = getCertificate();

    // Load license key from preferences,
    final byte[] key = getLicenseKey();
    if (null == key) {
      throw new NoLicenseInstalledException(getLicenseParam().getSubject());
    }

    certificate = getPrivacyGuard().key2cert(key);
    notary.verify(certificate);
    final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
    this.validate(content);
    setCertificate(certificate);

    return content;
  }

  /**
   * 校验生成证书的参数信息
   */
  protected void validateCreate(final LicenseContent content) throws LicenseContentException {
    final Date now = new Date();
    final Date notBefore = content.getNotBefore();
    final Date notAfter = content.getNotAfter();
    if (null != notAfter && now.after(notAfter)) {
      throw new LicenseContentException("证书失效时间不能早于当前时间");
    }
    if (null != notBefore && null != notAfter && notAfter.before(notBefore)) {
      throw new LicenseContentException("证书生效时间不能晚于证书失效时间");
    }
    final String consumerType = content.getConsumerType();
    if (null == consumerType) {
      throw new LicenseContentException("用户类型不能为空");
    }
  }


  /**
   * 复写validate方法，增加IP地址、Mac地址等其他信息校验
   */
  @Override
  protected synchronized void validate(final LicenseContent content) throws LicenseContentException {
    //1. 首先调用父类的validate方法
    super.validate(content);

    //2. 然后校验自定义的License参数
    //License中可被允许的参数信息
    HardwareInfo extraModel = (HardwareInfo) content.getExtra();
    //当前服务器真实的参数信息
    HardwareInfo serverModel = getServerInfos();

    if (extraModel == null || serverModel == null) {
      throw new LicenseContentException("不能获取服务器硬件信息");
    }

    //校验IP地址
    if (!checkIpAddress(extraModel.getIpAddress(), serverModel.getIpAddress())) {
      throw new LicenseContentException("当前服务器的IP没在授权范围内");
    }

    //校验Mac地址
    if (!checkIpAddress(extraModel.getMacAddress(), serverModel.getMacAddress())) {
      throw new LicenseContentException("当前服务器的Mac地址没在授权范围内");
    }

    //校验主板序列号
    if (!checkSerial(extraModel.getMainBoardSerial(), serverModel.getMainBoardSerial())) {
      throw new LicenseContentException("当前服务器的主板序列号没在授权范围内：" + serverModel.getMainBoardSerial());
    }

    //校验CPU序列号
    if (!checkSerial(extraModel.getCpuSerial(), serverModel.getCpuSerial())) {
      throw new LicenseContentException("当前服务器的CPU序列号没在授权范围内：" + serverModel.getCpuSerial());
    }

    //系统序列号
    if (!checkSerial(extraModel.getSerialNumber(), serverModel.getSerialNumber())) {
      throw new LicenseContentException("系统序列号未授权:" + serverModel.getSerialNumber());
    }
  }

  /**
   * 重写XMLDecoder解析XML
   */
  private Object load(String encoded) {
    try (final BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(encoded.getBytes(XML_CHARSET)));
         final XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bis, DEFAULT_BUFSIZE), null, null);) {
      return decoder.readObject();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 获取当前服务器需要额外校验的License参数
   */
  private HardwareInfo getServerInfos() {
    //操作系统类型
    ServerInfos serverInfos;

    //根据不同操作系统类型选择不同的数据获取方法
    if (SystemOS.LOCALE.isWindows()) {
      serverInfos = new WindowsServerInfos();
    } else {
      //其他服务器类型
      serverInfos = new LinuxServerInfos();
    }

    return serverInfos.getServerInfos();
  }

  /**
   * 校验当前服务器的IP/Mac地址是否在可被允许的IP范围内<br/>
   * 如果存在IP在可被允许的IP/Mac地址范围内，则返回true
   */
  private boolean checkIpAddress(List<String> expectedList, List<String> serverList) {
    if (expectedList != null && !expectedList.isEmpty()) {
      for (String expected : expectedList) {
        if (serverList.contains(expected.trim())) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  /**
   * 校验当前服务器硬件（主板、CPU等）序列号是否在可允许范围内
   */
  private boolean checkSerial(String expectedSerial, String serverSerial) {
    if (StringUtils.isNotBlank(expectedSerial)) {
      return StringUtils.isNotBlank(serverSerial) && expectedSerial.equals(serverSerial);
    }
    return true;
  }

}
