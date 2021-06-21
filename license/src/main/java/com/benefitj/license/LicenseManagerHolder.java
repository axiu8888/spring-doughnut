package com.benefitj.license;

import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

/**
 * de.schlichtherle.license.LicenseManager的单例
 */
public class LicenseManagerHolder {

  private static volatile LicenseManager INSTANCE;

  public static LicenseManager getInstance(LicenseParam param) {
    if (INSTANCE == null) {
      synchronized (LicenseManagerHolder.class) {
        if (INSTANCE == null) {
          INSTANCE = new DefaultLicenseManager(param);
        }
      }
    }
    return INSTANCE;
  }

}
