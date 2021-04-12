package com.benefitj.spring.registrar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * A method annotated with {@link Annotation}, together with the annotations.
 */
public class MethodElement {

  private Method method; // NOSONAR
  private Annotation[] annotations; // NOSONAR

  public MethodElement(Method method, Annotation[] annotations) { // NOSONAR
    this.method = method;
    this.annotations = annotations;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public Annotation[] getAnnotations() {
    return annotations;
  }

  public void setAnnotations(Annotation[] annotations) {
    this.annotations = annotations;
  }

}
