package com.benefitj.license;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LicenseApplicationTests {


  @Test
  public void testGenerate() {
    WindowsServerInfos infos = new WindowsServerInfos();
    System.err.println("getSerialNumber: " + infos.getSerialNumber());
    System.err.println("getCPUSerial: " + infos.getCPUSerial());
    System.err.println("getMainBoardSerial: " + infos.getMainBoardSerial());

    String serial = infos.getSerialNumber();
    LicenseCreatorProperty conf = new LicenseCreatorProperty();
    conf.getLicenseModel().setSerialNumber(serial);
    LicenseCreator licenseCreator = new LicenseCreator(conf);
    boolean result = licenseCreator.generateLicense();
  }


  @Test
  public void testVerify() {
    LicenseVerifyProperty param = new LicenseVerifyProperty();
    LicenseVerify licenseVerify = new LicenseVerify();
    //安装证书
    licenseVerify.install(param);
    System.out.println("++++++++ 证书安装结束 ++++++++");
    LicenseVerify licenseVerify1 = new LicenseVerify();
    //校验证书是否有效GuardTaskServiceImpl
    boolean verifyResult = licenseVerify1.verify();
    if (!verifyResult) {
      System.out.println("证书无效");
    }
  }

}
