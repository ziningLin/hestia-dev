package com.ispan.hestia.controller;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.OrderDetailsResponse;
import com.ispan.hestia.dto.OrderDetailsRoomDTO;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.service.impl.OrderDetailsService;
import com.ispan.hestia.service.impl.OrderService;

@RestController
@RequestMapping("/orderDetails")
@CrossOrigin
public class OrderDetailsController {

    @Autowired
    private OrderDetailsService orderDetailsService;

    @Autowired
    private OrderService orderService;

    /* find order room by orderId */
    @GetMapping("/findOrderRoom/{orderId}")
    public OrderDetailsResponse findOrderRooms(@PathVariable Integer orderId) {
        if(orderId == null) return new OrderDetailsResponse(null, false, "Id is necessary");
        else if ( !orderService.exists(orderId)) return new OrderDetailsResponse(null, false,"orderId do not exist");
        else{
            Set<Room> rooms = orderDetailsService.findOrderRoom(orderId);

            if (rooms != null && rooms.size() != 0) {
                List<OrderDetailsRoomDTO> lst = rooms.stream()
                .map(room -> new OrderDetailsRoomDTO(
                    room.getRoomId(),
                    room.getRoomName(),
                    orderDetailsService.findOrderRoomDate(orderId, room.getRoomId()).get(0),
                    orderDetailsService.findOrderRoomDate(orderId, room.getRoomId()).get(1),
                    room.getRoomSize(),
                    room.getBedroomCount()))
                    .collect(Collectors.toList());
                return new OrderDetailsResponse(lst, true, "查詢成功！");
            }
        }
        return new OrderDetailsResponse(null, true, "查詢失敗！");
    }

}
