package com.benefitj.system.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.WeakHashMap;

public class MessageDigestHash {

  public static final MessageDigestHash MD5 = new MessageDigestHash("MD5");

  private final MessageDigest md;

  private final Map<String, Integer> hashCache = new WeakHashMap<>();

  public MessageDigestHash(String algorithm) {
    try {
      this.md = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public int hash(String key) {
    int h;
    h = hashCache.getOrDefault(key, 0);
    if (h == 0) {
      synchronized (this) {
        md.reset();
        md.update(key.getBytes());
        byte[] digest = md.digest();
        for (int i = 0; i < 4; i++) {
          h <<= 8;
          h |= (digest[i]) & 0xFF;
        }
        hashCache.put(key, h);
      }
    }
    return h;
  }

  public String getAlgorithm() {
    return md.getAlgorithm();
  }

}
