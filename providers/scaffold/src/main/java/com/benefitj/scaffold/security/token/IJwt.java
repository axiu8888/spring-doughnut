package com.benefitj.scaffold.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface IJwt<B extends Claims> extends Jwt<Header, B>, Claims {

  @Override
  Header getHeader();

  @Override
  B getBody();

  @Override
  default String getIssuer() {
    return getBody().getIssuer();
  }

  @Override
  default Claims setIssuer(String iss) {
    return getBody().setIssuer(iss);
  }

  @Override
  default String getSubject() {
    return getBody().getSubject();
  }

  @Override
  default Claims setSubject(String sub) {
    return getBody().setSubject(sub);
  }

  @Override
  default String getAudience() {
    return getBody().getAudience();
  }

  @Override
  default Claims setAudience(String aud) {
    return getBody().setAudience(aud);
  }

  @Override
  default Date getExpiration() {
    return getBody().getExpiration();
  }

  @Override
  default Claims setExpiration(Date exp) {
    return getBody().setExpiration(exp);
  }

  @Override
  default Date getNotBefore() {
    return getBody().getNotBefore();
  }

  @Override
  default Claims setNotBefore(Date nbf) {
    return getBody().setNotBefore(nbf);
  }

  @Override
  default Date getIssuedAt() {
    return getBody().getIssuedAt();
  }

  @Override
  default Claims setIssuedAt(Date iat) {
    return getBody().setIssuedAt(iat);
  }

  @Override
  default String getId() {
    return getBody().getId();
  }

  @Override
  default Claims setId(String jti) {
    return getBody().setId(jti);
  }

  @Override
  default <T> T get(String claimName, Class<T> requiredType) {
    return getBody().get(claimName, requiredType);
  }


  @Override
  default int size() {
    return getBody().size();
  }

  @Override
  default boolean isEmpty() {
    return getBody().isEmpty();
  }

  @Override
  default boolean containsKey(Object key) {
    return getBody().containsKey(key);
  }

  @Override
  default boolean containsValue(Object value) {
    return getBody().containsValue(value);
  }

  @Override
  default Object get(Object key) {
    return getBody().get(key);
  }

  @Nullable
  @Override
  default Object put(String key, Object value) {
    return getBody().put(key, value);
  }

  @Override
  default Object remove(Object key) {
    return getBody().remove(key);
  }

  @Override
  default void putAll(@NotNull Map<? extends String, ?> m) {
    getBody().putAll(m);
  }

  @Override
  default void clear() {
    getBody().clear();
  }

  @NotNull
  @Override
  default Set<String> keySet() {
    return getBody().keySet();
  }

  @NotNull
  @Override
  default Collection<Object> values() {
    return getBody().values();
  }

  @NotNull
  @Override
  default Set<Map.Entry<String, Object>> entrySet() {
    return getBody().entrySet();
  }
}
