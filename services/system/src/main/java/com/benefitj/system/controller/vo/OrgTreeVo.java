package com.benefitj.system.controller.vo;

import com.benefitj.core.SortedTree;
import com.benefitj.system.model.SysOrgEntity;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 组织机构树
 */
public class OrgTreeVo extends SysOrgEntity implements SortedTree.Tree<String, OrgTreeVo> {

  private final Set<OrgTreeVo> children = new LinkedHashSet<>();

  @Override
  public String getParentId() {
    return getPid();
  }

  @Override
  public void setParentId(String pid) {
    setPid(pid);
  }

  @Override
  public Set<OrgTreeVo> getChildren() {
    return children;
  }

}
