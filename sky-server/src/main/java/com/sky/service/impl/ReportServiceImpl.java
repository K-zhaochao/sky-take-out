package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end范围内(包括begin和end)的日期
        List<LocalDate> dataList = new ArrayList<>();
        dataList.add(begin);
        LocalDate currentDate = begin.plusDays(1);
        while (!currentDate.isAfter(end)) {
            dataList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

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
}
