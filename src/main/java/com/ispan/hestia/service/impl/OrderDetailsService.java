package com.ispan.hestia.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.model.OrderDetails;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.repository.OrderDetailsRepository;
import com.ispan.hestia.util.DateUtil;

@Service
public class OrderDetailsService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepo;

    public boolean exists(Integer id) {
        if (id != null) {
            return orderDetailsRepo.existsById(id);
        }
        return false;
    }

    public Set<Room> findOrderRoom(Integer orderId) {
        if (orderId != null) {
            List<OrderDetails> dbOrderDetails = orderDetailsRepo.findByOrderId(orderId);

            if (dbOrderDetails != null && dbOrderDetails.size() != 0) {
                Set<Room> rooms = new HashSet<>();

                for (OrderDetails dbOrderDetail : dbOrderDetails) {
                    rooms.add(dbOrderDetail.getRoomAvailableDate().getRoom());
                }
                return rooms;
            }
        }
        return null;
    }

    public List<Date> findOrderRoomDate(Integer orderId, Integer roomId) {
        if (orderId != null && roomId != null) {
            List<OrderDetails> dbOrderDetails = orderDetailsRepo.findByOrderIdandRoomId(orderId, roomId);

            if (dbOrderDetails != null && dbOrderDetails.size() != 0) {
                List<Date> dates = new ArrayList<>();
                for (OrderDetails dbOrderDetail : dbOrderDetails) {
                    dates.add(dbOrderDetail.getRoomAvailableDate().getAvailableDates());
                }

                if (dates != null && dates.size() != 0) {

                    // 串成一個只有 start, end 的 list
                    List<Date> startAndEnd = new ArrayList<>();

                    // 使用 sort 方法進行排序
                    Collections.sort(dates);

                    // startdate 是 dates 的第一個
                    startAndEnd.add(dates.get(0));
                    // 如果只有一天，endDate 自動加一
                    if (dates.size() == 1) {
                        startAndEnd.add(DateUtil.getNextDay(dates.get(0)));
                    } else {
                        startAndEnd.add(DateUtil.getNextDay(dates.get(dates.size() - 1)));
                    }
                    return startAndEnd;
                }
            }
        }
        return null;
    }

}
