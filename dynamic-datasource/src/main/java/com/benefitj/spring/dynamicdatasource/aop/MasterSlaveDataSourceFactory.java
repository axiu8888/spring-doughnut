package com.benefitj.spring.dynamicdatasource.aop;

import com.benefitj.spring.dynamicdatasource.DataSourceFactory;

import javax.sql.DataSource;

/**
 * 主从数据源工厂
 */
public class MasterSlaveDataSourceFactory implements DataSourceFactory {

  /**
   * 主数据源
   */
  private DataSource masterDataSource;
  /**
   * 从数据源
   */
  private DataSource slaveDataSource;
  /**
   * 自定义数据源
   */
  private DataSource customDataSource;

  public MasterSlaveDataSourceFactory(DataSource masterDataSource,
                                      DataSource slaveDataSource,
                                      DataSource customDataSource) {
    this.masterDataSource = masterDataSource;
    this.slaveDataSource = slaveDataSource;
    this.customDataSource = customDataSource;
  }

  @Override
  public DataSource create(Object lookupKey) throws Exception {
    MethodType type = (MethodType) lookupKey;
    if (type == MethodType.MASTER) {
      return getMasterDataSource();
    }
    else if (type == MethodType.SLAVE) {
      return getSlaveDataSource();
    }
    return getCustomDataSource();
  }


  public DataSource getMasterDataSource() {
    return masterDataSource;
  }

  public void setMasterDataSource(DataSource masterDataSource) {
    this.masterDataSource = masterDataSource;
  }

  public DataSource getSlaveDataSource() {
    return slaveDataSource;
  }

  public void setSlaveDataSource(DataSource slaveDataSource) {
    this.slaveDataSource = slaveDataSource;
  }

  public DataSource getCustomDataSource() {
    return customDataSource;
  }

  public void setCustomDataSource(DataSource customDataSource) {
    this.customDataSource = customDataSource;
  }
}
