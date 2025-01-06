package com.ispan.hestia.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.CartRoomDTO;
import com.ispan.hestia.dto.CartRoomRequest;
import com.ispan.hestia.dto.CartRoomResponse;
import com.ispan.hestia.dto.CheckOutRequest;
import com.ispan.hestia.dto.CheckOutResponse;
import com.ispan.hestia.dto.OrderDetailsDTO;
import com.ispan.hestia.model.CartRoom;
import com.ispan.hestia.model.OrderDetails;
import com.ispan.hestia.model.RoomAvailableDate;
import com.ispan.hestia.service.UserService;
import com.ispan.hestia.service.impl.CartRoomService;
import com.ispan.hestia.service.impl.OrderService;
import com.ispan.hestia.service.impl.RoomAvailableDateService;
import com.ispan.hestia.service.impl.RoomService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartRoomService cartRoomService;

    @Autowired
    private RoomAvailableDateService roomAvailableDateService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /* 新增 多筆roomAvailableDate 至 user 的 cart */
    @PostMapping("/addLstToCart")
    public CartRoomResponse addDateLstToCart(@RequestBody CartRoomRequest request, HttpServletRequest req) {
        Integer userId = (Integer) req.getAttribute("userId");
        // request 有內容才新增
        System.out.println(request);
        if (request != null) {

            if (request.dates() != null) {

                boolean insertable = true; // 設置開關，如果當中有遇到不存在的 RoomAvailableDate 變成 flase 就不能新增
                List<RoomAvailableDate> dbRoomAvailableDates = new ArrayList<>();
                for (Date date : request.dates()) {
                    RoomAvailableDate dbRoomAvailableDate = roomAvailableDateService
                            .findRoomAvailableDateroomIdAndDate(request.roomId(), date);

                    if (dbRoomAvailableDate == null) {
                        insertable = false;
                        break;
                    } else {
                        dbRoomAvailableDates.add(dbRoomAvailableDate);
                    }
                }

                // 如果全部都存在且可用狀態 才把全部一次新增到 cart 中
                if (insertable) {

                    for (RoomAvailableDate dbRoomAvailableDate : dbRoomAvailableDates) {
                        cartRoomService.addRoomstoCart(dbRoomAvailableDate, userId);
                    }
                    return new CartRoomResponse(1, null, true, "加入購物車成功！");
                }
            }
        }
        return new CartRoomResponse(1, null, false, "加入購物車失敗！");
    }

    /* 用 roomId 刪除 room Dates 從某 user 的 cart */
    @PostMapping("/deleteRoomDatesFromCart/{roomId}")
    public CartRoomResponse deleteRoomFromCartByRoomId(@PathVariable Integer
    roomId, @RequestBody String entity) {

    if (roomId == null) {
        return new CartRoomResponse(0, null, false, "Id 是必要欄位");
    } else if (!cartRoomService.existsByRoomId(roomId)) {
        return new CartRoomResponse(0, null, false, "Id 不存在");
    } else {
        boolean success = cartRoomService.deleteRoomIdFromCart(roomId, entity);
        if (success) {
            return new CartRoomResponse(0, null, true, "刪除成功");
        }
    }
        return new CartRoomResponse(0, null, false, "刪除失敗");
    }
    
    /* 傳進一個 cartId 查此roomAvailableDate是否可用 */
    @GetMapping("/checkAvailableDate/{cartId}")
    public CartRoomResponse checkAvailableDate(@PathVariable Integer cartId) {
        if (cartId == null) {
            return new CartRoomResponse(0, null, false, "Id 是必要欄位");
        } else if (!cartRoomService.exists(cartId)) {
            return new CartRoomResponse(0, null, false, "Id 不存在");
        } else {
            boolean available = cartRoomService.checkAvailableDate(cartId);
            if (available) {
                return new CartRoomResponse(0, null, true, "is available");
            }
        }
            return new CartRoomResponse(0, null, false, "not available");
        
    }

    /* 傳進一個 roomId 查此roomAvailableDate是否可用 */
    @GetMapping("/checkAvailableDateByRoomId/{roomId}/{date}")
    public CartRoomResponse checkAvailableDate(@PathVariable Integer roomId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        if (roomId == null) {
            return new CartRoomResponse(0, null, false, "Id 是必要欄位");
        } else if (!roomService.exists(roomId)) {
            return new CartRoomResponse(0, null, false, "Id 不存在");
        } else {
            boolean available = cartRoomService.checkAvailableDateByRoomId(roomId, date);
            if (available) {
                return new CartRoomResponse(0, null, true, "is available");
            }
        }
            return new CartRoomResponse(0, null, false, "not available");
        
    }
    
    /* 用 cartId 刪除某 user 的 cart */
    @GetMapping("/deleteCart/{cartId}")
    public CartRoomResponse deleteRoomFromCartByCartId(@PathVariable Integer cartId) {

        if (cartId == null) {
            return new CartRoomResponse(0, null, false, "Id 是必要欄位");
        } else if (!cartRoomService.exists(cartId)) {
            System.out.println("cartId"+cartId);
            return new CartRoomResponse(0, null, false, "Id 不存在");
        } else {
            boolean success = cartRoomService.deleteCartById(cartId);
            if (success) {
                return new CartRoomResponse(0, null, true, "刪除成功");
            }
        }
        return new CartRoomResponse(0, null, false, "刪除失敗");
    }

    /* 查詢 某 user 的 cart */
    @GetMapping("/getCart")
    public CartRoomResponse getUserCart(HttpServletRequest req) {
        Integer userId = (Integer) req.getAttribute("userId");

        if (userId == null) {
            return new CartRoomResponse(0, null, false, "Id 是必要欄位");
        } else if (!userService.exists(userId)) {
            return new CartRoomResponse(0, null, false, "Id 不存在");
        } else {
            // count
            long count = cartRoomService.count(userId);

            // 查
            List<CartRoom> dbCartRooms = cartRoomService.findCartList(userId);
            System.out.println("000000000");

            if (dbCartRooms.size() != 0 && count != 0) {
                System.out.println("111111111");
                List<CartRoomDTO> lst = new ArrayList<>();
                CartRoomDTO dto = null;
                for (CartRoom dbCartRoom : dbCartRooms) {
                    // 將前端要用的資料塞進 dto
                    dto = new CartRoomDTO(
                            dbCartRoom.getCartId(),
                            dbCartRoom.getUser().getUserId(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getRoomId(),
                            dbCartRoom.getRoomAvailableDate().getAvailableDates(),
                            dbCartRoom.getRoomAvailableDate().getPrice(),
                            dbCartRoom.getRoomAvailableDate().getRoomSum(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getRoomName(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getRoomSize(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getDoubleBed(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getSingleBed(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getBathroom(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getBedroomCount(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getCity().getCityId(),
                            dbCartRoom.getRoomAvailableDate().getRoom().getCity().getCityName());

                    // 將 dto 塞進list
                    lst.add(dto);
                }

                return new CartRoomResponse(count, lst, true, "查詢成功！");
            }
            return new CartRoomResponse(0, null, false, "尚無商品在購物車內");
        }
    }

    // checkOut Cart selected items
    @PostMapping("/checkOutCart")
    public CheckOutResponse postMethodName(@RequestBody String entity, HttpServletRequest req) {
        Integer userId = (Integer) req.getAttribute("userId");
        
        JSONArray request = new JSONArray(entity);

        if(request == null || request.isEmpty()){
            return new CheckOutResponse(0, null, false,"request == null");
        }
        else{
            List<OrderDetails> list = orderService.checkOut(request, userId);
            if(list!=null && list.size()!=0){

                List<OrderDetailsDTO> lst = list.stream()
                .map(od -> new OrderDetailsDTO(
                    od.getOrder().getOrderId(), 
                    od.getOrderRoomId(), 
                    od.getCheckInDate(), 
                    od.getRoomAvailableDate().getRoom().getRoomName(),
                    od.getPurchasedPrice(),
                    od.getRoomAvailableDate().getAvailableDates(),
                    od.getOrder().getDate(),
                    od.getRoomAvailableDate().getRoom().getSingleBed(),
                    od.getRoomAvailableDate().getRoom().getDoubleBed(),
                    od.getRoomAvailableDate().getRoom().getBedroomCount(),
                    od.getRoomAvailableDate().getRoom().getCheckinTime(),
                    od.getRoomAvailableDate().getRoom().getCheckoutTime(),
                    od.getRoomAvailableDate().getRoom().getState().getStateContent(),
                    od.getRoomAvailableDate().getRoom().getState().getStateId(),
                    od.getActiveRefundRequest(),
                    od.getRoomAvailableDate().getRoom().getMainImage(),
                    od.getRoomAvailableDate().getRoom().getRoomAddr(),
                    od.getRoomAvailableDate().getRoom().getCity().getCityName(),
                    od.getRoomAvailableDate().getRoom().getRoomNotice(),
                    od.getRoomAvailableDate().getRoom().getProvider().getUser().getName(),
                    od.getRoomAvailableDate().getRoom().getProvider().getUser().getEmail())).collect(Collectors.toList());
                    return new CheckOutResponse(0, lst, true,"下單成功");
            }
        }
        return new CheckOutResponse(0, null, false,"預定失敗，請重新檢查商品");

    }
    

    // [edit] 傳進一日期 List，比較她與原先 cart 中的 roomId 是否擁有同一日期 List，將多的刪除，少的新增
    @PostMapping("/editCart")
    public CartRoomResponse editCartDate(@RequestBody CartRoomRequest request, HttpServletRequest req){
        Integer userId = (Integer) req.getAttribute("userId");
        
        if (request.roomId() == null) {
            return new CartRoomResponse(0, null, false, "Id 是必要欄位");
        } else if (!roomService.exists(request.roomId())) {
            return new CartRoomResponse(0, null, false, "Id 不存在");
        } else {
            List<CartRoom> cartList = cartRoomService.editCartDate(request.roomId(), userId, request.dates());
            if (cartList!= null && cartList.size() != 0) {
                List<CartRoomDTO> lst = cartList.stream()
                .map(cart -> new CartRoomDTO(
                    cart.getCartId(), 
                    cart.getUser().getUserId(), 
                    cart.getRoomAvailableDate().getRoom().getRoomId(), 
                    cart.getRoomAvailableDate().getAvailableDates(), 
                    cart.getRoomAvailableDate().getPrice(),
                    cart.getRoomAvailableDate().getRoomSum(), 
                    cart.getRoomAvailableDate().getRoom().getRoomName(), 
                    cart.getRoomAvailableDate().getRoom().getRoomSize(),
                    cart.getRoomAvailableDate().getRoom().getDoubleBed(),
                    cart.getRoomAvailableDate().getRoom().getSingleBed(),
                    cart.getRoomAvailableDate().getRoom().getBathroom(),
                    cart.getRoomAvailableDate().getRoom().getBedroomCount(),
                    cart.getRoomAvailableDate().getRoom().getCity().getCityId(),
                    cart.getRoomAvailableDate().getRoom().getCity().getCityName()))
                .collect(Collectors.toList());
                return new CartRoomResponse(0, lst, true, "修改成功");
            }
        }
        return new CartRoomResponse(0, null, false, "刪除失敗");
    }

    // private List<Date> getAllDatesInTheDateRange(Date startDate, Date endDate) {
    // List<Date> dateList = new ArrayList<>();

    // // 开始时间必须小于结束时间
    // if (startDate.after(endDate)) {
    // return null;
    // }

    // Calendar calendar = Calendar.getInstance();
    // calendar.setTime(startDate);

    // while (calendar.getTime().before(endDate)) {
    // dateList.add(calendar.getTime());
    // calendar.add(Calendar.DATE, 1); // 加一天
    // }
    // // dateList.add(endDate); // 添加结束日期
    // return dateList;
    // }

}
