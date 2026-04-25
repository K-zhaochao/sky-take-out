package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 套餐与菜品关系表
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据id批量查询setmealId【套餐与菜品关系表】
     * @param dishIds
     * @return
     */
    List<Long> getsetmealIdsByDishIds(List<Long> dishIds);
}
