package com.benefitj.system.service;

import com.benefitj.core.IdUtils;
import com.benefitj.system.mapper.SysMenuMapper;
import com.benefitj.system.model.SysMenuEntity;
import com.benefitj.scaffold.base.BaseService;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.benefitj.scaffold.security.token.JwtTokenManager.currentOrgId;

/**
 * 菜单
 */
@Service
public class SysMenuService extends BaseService<SysMenuMapper, SysMenuEntity> {

  /**
   * 创建菜单
   *
   * @param menu 菜单信息
   */
  public SysMenuEntity create(SysMenuEntity menu) {
    menu.setId(IdUtils.uuid());
    if (StringUtils.isBlank(menu.getOrgId())) {
      menu.setOrgId(currentOrgId());
    }
    menu.setActive(Boolean.TRUE);
    getBaseMapper().insert(menu);
    return menu;
  }

  /**
   * 更新菜单
   *
   * @param menu 菜单信息
   * @return 返回更新的数据
   */
  public SysMenuEntity update(SysMenuEntity menu) {
    SysMenuEntity existMenu = getById(menu.getId());
    if (existMenu == null) {
      throw new IllegalStateException("无法发现菜单");
    }
    existMenu.setName(menu.getName());
    existMenu.setRemarks(menu.getRemarks());
    getBaseMapper().updateById(existMenu);
    return existMenu;
  }

  /**
   * 删除菜单
   *
   * @param id 菜单ID
   * @return 返回删除条数，如果被删除成功，应该返回 1, 否则返回 0
   */
  public int delete(String id) {
    SysMenuEntity menu = getById(id);
    if (menu != null) {
      return getBaseMapper().deleteById(menu.getId());
    }
    return 0;
  }

  /**
   * 改变菜单可用状态
   *
   * @param id     菜单ID
   * @param active 状态
   * @return 返回是否更新
   */
  public boolean changeActive(String id, Boolean active) {
    SysMenuEntity menu = getById(id);
    if (menu != null) {
      menu.setActive(active != null ? active : menu.getActive());
      return updateById(menu);
    }
    return false;
  }

}
