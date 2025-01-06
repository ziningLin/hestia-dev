package com.ispan.hestia.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ispan.hestia.dto.RoomAvailableDateDTO;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomAvailableDate;
import com.ispan.hestia.model.State;
import com.ispan.hestia.repository.RoomAvailableDateRepository;
import com.ispan.hestia.repository.RoomRepository;
import com.ispan.hestia.repository.StateRepository;
import com.ispan.hestia.util.DateUtil;

import jakarta.transaction.Transactional;

@Service
public class RoomAvailableDateService {
    @Autowired
    private RoomAvailableDateRepository roomAvailableDateRepo;
    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private StateRepository stateRepo;

    public boolean exists(Integer id) {
        if (id != null) {
            return roomAvailableDateRepo.existsById(id);
        }
        return false;
    }

    /* 新增 Available Date */
    public List<RoomAvailableDate> insertDates(RoomAvailableDateDTO dateRequest) {
        try {
            if (dateRequest == null) {
                throw new IllegalArgumentException("欄位不可有空白！");
            }

            // 檢查是否有 Room Id
            if (dateRequest.getRoomId() != null) {
                Optional<Room> dbRoom = roomRepo.findById(dateRequest.getRoomId());
                if (dbRoom.isPresent()) {
                    List<RoomAvailableDate> availableDates = new ArrayList<>();

                    // 創建 RoomAvailableDate 實例
                    for (Date date : dateRequest.getAvailableDates()) {
                        RoomAvailableDate insert = new RoomAvailableDate();

                        insert.setId(dateRequest.getId());
                        insert.setAvailableDates(date);
                        insert.setPrice(dateRequest.getPrice());
                        insert.setRoomSum(dateRequest.getRoomSum());
                        insert.setLatestBookingDate(dateRequest.getLatestBookingDate());
                        insert.setReleaseDate(dateRequest.getReleaseDate());

                        // 關聯 Room
                        insert.setRoom(dbRoom.get());

                        if (dateRequest.getStateId() != null) {
                            State state = stateRepo.findById(dateRequest.getStateId())
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                            "查無此狀態" + dateRequest.getStateId()));
                            insert.setState(state);
                        }
                        availableDates.add(insert);
                    }
                    return roomAvailableDateRepo.saveAll(availableDates);
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此房間: " +
                    dateRequest.getRoomId());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* 修改 Available Date */
    public List<RoomAvailableDate> modifyDates(Integer roomId, RoomAvailableDateDTO dateRequest) {
        try {
            if (dateRequest == null) {
                throw new IllegalArgumentException("欄位不可有空白！");
            }

            if (dateRequest.getRoomId() == null) {
                throw new IllegalArgumentException("房間 ID 不可為空！");
            }

            // 檢查是否有 Room
            Optional<Room> dbRoom = roomRepo.findById(dateRequest.getRoomId());

            if (dbRoom.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此房間: " + roomId);
            }

            Room room = dbRoom.get();

            List<RoomAvailableDate> availableDates = new ArrayList<>();

            // 將 List<?> 轉換為 List<Date>
            List<?> rawDates = dateRequest.getAvailableDates();
            List<Date> dateList = rawDates.stream()
                    .filter(element -> element instanceof Date)
                    .map(element -> (Date) element)
                    .collect(Collectors.toList());

            for (Date date : dateList) {

                // 查詢是否已存在該日期的記錄
                Optional<RoomAvailableDate> dbDate = roomAvailableDateRepo
                        .findByRoomAndAvailableDates(dbRoom.get(), date);

                RoomAvailableDate edit;
                if (dbDate.isPresent()) {
                    // 更新現有記錄
                    edit = dbDate.get();
                } else {
                    // 創建新記錄
                    edit = new RoomAvailableDate();
                }

                edit.setAvailableDates(date);
                edit.setPrice(dateRequest.getPrice());
                edit.setRoomSum(dateRequest.getRoomSum());
                edit.setLatestBookingDate(dateRequest.getLatestBookingDate());
                edit.setReleaseDate(dateRequest.getReleaseDate());

                // 關聯 Room
                edit.setRoom(room);

                if (dateRequest.getStateId() != null) {
                    State state = stateRepo.findById(dateRequest.getStateId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "查無此狀態" + dateRequest.getStateId()));
                    edit.setState(state);
                }
                availableDates.add(edit);
            }

            return roomAvailableDateRepo.saveAll(availableDates);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /* 刪除 Available Date */
    @Transactional
    public boolean removeDates(Integer roomAvailableDateId) {
        if (roomAvailableDateId != null && roomAvailableDateRepo.existsById(roomAvailableDateId)) {
            roomAvailableDateRepo.deleteById(roomAvailableDateId);
            return true;
        }
        return false;
    }

    /* 多筆查詢: 根據 roomId 查詢所有狀態的 Room Available Date */
    public Set<RoomAvailableDate> findRoomDates(Integer roomId) {
        if (roomId != null && roomId != 0) {

            // 檢查是否有此 Room
            Optional<Room> dbRoom = roomRepo.findById(roomId);
            Set<RoomAvailableDate> roomAvailableDate = dbRoom.get().getRoomAvailableDate();
            if (!roomAvailableDate.isEmpty()) {
                Set<RoomAvailableDate> roomAvailableDates = roomAvailableDateRepo.findRoomAvailableDateByRoomId(roomId);
                if (!roomAvailableDates.isEmpty()) {
                    return roomAvailableDates;
                }
            }
        }
        return null;
    }

    public RoomAvailableDate findRoomAvailableDateroomIdAndDate(Integer roomId, Date date) {
        if (date != null) {
            RoomAvailableDate dbRoomAvailableDate = roomAvailableDateRepo.findAvailableDatesByRoomIdAndDate(roomId,
                    date);
            // 有這個日期，並且是可用的(stateId ==22)
            if (dbRoomAvailableDate != null && dbRoomAvailableDate.getState().getStateId() == 22)
                return dbRoomAvailableDate;
        }
        return null;
    }

    // 列出某個房間所有可提供日期和價格
    public List<Object[]> findAllAvailableDatesAndPricesByRoomId(Integer roomId) {
        return roomAvailableDateRepo.findAvailableDatesAndPricesByRoomId(roomId);
    }

    // 查詢某 roomId 的日期區間是否可用
    public List<RoomAvailableDate> findIsRoomAvailableInDates(Integer roomId, String entity) {
        // 將 startDate, endDate 轉換成一個 dateList (不包含結束日期)
        JSONObject obj = new JSONObject(entity);
        List<Date> dates = DateUtil.getDatesList(DateUtil.parseDate(obj.getString("startDate")),
                DateUtil.parseDate(obj.getString("endDate")));

        // 製作一個 List<RoomAvailableDate> 準備回傳
        List<RoomAvailableDate> dbRoomAvailableDate = new ArrayList<>();
        Optional<RoomAvailableDate> optRoomAvailableDate = null;

        // loop over dates 一個一個查是否可用
        for (Date date : dates) {
            optRoomAvailableDate = roomAvailableDateRepo.findAvailableDatesAndPricesByRoomId(roomId, date);

            // 可用的話就裝回 List 等待回傳
            if (optRoomAvailableDate.isPresent()) {
                dbRoomAvailableDate.add(optRoomAvailableDate.get());
            }
        }
        System.out.println(dbRoomAvailableDate);
        return dbRoomAvailableDate;
    }

    // 查詢某 roomId 近 15 個月的可用日期
    public List<RoomAvailableDate> findRecentRoomAvailableDates(Integer roomId) {

        // 15 個月後的月底
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // 設定起始日期
        calendar.add(Calendar.MONTH, 15); // 增加 15 個月
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // 設定為當月最後一天
        Date lastDayOfMonth = calendar.getTime();

        // 呼叫 Repository 查詢
        return roomAvailableDateRepo.findRoomAvailableDatesWithinRange(roomId, lastDayOfMonth);
    }

}
