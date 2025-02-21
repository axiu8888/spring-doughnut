package com.benefitj.spring.mvc.jsonbody;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

public interface JsonBodyProcessor {

  JSONObject process(InputStream in);


  static JSONObject toJson(InputStream in) {
    String str = IOUtils.readFully(in).toString();
    return StringUtils.isNotBlank(str) ? JSON.parseObject(str) : new JSONObject();
  }

}
