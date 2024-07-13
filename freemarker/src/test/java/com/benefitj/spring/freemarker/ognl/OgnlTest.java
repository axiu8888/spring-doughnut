package com.benefitj.spring.freemarker.ognl;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OgnlTest  {

  @BeforeEach
  public void setUp() {
  }

  @Test
  public void testOgnl() throws OgnlException {
    // 创建context对象
    OgnlContext context = new OgnlContext(null, null, new DefaultMemberAccess(false));
    // 利用context来获取root对象
    Object root = context.getRoot();
    // Ognl中的静态方法getValue(expression, context, root, resultType)可以用来获取数据
    Object value = Ognl.getValue("'helloworld'.length()", context, root);  //expression就是方法表达式
    System.out.println(value.toString());
  }

  @Test
  public void testIn() throws OgnlException {
    OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
    Object node = Ognl.parseExpression("#name in {\"Greenland\", \"Austin\", \"Africa\", \"Rome\"}");
    Object root = null;

    context.put("name", "Austin");
    assertEquals(Boolean.TRUE, Ognl.getValue(node, context, root));
  }

  @Test
  public void testList() throws OgnlException {
    OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
    Object node = Ognl.parseExpression("#{ \"foo\" : \"foo value\", \"bar\" : \"bar value\" }\n");
    Object root = null;
    System.err.println("node: " + Ognl.getValue(node, context, root));;
  }

  @Test
  public void testProperties() throws OgnlException {
    OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
    Object node = Ognl.parseExpression("base-url=http://#remote-address:80/api/");
    context.put("remote", "192.168.1.198");

    Object root = null;
    System.err.println("node: " + Ognl.getValue(node, context, root));;
  }

  public void tearDown() throws Exception {
  }
}