package com.benefitj.spring;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.ref.SoftReference;

public class JsonUtils {

  private static final ThreadLocal<SoftReference<ObjectMapper>> localMapper = new ThreadLocal<>();



}
