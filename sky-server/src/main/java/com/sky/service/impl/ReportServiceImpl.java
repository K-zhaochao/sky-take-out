package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end范围内(包括begin和end)的日期
        List<LocalDate> dataList = getDateList(begin, end);

        /* 自己想的转换方式
            StringJoiner stringJoiner = new StringJoiner(",");
            dataList.forEach(data -> {
                stringJoiner.add(data.toString());
            });
            String dateList = stringJoiner.toString();
         */

        // 查询对应日期的营业额
        List<Double> turnoverList =  new ArrayList<>();
        dataList.forEach(date->{
            // 查询date日期对应的营业额数据，营业额是指: 状态为“已完成”的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 封装传入的参数到Map
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0 : turnover;

            // 添加至营业额集合
            turnoverList.add(turnover);
        });

        // 封装返回结果
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内的用户数据
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end范围内(包括begin和end)的日期
        List<LocalDate> dataList = getDateList(begin, end);

        // 查询对应日期的用户数据: 用户总量, 新增用户量
        List<Integer> totalUserList =  new ArrayList<>();
        List<Integer> newUserList =  new ArrayList<>();
        LocalDateTime beforeBeginTime = LocalDateTime.of(begin.minusDays(1), LocalTime.MAX);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("end", beforeBeginTime);
        Integer lastTotalUser = userMapper.countByMap(paramMap);
        lastTotalUser = lastTotalUser == null ? 0 : lastTotalUser;

        for (LocalDate date : dataList) {
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 封装 Map 传参
            paramMap.put("end", endTime);
            Integer totalUser = userMapper.countByMap(paramMap);
            totalUser = totalUser == null ? 0 : totalUser;

            // 利用上一轮的累计量算出当天新增
            Integer newUser = totalUser - lastTotalUser;

            totalUserList.add(totalUser);
            newUserList.add(newUser);

            // 更新 lastTotalUser，供下一轮循环使用
            lastTotalUser = totalUser;
        }

        // 封装返回结果
        return UserReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dataList = getDateList(begin, end);

        // 记录从begin到end每一天的订单数目
        List<Integer> orderCountList =   new ArrayList<>();
        // 记录从begin到end每一天的有效订单数目
        List<Integer> validOrderCountList  =  new ArrayList<>();
        // 记录总的订单数
        Integer totalOrderCount = 0;
        // 记录总的有效订单数
        Integer validOrderCount = 0;

        // 开始查询每一日的订单数目
        for (LocalDate date : dataList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            // 查询总的订单数目
            Integer orderCountTemp = orderMapper.countByMap(map);
            orderCountTemp = orderCountTemp == null ? 0 : orderCountTemp;
            totalOrderCount = totalOrderCount + orderCountTemp;
            orderCountList.add(orderCountTemp);

            map.put("status", Orders.COMPLETED);

            // 查询有效订单数
            Integer validOrderCountTemp = orderMapper.countByMap(map);
            validOrderCountTemp = validOrderCountTemp == null ? 0 : validOrderCountTemp;
            validOrderCount = validOrderCount + validOrderCountTemp;
            validOrderCountList.add(validOrderCountTemp);
        }

        Double orderCompletionRate = null;
        try {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dataList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .build();
    }

    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        LocalDate currentDate = begin.plusDays(1);
        while (!currentDate.isAfter(end)) {
            dataList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return dataList;
    }
}
