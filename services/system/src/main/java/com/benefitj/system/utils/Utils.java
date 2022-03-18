package com.benefitj.system.utils;


import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.system.model.IOrgEntity;
import org.apache.commons.lang3.StringUtils;

public class Utils {

  public static void setOrgId(IOrgEntity condition) {
    condition.setOrgId(StringUtils.isNotBlank(condition.getOrgId())
        ? condition.getOrgId() : JwtTokenManager.currentOrgId());
  }


}
