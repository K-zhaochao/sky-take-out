package com.sky.mapper;

import com.sky.entity.SetmealDish;
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

    /**
     * 批量保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 删除套餐与菜品的对应关系
     * @param setmealIds
     */
    void deleteBySetmealId(List<Long> setmealIds);
}
