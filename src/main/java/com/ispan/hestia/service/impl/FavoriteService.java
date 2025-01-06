package com.ispan.hestia.service.impl;

import java.util.List;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.hestia.dto.FavoriteEditRequest;
import com.ispan.hestia.dto.FavoriteRequest;
import com.ispan.hestia.model.Favorite;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.FavoriteRepository;
import com.ispan.hestia.repository.RoomRepository;
import com.ispan.hestia.repository.UserRepository;

@Service
public class FavoriteService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoomRepository roomRepo;

	@Autowired
	private FavoriteRepository favoRepo;

	/* 查詢此 id 之 Favorite 是否存在 */
	public boolean exists(Integer id) {
		if (id != null) {
			return favoRepo.existsById(id);
		}
		return false;
	}

	/* count 此 user 總共有幾筆 favorite */
	public long count(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return favoRepo.count(obj);
		} catch (JSONException e) {
			System.err.println("Invalid JSON format: " + e.getMessage());
		}
		return 0;
	}

	// 某user新增商品至favorite
	@Transactional
	public Favorite addToFavorite(FavoriteRequest request, Integer userId) {

		if (request != null) {
			System.out.print("0");
			// 確認有此user
			Optional<User> user = userRepo.findById(userId);

			// get room物件確認有此room
			Optional<Room> optRoom = roomRepo.findById(request.roomId());

			// room, user都存在的情況下才開始加入favorite list
			if (optRoom.isPresent() && user.isPresent()) {
				System.out.print("1");

				// 1. 先查是否已在Favorite List內
				Favorite favorite = favoRepo.findByUserIdandRoomId(userId, request.roomId());

				// 2. 若1沒有，才save
				if (favorite == null) {
					Favorite fav = new Favorite();
					fav.setUser(user.get());
					fav.setRoom(optRoom.get());
					fav.setNote(request.note());
					System.out.print("3" + favoRepo.save(fav));
					return favoRepo.save(fav);
				}
			}
		}
		return null;
	}

	// 刪除favorite商品
	public boolean deleteFromFavorite(Integer id) {

		if (id != null) {
			// 1. 先查是否已在Favorite List內
			Optional<Favorite> favorite = favoRepo.findById(id);

			// 2. 若1有，才delete
			if (favorite.isPresent()) {
				favoRepo.delete(favorite.get());
				return true;
			}
		}
		return false;
	}

	// 刪除room favorite商品
	public boolean deleteRoomFromFavorite(Integer roomid, Integer userId) {

		if (roomid != null) {
			// 1. 先查是否已在Favorite List內
			Favorite favorite = favoRepo.findByUserIdandRoomId(userId, roomid);

			// 2. 若1有，才delete
			if (favorite != null) {
				favoRepo.delete(favorite);
				return true;
			}
		}
		return false;
	}

	// 查詢favorite商品
	public List<Favorite> findFromFavorite(Integer userId) {
		// user email查到此user
		Optional<User> user = userRepo.findById(userId);

		// 確認有此 user
		if (user.isPresent()) {
			return favoRepo.findByUserId(userId);
		}
		return null;
	}

	// 修改某筆 favorite 的 note
	@Transactional
	public Favorite addNoteToFavoriteRoom(Integer favoriteId, FavoriteEditRequest request, Integer userId) {

		if (request != null) {

			// 查到此 user
			Optional<User> optUser = userRepo.findById(userId);
	
			// 查到此 favorite
			Optional<Favorite> optFavo = favoRepo.findById(favoriteId);

			// 確認有此 user
			if (optUser.isPresent() && optFavo.isPresent()) {
	
				optFavo.get().setNote(request.note());
				return optFavo.get();
			}
		}
		return null;
	}
}
