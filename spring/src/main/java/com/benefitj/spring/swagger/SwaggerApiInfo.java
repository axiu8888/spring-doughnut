package com.benefitj.spring.swagger;

import org.checkerframework.checker.units.qual.C;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "swagger.api-info")
public class SwaggerApiInfo {

  /**
   * 版本
   */
  private String version;
  /**
   * 标题
   */
  private String title;
  /**
   * 描述
   */
  private String description;
  /**
   * 法律服务地址
   */
  private String termsOfServiceUrl;
  /**
   * 证书
   */
  private String license;
  /**
   * 证书地址
   */
  private String licenseUrl;
  /**
   * 联系人信息
   */
  private Contact contact = new Contact();
  /**
   * 商标
   */
  private List<VendorExtension> vendorExtensions = Collections.emptyList();

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTermsOfServiceUrl() {
    return termsOfServiceUrl;
  }

  public void setTermsOfServiceUrl(String termsOfServiceUrl) {
    this.termsOfServiceUrl = termsOfServiceUrl;
  }

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public String getLicenseUrl() {
    return licenseUrl;
  }

  public void setLicenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
  }

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }

  public void setVendorExtensions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions = vendorExtensions;
  }

  public static class Contact {
    /**
     * 联系人姓名
     */
    private String name;
    /**
     * 联系人地址
     */
    private String url;
    /**
     * 联系人e-mail
     */
    private String email;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }

  /**
   * 商标
   */
  public static class VendorExtension {
    /**
     * 商标名称
     */
    private String name;
    /**
     * 商标地址
     */
    private String url;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }
}
