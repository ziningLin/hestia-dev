package com.ispan.hestia.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.CommentDTO;
import com.ispan.hestia.dto.RoomAvailableDateDTO;
import com.ispan.hestia.dto.RoomAvailableDateReponse;
import com.ispan.hestia.dto.RoomAvailableDatesDTO;
import com.ispan.hestia.dto.RoomResponse;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomAvailableDate;
import com.ispan.hestia.service.impl.RoomAvailableDateService;
import com.ispan.hestia.service.impl.RoomService;

@RestController
@RequestMapping("/room-available-date")
@CrossOrigin
public class RoomAvailableDateController {

    @Autowired
    private RoomAvailableDateService roomAvailableDateService;
    @Autowired
    private RoomService roomService;

    /* 多筆查詢: 根據 roomId 查詢所有狀態的 Room Available Date */
    @PostMapping("/provider/findByRoom/{roomId}")
    public RoomAvailableDateReponse findProviderRooms(@PathVariable Integer roomId) {
        // 檢查是否真的有此 ProviderId
        if (roomId == null) {
            return new RoomAvailableDateReponse(0, null, false, "Id是必要欄位");
        } else if (!roomService.exists(roomId)) {
            return new RoomAvailableDateReponse(0, null, false, "Id不存在");
        } else {

            // 組合成一個 JSONObject 去 RoomAvailableDateService 找筆數
            String jsonObj = "{ \"room\":" + roomId + "}";
            // long count = roomService.count(jsonObj);

            Set<RoomAvailableDate> dbRoomDates = roomAvailableDateService.findRoomDates(roomId);
            if (dbRoomDates != null && dbRoomDates.size() != 0) {
                List<RoomAvailableDateDTO> lst = dbRoomDates.stream()
                        .map(roomAvailableDate -> new RoomAvailableDateDTO(
                                roomAvailableDate.getId(),
                                roomAvailableDate.getRoom().getRoomId(),
                                roomAvailableDate.getAvailableDates() != null
                                        ? List.of(roomAvailableDate.getAvailableDates())
                                        : Collections.emptyList(),
                                roomAvailableDate.getPrice(),
                                roomAvailableDate.getRoomSum(),
                                roomAvailableDate.getLatestBookingDate(),
                                roomAvailableDate.getReleaseDate(),
                                roomAvailableDate.getState().getStateId()))
                        .collect(Collectors.toList());
                return new RoomAvailableDateReponse(0, lst, true, "查詢成功");
            }
        }
        return new RoomAvailableDateReponse(0, null, false, "查詢失敗");
    }

    /* 新增 Available Date */
    @PostMapping("provider/insert")
    public RoomAvailableDateReponse insertDates(@RequestBody RoomAvailableDateDTO dateRequest) {
        List<RoomAvailableDate> inserted = roomAvailableDateService.insertDates(dateRequest);
        if (inserted != null && inserted.size() != 0) {
            List<RoomAvailableDateDTO> lst = inserted.stream()
                    .map(roomAvailableDate -> new RoomAvailableDateDTO(
                            roomAvailableDate.getId(),
                            roomAvailableDate.getRoom().getRoomId(),
                            roomAvailableDate.getAvailableDates() != null
                                    ? List.of(roomAvailableDate.getAvailableDates())
                                    : Collections.emptyList(),
                            roomAvailableDate.getPrice(),
                            roomAvailableDate.getRoomSum(),
                            roomAvailableDate.getLatestBookingDate(),
                            roomAvailableDate.getReleaseDate(),
                            roomAvailableDate.getState().getStateId()))
                    .collect(Collectors.toList());

            return new RoomAvailableDateReponse(1, lst, true, "新增成功");
        } else {
            return new RoomAvailableDateReponse(0, null, false, "新增失敗");
        }
    }

    /* 修改 Available Date */
    @PutMapping("/provider/modify/{roomId}")
    public RoomResponse modifyDates(@PathVariable Integer roomId, @RequestBody RoomAvailableDateDTO dateRequest) {
        if (roomId == null) {
            return new RoomResponse(0, null, false, "id是必要欄位");
        } else if (!roomService.exists(roomId)) {
            return new RoomResponse(0, null, false, "資料不存在");
        } else {
            List<RoomAvailableDate> modified = roomAvailableDateService.modifyDates(roomId, dateRequest);
            if (modified != null && modified.size() != 0) {
                List<RoomAvailableDateDTO> lst = modified.stream()
                        .map(roomAvailableDate -> new RoomAvailableDateDTO(
                                roomAvailableDate.getId(),
                                roomAvailableDate.getRoom().getRoomId(),
                                roomAvailableDate.getAvailableDates() != null
                                        ? List.of(roomAvailableDate.getAvailableDates())
                                        : Collections.emptyList(),
                                roomAvailableDate.getPrice(),
                                roomAvailableDate.getRoomSum(),
                                roomAvailableDate.getLatestBookingDate(),
                                roomAvailableDate.getReleaseDate(),
                                roomAvailableDate.getState().getStateId()))
                        .collect(Collectors.toList());
                return new RoomResponse(1, lst, true, "修改成功");
            } else {
                return new RoomResponse(0, null, false, "修改失敗");
            }
        }
    }

    /* 刪除一筆 Available Date */
    @DeleteMapping("/provider/remove/{roomAvailableDateId}")
    public RoomResponse removeDates(@PathVariable Integer roomAvailableDateId) {
        if (roomAvailableDateId == null) {
            return new RoomResponse(0, null, false, "id是必要欄位");
        } else if (!roomAvailableDateService.exists(roomAvailableDateId)) {
            return new RoomResponse(0, null, false, "id不存在");
        } else {
            if (roomAvailableDateService.removeDates(roomAvailableDateId)) {
                return new RoomResponse(1, null, true, "刪除成功");
            } else {
                return new RoomResponse(0, null, false, "刪除失敗");
            }
        }
    }

    // 列出某個房間所有可提供日期和價格
    @GetMapping("/dates&prices/{roomId}")
    public List<Object[]> getDatesAndPricesByRoomId(@PathVariable Integer roomId) {
        return roomAvailableDateService.findAllAvailableDatesAndPricesByRoomId(roomId);
    }

    // 查詢某 roomId 的日期區間是否可用
    @PostMapping("/isAvailable/{roomId}")
    public RoomResponse isRoomAvailableInDates(@PathVariable Integer roomId, @RequestBody String entity) {
        if (roomId == null) {
            return new RoomResponse(0, null, false, "id是必要欄位");
        } else if (!roomService.exists(roomId)) {
            return new RoomResponse(0, null, false, "id不存在");
        } else {
            List<RoomAvailableDate> lst = roomAvailableDateService.findIsRoomAvailableInDates(roomId, entity);

            if (lst != null && lst.size() != 0) {
                List<RoomAvailableDatesDTO> list = lst.stream()
                        .map(roomAvailableDate -> new RoomAvailableDatesDTO(
                                roomAvailableDate.getId(),
                                roomAvailableDate.getRoom().getRoomId(),
                                roomAvailableDate.getAvailableDates(),
                                roomAvailableDate.getPrice(),
                                roomAvailableDate.getRoomSum(),
                                roomAvailableDate.getRoom().getRoomName(),
                                roomAvailableDate.getRoom().getRoomSize(),
                                roomAvailableDate.getRoom().getDoubleBed(),
                                roomAvailableDate.getRoom().getSingleBed(),
                                roomAvailableDate.getRoom().getBathroom(),
                                roomAvailableDate.getRoom().getBedroomCount(),
                                roomAvailableDate.getRoom().getCity().getCityId(),
                                roomAvailableDate.getRoom().getCity().getCityName()))
                        .collect(Collectors.toList());
                return new RoomResponse(0, list, true, "查詢成功");
            }
            return new RoomResponse(0, null, false, "日期區間皆不可用");
        }
    }

    // 查詢某 roomId 近 15 個月的可用日期
    @GetMapping("/RecentAvailableDates/{roomId}")
    public RoomResponse findRecentRoomAvailableDates(@PathVariable Integer roomId) {
        if (roomId == null) {
            return new RoomResponse(0, null, false, "id是必要欄位");
        } else if (!roomService.exists(roomId)) {
            return new RoomResponse(0, null, false, "id不存在");
        } else {
            List<RoomAvailableDate> lst = roomAvailableDateService.findRecentRoomAvailableDates(roomId);

            if (lst != null && lst.size() != 0) {
                List<RoomAvailableDatesDTO> list = lst.stream()
                        .map(roomAvailableDate -> new RoomAvailableDatesDTO(
                                roomAvailableDate.getId(),
                                roomAvailableDate.getRoom().getRoomId(),
                                roomAvailableDate.getAvailableDates(),
                                roomAvailableDate.getPrice(),
                                roomAvailableDate.getRoomSum(),
                                roomAvailableDate.getRoom().getRoomName(),
                                roomAvailableDate.getRoom().getRoomSize(),
                                roomAvailableDate.getRoom().getDoubleBed(),
                                roomAvailableDate.getRoom().getSingleBed(),
                                roomAvailableDate.getRoom().getBathroom(),
                                roomAvailableDate.getRoom().getBedroomCount(),
                                roomAvailableDate.getRoom().getCity().getCityId(),
                                roomAvailableDate.getRoom().getCity().getCityName()))
                        .collect(Collectors.toList());
                return new RoomResponse(0, list, true, "查詢成功");
            }
            return new RoomResponse(0, null, false, "查無可用日期");
        }
    }

}
