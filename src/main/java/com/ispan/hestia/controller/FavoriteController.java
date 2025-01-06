package com.ispan.hestia.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.FavoriteDTO;
import com.ispan.hestia.dto.FavoriteEditRequest;
import com.ispan.hestia.dto.FavoriteRequest;
import com.ispan.hestia.dto.FavoriteResponse;
import com.ispan.hestia.model.Favorite;
import com.ispan.hestia.service.UserService;
import com.ispan.hestia.service.impl.FavoriteService;
import com.ispan.hestia.service.impl.RoomService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/favorite")
@CrossOrigin
public class FavoriteController {
    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    /* 新增 room 至 user 的 favorite */
    @PostMapping("/addToFavorite")
    public FavoriteResponse addRoomToCart(@RequestBody FavoriteRequest request, HttpServletRequest req) {
        Integer userId = (Integer) req.getAttribute("userId");
        // request 有內容才新增
        if (request != null) {

            // 如果沒有此 room
            if (!roomService.exists(request.roomId())) {
                return new FavoriteResponse(0, null, false, "查無此 roomId");
            }

            // 如果沒有此 user
            else if (!userService.exists(userId)) {
                {
                    return new FavoriteResponse(0, null, false, "查無此 userId");
                }
            }

            // 都有才 save
            else {
                Favorite favorite = favoriteService.addToFavorite(request, userId);
                if (favorite != null) {
                    return new FavoriteResponse(1, null, true, "加入收藏成功！");
                } else {
                    return new FavoriteResponse(0, null, false, "已加入過此筆收藏");
                }
            }
        }
        return new FavoriteResponse(1, null, false, "加入收藏失敗！");
    }

     /* 刪除 favorite */
    @GetMapping("/deleteFavorite/{favoriteId}")
    public FavoriteResponse deleteFavorite(@PathVariable Integer favoriteId) {
        if (favoriteId == null) {
            return new FavoriteResponse(0, null, false, "Id 是必要欄位");
        } else if (!favoriteService.exists(favoriteId)) {
            return new FavoriteResponse(0, null, false, "Id 不存在");
        } else {
            boolean success = favoriteService.deleteFromFavorite(favoriteId);
            if (success) {
                return new FavoriteResponse(0, null, true, "刪除成功");
            }
        }
        return new FavoriteResponse(0, null, false, "刪除失敗");
    }

    /* 刪除 room 從某 user 的 favorite */
    @GetMapping("/deleteRoomFromFavorite/{roomId}")
    public FavoriteResponse deleteRoomFromFav(@PathVariable Integer roomId, HttpServletRequest req) {
        Integer userId = (Integer) req.getAttribute("userId");
        if (roomId == null) {
            return new FavoriteResponse(0, null, false, "Id 是必要欄位");
        } else if (!roomService.exists(roomId)) {
            return new FavoriteResponse(0, null, false, "Id 不存在");
        } else {
            boolean success = favoriteService.deleteRoomFromFavorite(roomId, userId);
            if (success) {
                return new FavoriteResponse(0, null, true, "刪除成功");
            }
        }
        return new FavoriteResponse(0, null, false, "刪除失敗");
    }

    /* 修改某筆 favorite 的 note */
    @PostMapping("/editFavorite/{favoriteId}")
    public FavoriteResponse addRoomToCart(@PathVariable Integer favoriteId, @RequestBody FavoriteEditRequest request, HttpServletRequest req) {
        Integer userId = (Integer) req.getAttribute("userId");

        if (favoriteId == null)
            return new FavoriteResponse(0, null, false, "Id 是必要欄位");
        else if (!favoriteService.exists(favoriteId))
            return new FavoriteResponse(0, null, false, "Id 不存在");
        else {
            if (request != null) {
                Favorite favorite = favoriteService.addNoteToFavoriteRoom(favoriteId, request, userId);
                if (favorite != null)
                    return new FavoriteResponse(1, null, true, "修改成功！");
            }
        }
        return new FavoriteResponse(0, null, false, "修改失敗");
    }

    /* 查詢 某 user 的 favorite */
    @GetMapping("/getFavorite")
    public FavoriteResponse getUserCart(HttpServletRequest req) {
        Integer userId = (Integer) req.getAttribute("userId");

        // 檢查有沒有傳入 userId
        if (userId == null)
            return new FavoriteResponse(0, null, false, "Id 是必要欄位");

        // 檢查有此 userId 存不存在
        else if (!userService.exists(userId))
            return new FavoriteResponse(0, null, false, "Id 不存在");

        // 都存在才繼續查詢
        else {
            // 組合成一個 JSONObject 傳去 CommentService 找筆數
            String jsonObj = "{ \"userId\":" + userId + "}";
            long count = favoriteService.count(jsonObj);
            List<Favorite> dbFavorites = favoriteService.findFromFavorite(userId);

            if (count != 0 && dbFavorites.size() != 0) {
                List<FavoriteDTO> lst = dbFavorites.stream()
                        .map(favorite -> new FavoriteDTO(
                                favorite.getFavoriteId(),
                                favorite.getUser().getUserId(),
                                favorite.getRoom().getRoomName(),
                                favorite.getRoom().getRoomAddr(),
                                favorite.getRoom().getRoomId(),
                                favorite.getRoom().getDoubleBed(),
                                favorite.getRoom().getSingleBed(),
                                favorite.getRoom().getBathroom(),
                                favorite.getRoom().getBedroomCount(),
                                favorite.getRoom().getRoomAvailableDate().size() > 0 ? favorite.getRoom().getRoomAvailableDate().iterator().next().getPrice(): null,
                                favorite.getRoom().getRoomSize(),
                                favorite.getRoom().getCity().getCityId(),
                                favorite.getRoom().getCity().getCityName(),
                                favorite.getNote()))
                        .collect(Collectors.toList());
                return new FavoriteResponse(count, lst, true, "查詢成功");
            }

        }
        return new FavoriteResponse(0, null, false, "還沒有任何收藏");
    }
}
