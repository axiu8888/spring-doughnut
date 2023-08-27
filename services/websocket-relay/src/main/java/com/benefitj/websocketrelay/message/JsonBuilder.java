package com.benefitj.websocketrelay.message;

import com.alibaba.fastjson2.JSONObject;

import java.util.LinkedHashMap;

public class JsonBuilder extends JSONObject {

  public static JsonBuilder build() {
    return new JsonBuilder();
  }

  public JsonBuilder() {
    super(new LinkedHashMap<>());
  }

  public JsonBuilder set(String ket, Object value) {
    put(ket, value);
    return this;
  }

}
