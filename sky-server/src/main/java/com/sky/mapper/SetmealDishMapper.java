package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
     * 根据id获取套餐与菜品的数据
     * @param setmealId
     * @return
     */
    @Select("select * from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getsetmealIdsByDishId(Long setmealId);

    /**
     * 批量保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据id批量删除套餐与菜品的对应关系
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据id删除套餐与菜品的对应关系
     * @param setmealId
     */
    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

}
