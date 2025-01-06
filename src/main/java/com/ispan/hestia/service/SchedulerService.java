package com.ispan.hestia.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ispan.hestia.service.impl.OrderService;

@Service
public class SchedulerService {

    @Autowired
    OrderService orderService;

    // @Scheduled(fixedRate = 1000)
    public void cancelOrder() {
        orderService.automaticallyCancelOrders();
        System.out.println("成功執行 時間:" + new Date());
    }
}
