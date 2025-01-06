package com.ispan.hestia.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.model.CartRoom;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomAvailableDate;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.CartRoomRepository;
import com.ispan.hestia.repository.RoomAvailableDateRepository;
import com.ispan.hestia.repository.RoomRepository;
import com.ispan.hestia.repository.UserRepository;

@Service
public class CartRoomService {

	@Autowired
	private CartRoomRepository cartRoomRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoomRepository roomRepo;

	@Autowired
	private RoomAvailableDateRepository roomAvailableDateRepo;

	/* 查詢此 id 之 CartRoom 是否存在 */
	public boolean exists(Integer id) {
		if (id != null) {
			return cartRoomRepo.existsById(id);
		}
		return false;
	}

	/* 查詢此 user 是否有此 roomId 之 CartRoom 存在 */
	public boolean existsByRoomId(Integer roomId) {
		if (roomId != null) {
			List<CartRoom> lst = cartRoomRepo.findByRoomId(roomId);
			if (lst != null && lst.size() != 0)
				return true;
		}
		return false;
	}

	/* count 此 user 總共有幾筆 cart */
	public long count(Integer userId) {
		try {
			return cartRoomRepo.count(userId);
		} catch (JSONException e) {
			System.err.println("Invalid JSON format: " + e.getMessage());
		}
		return 0;
	}

	public CartRoom addRoomstoCart(RoomAvailableDate dbRoomAvailableDate, Integer userId) {
		if (dbRoomAvailableDate != null && userId != null) {
			// userId 查到此 user
			Optional<User> user = userRepo.findById(userId);

			if (!user.isPresent())
				return null;

			// 檢查是否已經有加入過 cart
			CartRoom dbCartRooom = cartRoomRepo.findByUserIdAndRoomAvailableDateId(userId, dbRoomAvailableDate.getId());

			// 沒有加入過才把它 save 入 cart
			if (dbCartRooom == null) {
				CartRoom newCart = new CartRoom();
				newCart.setUser(user.get());
				newCart.setRoomAvailableDate(dbRoomAvailableDate);

				return cartRoomRepo.save(newCart);
			}
		}
		return null;
	}

	/*
	* 從cart移除商品
	* param roomId: Integer
	* return: 移除Cart物件true or 沒有此物件的話false
	*/
	public boolean deleteRoomIdFromCart(Integer roomId, String entity) {
		// 確認 cartId 有值
		if (roomId != null) {

			// 確認 db 中有此筆 CartRoom 才繼續
			List<CartRoom> dbCartRooms = cartRoomRepo.findByRoomId(roomId);

			JSONObject obj = new JSONObject(entity);
			JSONArray dates = obj.getJSONArray("dates");
			// 定義日期格式
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

			// 轉換為 List<Date>
			List<Date> dateList = new ArrayList<>();
			for (int i = 0; i < dates.length(); i++) {
				try {
					String dateStr = dates.getString(i);
					Date date = dateFormat.parse(dateStr); // 將字符串轉換為 Date
					dateList.add(date); // 添加到 List
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		if (dbCartRooms != null && dbCartRooms.size() != 0) {

			List<CartRoom> roomsToDelete = new ArrayList<>();
			for (CartRoom dbCartRoom : dbCartRooms) {
				for (Date date : dateList) {
					if (dbCartRoom.getRoomAvailableDate().getAvailableDates().equals(date)) {
						roomsToDelete.add(dbCartRoom);
					}
				}
			}
			System.err.println(roomsToDelete);
			// 一次性刪除
			cartRoomRepo.deleteAll(roomsToDelete);
			return true;
			}
		}
		return false;
	}

	public boolean deleteCartById(Integer cartId) {
		// 確認 cartId 有值
		if (cartId != null) {

			// 確認 db 中有此筆 CartRoom 才繼續
			Optional<CartRoom> dbCartRoom = cartRoomRepo.findById(cartId);
			if (dbCartRoom.isPresent()) {

				// 有在購物車內，才移除
				cartRoomRepo.delete(dbCartRoom.get());
				return true;
			}
		}
		return false;
	}

	/* 查詢cart商品 */
	public List<CartRoom> findCartList(Integer userId) {

		// user id查到此user
		Optional<User> user = userRepo.findById(userId);

		// 確認有此user，有的話才繼續查
		if (user.isPresent()) {

			// 查詢此user的cart list
			List<CartRoom> lst = cartRoomRepo.findByUserId(userId);
			return lst;
		}
		return null;
	}

	// /* 修改cart Date */
	// public List<CartRoom> editCartDate(Integer roomId, Integer userId, List<Date> dates){
	// 	if( roomId == null ) return null;
	// 	if( userId == null) return null;
	// 	if( dates == null || dates.size() == 0) return null;

	// 	// 找出 user 物件
	// 	Optional<User> optUser = userRepo.findById(userId);
	// 	if( !optUser.isPresent()) return null;

	// 	// 找出 room 物件
	// 	Optional<Room> optRoom = roomRepo.findById(roomId);
	// 	if( !optRoom.isPresent()) return null;


	// 	// 找出目前有此 roomId 相關的 cart
	// 	List<CartRoom> dbCartRoom = cartRoomRepo.findByRoomId(roomId);
	// 	if( dbCartRoom == null || dbCartRoom.size()==0) return null;

		
	// 	List<CartRoom> roomsToDelete = new ArrayList<>();
	// 	List<Date> datesToAdd = new ArrayList<>(dates);

	// 	// 找出 dates 的物件是否都存在
	// 	for (Date date: dates){
	// 		Optional<RoomAvailableDate> optRoomAvailableDate = roomAvailableDateRepo.findByRoomAndAvailableDates(optRoom.get(), date);
	// 		if(optRoomAvailableDate.isPresent()){

	// 			// 比較 "cart中" 與 "新傳入 dates" 兩者的 roomAvailableDate
	// 			for( CartRoom room: dbCartRoom){
					
	// 				// 如果兩個日期相同: 代表不需要做更動 ->將 date 先從 datesList 移除
	// 				if( room.getRoomAvailableDate().getId().equals(optRoomAvailableDate.get().getId())){
	// 					datesToAdd.remove(date);
	// 					roomsToDelete.remove(room);
	// 				}
	// 			}
	// 		}
	// 	}

	// 	// 把相同的都移除完成之後 dbCartRoom剩下的資料是需要從資料庫中移除的
	// 	for( CartRoom room: roomsToDelete){
	// 		cartRoomRepo.delete(room);
	// 	}

	// 	// 把相同的都移除完成之後 dates剩下的資料是需要從新增進資料庫的
	// 	for (Date date: datesToAdd){
	// 		CartRoom newCart = new CartRoom();
	// 		newCart.setRoomAvailableDate(roomAvailableDateRepo.findByRoomAndAvailableDates(optRoom.get(), date).get());
	// 		newCart.setUser(optUser.get());
	// 	}

	// 	return cartRoomRepo.findByRoomId(roomId);

	// }

	/* 修改 cart Date */
	public List<CartRoom> editCartDate(Integer roomId, Integer userId, List<Date> dates) {
		if (roomId == null || userId == null || dates == null || dates.isEmpty()) {
			return null;
		}

		// 找出 user 物件
		Optional<User> optUser = userRepo.findById(userId);
		if (!optUser.isPresent()) {
			return null;
		}

		// 找出 room 物件
		Optional<Room> optRoom = roomRepo.findById(roomId);
		if (!optRoom.isPresent()) {
			return null;
		}

		// 找出目前有此 roomId 相關的 cart
		List<CartRoom> dbCartRoom = new ArrayList<>(cartRoomRepo.findByRoomId(roomId)); // 複製列表避免直接修改
		if (dbCartRoom.isEmpty()) {
			return null;
		}

		// 建立需要保留的 `CartRoom` 和新增的 `dates`
		List<CartRoom> roomsToDelete = new ArrayList<>();
		List<Date> datesToAdd = new ArrayList<>(dates);

		for (Iterator<CartRoom> iterator = dbCartRoom.iterator(); iterator.hasNext(); ) {
			CartRoom cartRoom = iterator.next();

			// 比較 "cart中" 與 "新傳入 dates" 兩者的 roomAvailableDate
			RoomAvailableDate existingDate = cartRoom.getRoomAvailableDate();
			datesToAdd.removeIf(date -> {
				Optional<RoomAvailableDate> optRoomAvailableDate = roomAvailableDateRepo.findByRoomAndAvailableDates(optRoom.get(), date);
				if (optRoomAvailableDate.isPresent() && optRoomAvailableDate.get().getId().equals(existingDate.getId())) {
					iterator.remove(); // 安全地從 dbCartRoom 中移除
					return true;       // 同時從 datesToAdd 中移除該日期
				}
				return false;
			});
		}

		// 把需要刪除的資料從資料庫中移除
		for (CartRoom room : dbCartRoom) {
			cartRoomRepo.delete(room);
		}

		// 把剩下的 dates 新增進資料庫
		for (Date date : datesToAdd) {
			Optional<RoomAvailableDate> optRoomAvailableDate = roomAvailableDateRepo.findByRoomAndAvailableDates(optRoom.get(), date);
			if (optRoomAvailableDate.isPresent() && optRoomAvailableDate.get().getState().getStateId().equals(22)) {
				CartRoom newCart = new CartRoom();
				newCart.setRoomAvailableDate(optRoomAvailableDate.get());
				newCart.setUser(optUser.get());
				cartRoomRepo.save(newCart); // 保存新數據到資料庫
			}
		}

		// 返回最新的 cartRoom 列表
		return cartRoomRepo.findByRoomId(roomId);
	}

	/* checkAvailableDate */
	public boolean checkAvailableDate(Integer cartId){
		
		Optional<CartRoom> dbCartRoom = cartRoomRepo.findById(cartId);
		if(dbCartRoom.isPresent()){
			return dbCartRoom.get().getRoomAvailableDate().getState().getStateId().equals(22);
		}else{
			return false;
		}
	}

	/* checkAvailableDate ByRoomId 未登入時使用 */
	public boolean checkAvailableDateByRoomId(Integer roomId, Date date){
		
		Optional<Room> dbRoom = roomRepo.findById(roomId);
		if(dbRoom.isPresent()){
			Set<RoomAvailableDate> dbRoomAvailableDates = dbRoom.get().getRoomAvailableDate();
			for(RoomAvailableDate dbRoomAvailableDate:dbRoomAvailableDates ){
				if(dbRoomAvailableDate.getAvailableDates().equals(date) && dbRoomAvailableDate.getState().getStateId()==22){
					return true;
				}
			}
		}
		return false;
	}
}
