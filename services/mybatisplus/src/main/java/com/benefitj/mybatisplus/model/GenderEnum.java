package com.benefitj.mybatisplus.model;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 性别
 */
public enum GenderEnum implements IEnum<String> {
    MALE,
    FEMALE;

    @Override
    public String getValue() {
        return name().toLowerCase();
    }
}
