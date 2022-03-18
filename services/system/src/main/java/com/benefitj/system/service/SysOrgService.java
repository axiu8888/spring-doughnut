package com.benefitj.system.service;

import com.benefitj.core.IdUtils;
import com.benefitj.core.SortedTree;
import com.benefitj.scaffold.base.BaseService;
import com.benefitj.scaffold.security.CurrentUserService;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.system.controller.vo.OrgTreeVo;
import com.benefitj.system.mapper.SysOrgMapper;
import com.benefitj.system.model.SysOrgEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 机构
 */
@Service
public class SysOrgService extends BaseService<SysOrgMapper, SysOrgEntity> implements CurrentUserService {

  /**
   * 获取下一个机构的ID
   */
  private String getNextOrgId() {
    String orgId = null;
    for (int i = 0; i < 10000; i++) {
      String id = IdUtils.nextId(12);
      if (countById(id) < 1) {
        orgId = id;
        break;
      }
    }
    if (orgId == null) {
      throw new IllegalStateException("无法获取到机构ID");
    }
    return orgId;
  }

  /**
   * 获取 autoCode
   *
   * @param org 机构
   * @return 返回新的 autoCode
   */
  private String getAutoCode(SysOrgEntity org) {
    String pid = org.getPid();
    return getAutoCode(pid, org.getId(), StringUtils.isNotBlank(pid));
  }

  /**
   * 获取 autoCode
   *
   * @param pid        父机构
   * @param id         机构ID
   * @param allowThrow 如果父机构不存在，是否抛出异常
   * @return 返回新的 autoCode
   */
  private String getAutoCode(String pid, String id, boolean allowThrow) {
    SysOrgEntity parent = getById(pid);
    if (parent == null && allowThrow) {
      throw new IllegalStateException("父级机构不存在");
    }
    String parentAutoCode = parent != null ? parent.getAutoCode() : null;
    return SysOrgEntity.generateAutoCode(parentAutoCode, id);
  }

  /**
   * 通过机构ID查询 autoCode
   *
   * @param id 机构ID
   * @return 返回查询的 autoCode
   */
  public String getAutoCodeById(String id) {
    return getBaseMapper().selectAutoCodeById(id);
  }

  /**
   * 获取机构
   *
   * @param id 机构ID
   * @return 返回查询到的机构
   */
  public SysOrgEntity get(String id) {
    return getBaseMapper().selectById(id);
  }

  /**
   * 创建新的机构
   */
  public SysOrgEntity create(SysOrgEntity org) {
    // 生成ID
    String orgId = getNextOrgId();
    org.setId(orgId);
    // 生成 autoCode
    String autoCode = getAutoCode(org);

    // 检查组织机构的层级，不建议超过10层
    String[] split = autoCode.split(SysOrgEntity.AUTO);
    if (split.length > 10) {
      throw new IllegalStateException("组织机构不能超过超过10层");
    }

    org.setAutoCode(autoCode);
    org.setActive(true);
    getBaseMapper().insert(org);
    return org;
  }

  /**
   * 更新机构
   *
   * @param org 更新的机构
   * @return 返回更新的机构
   */
  public SysOrgEntity update(SysOrgEntity org) {
    SysOrgEntity existOrg = getById(org.getId());
    if (existOrg == null) {
      throw new IllegalArgumentException("无法发现机构");
    }
    existOrg.setName(org.getName());
    existOrg.setCode(org.getCode());
    existOrg.setLogo(org.getLogo());
    super.updateById(existOrg);
    return existOrg;
  }

  /**
   * 改变机构状态
   *
   * @param id     机构ID
   * @param active 状态
   * @return 返回是否更新
   */
  public boolean changeActive(String id, Boolean active) {
    SysOrgEntity org = get(id);
    if (org != null) {
      org.setActive(active != null ? org.getActive() : null);
      return updateById(org);
    }
    return false;
  }

  /**
   * 使用正则表达式匹配符合的AutoCode
   *
   * @param page 分页参数
   * @param n    至少匹配的次数(0 ~ ∞)
   * @param m    之多匹配的次数
   * @return 返回符合的机构
   */
  protected PageInfo<SysOrgEntity> getByAutoCodeRegex(PageRequest<SysOrgEntity> page, int n, int m) {
    SysOrgEntity c = page.getCondition();
    // 重置autoCode
    c.setAutoCode(getAutoCodeById(c.getPid()));
    // ORDER BY
    String orderBy = String.join(",", getOrderByList(page.getOrderBy()));
    // 查询分页
    return PageHelper.startPage(page.getPageNum(), page.getPageSize(), orderBy).doSelectPageInfo(()
        -> getBaseMapper().selectByAutoCodeRegex(
        c, page.getStartTime(), page.getEndTime(), Math.max(0, n), Math.max(n, m)));
  }

  /**
   * 获取组织机构树
   *
   * @param id     机构ID
   * @param active 可用状态
   * @return 返回组织机构树
   */
  public List<SysOrgEntity> getOrgTreeList(String id, @Nullable Boolean active) {
    String autoCode = getAutoCodeById(id);
    if (StringUtils.isBlank(autoCode)) {
      return Collections.emptyList();
    }
    SysOrgEntity org = new SysOrgEntity();
    org.setAutoCode(autoCode);
    org.setActive(active);
    List<SysOrgEntity> list = getBaseMapper().selectByAutoCodeRegex(org, null, null, 0, 10);
    final Map<String, OrgTreeVo> map = new ConcurrentHashMap<>(list.size());
    for (SysOrgEntity o : list) {
      map.put(o.getId(), BeanHelper.copy(o, OrgTreeVo.class));
    }
    Map<String, OrgTreeVo> sort = SortedTree.sort(map);
    return new ArrayList<>(sort.values());
  }

}
