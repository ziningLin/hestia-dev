package com.ispan.hestia.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ispan.hestia.dto.RoomDTO;
import com.ispan.hestia.model.City;
import com.ispan.hestia.model.Facility;
import com.ispan.hestia.model.Provider;
import com.ispan.hestia.model.RefundPolicy;
import com.ispan.hestia.model.Regulation;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomFacility;
import com.ispan.hestia.model.RoomImages;
import com.ispan.hestia.model.RoomRegulation;
import com.ispan.hestia.model.RoomType;
import com.ispan.hestia.model.State;
import com.ispan.hestia.repository.CityRepository;
import com.ispan.hestia.repository.FacilityRepository;
import com.ispan.hestia.repository.ProviderRepository;
import com.ispan.hestia.repository.RefundPolicyRepository;
import com.ispan.hestia.repository.RegulationRepository;
import com.ispan.hestia.repository.RoomFacilityRepository;
import com.ispan.hestia.repository.RoomImagesRepository;
import com.ispan.hestia.repository.RoomRegulationRepository;
import com.ispan.hestia.repository.RoomRepository;
import com.ispan.hestia.repository.RoomTypeRepository;
import com.ispan.hestia.repository.StateRepository;

@Service
public class RoomService {

	@Autowired
	private RoomRepository roomRepo;
	@Autowired
	private ProviderRepository providerRepo;
	@Autowired
	private StateRepository stateRepo;
	@Autowired
	private RoomTypeRepository roomTypeRepo;
	@Autowired
	private CityRepository cityRepo;
	@Autowired
	private RefundPolicyRepository refundPolicyRepo;
	@Autowired
	private FacilityRepository facilityRepo;
	@Autowired
	private RoomFacilityRepository roomFacilityRepository;
	@Autowired
	private RegulationRepository regulationRepo;
	@Autowired
	private RoomRegulationRepository roomRegulationRepo;
	@Autowired
	private RoomImagesRepository roomImagesRepo;

	// 是否存在
	public boolean exists(Integer id) {
		if (id != null) {
			return roomRepo.existsById(id);
		}
		return false;
	}

	// 查詢已上架的全部房間
	public List<Room> findAllRoom() {
		return roomRepo.findRoom();
	}

	// 根據房間id查詢(已上架)
	public List<Room> findAllRoomByRoomId(Integer roomId) {
		return roomRepo.findRoomByRoomId(roomId);
	}

	// 根據房間名稱模糊查詢(已上架)
	public List<Room> findAllRoomByRoomName(String roomName) {
		return roomRepo.findRoomByRoomNameLike(roomName);
	}

	// 根據房源類型(住宿類型)查詢(已上架)
	public List<Room> findAllRoomByRoomType(String roomTypeName) {
		return roomRepo.findRoomByRoomType(roomTypeName);
	}

	// 根據縣市名稱查詢(已上架)
	public List<Room> findAllRoomByCity(String cityName) {
		return roomRepo.findRoomByCity(cityName);
	}

	// 根據地址模糊查詢(已上架)
	public List<Room> findAllRoomByRoomAddr(String roomAddr) {
		return roomRepo.findRoomByRoomAddrLike(roomAddr);
	}

	// 根據入住人數查詢(已上架)
	public List<Room> findAllRoomByRoomSize(Integer roomSize) {
		return roomRepo.findRoomByRoomSizeGreaterThanOrEqual(roomSize);
	}

	// 根據雙人床數量查詢(已上架)
	public List<Room> findAllRoomByDoubleBed(Integer doubleBed) {
		return roomRepo.findRoomByDoubleBedGreaterThanOrEqual(doubleBed);
	}

	// 根據單人床數量查詢(已上架)
	public List<Room> findAllRoomBySingleBed(Integer singleBed) {
		return roomRepo.findRoomBySingleBedGreaterThanOrEqual(singleBed);
	}

	// 根據臥室數量查詢(已上架)
	public List<Room> findAllRoomByBedroom(Integer bedroom) {
		return roomRepo.findRoomByBedroomGreaterThanOrEqual(bedroom);
	}

	// 根據衛浴數量查詢(已上架)
	public List<Room> findAllRoomByBathroom(Integer bathroom) {
		return roomRepo.findRoomByBathroomGreaterThanOrEqual(bathroom);
	}

	// 根據價格範圍查詢所有符合的房間(已上架)
	public List<Room> findAllRoomByPrice(Integer minPrice, Integer maxPrice) {
		return roomRepo.findRoomByPriceRange(minPrice, maxPrice);
	}

	// 根據日期範圍查詢所有符合的房間(已上架)
	public List<Room> findAllRoomByDate(Date checkinDate, Date checkoutDate) {
		return roomRepo.findRoomByDateRange(checkinDate, checkoutDate);
	}

	// 根據所選設施查詢所有符合的房間(已上架)
	public List<Room> findAllRoomByFacilities(List<String> facilityNames) {
		// 確保空的參數不會導致查詢錯誤
		if (facilityNames == null || facilityNames.isEmpty()) {
			return roomRepo.findAll(); // 如果不指定設施，則返回所有房間
		}
		return roomRepo.findRoomByFacilities(facilityNames, facilityNames.size());
	}

	// 根據房間名稱 or 縣市 or 地址 or 入住人數 or 日期範圍查詢(已上架)
	public List<Room> findAllRoomBySearchBar(String keyword, Integer roomSize, Date checkinDate, Date checkoutDate) {
		return roomRepo.findRoomBySearchBar(keyword, roomSize, checkinDate, checkoutDate);
	}

	// 根據價格範圍 or 雙人床數量 or 單人床數量 or 臥室數量 or 衛浴數量 or 所選設施查詢(已上架)
	public List<Room> findAllRoomByFilter(Integer minPrice, Integer maxPrice, Integer doubleBed, Integer singleBed,
			Integer bedroomCount, Integer bathroom, List<String> facilityNames) {
		Integer facilityCount = (facilityNames != null) ? facilityNames.size() : null;
		return roomRepo.findRoomByFilter(minPrice, maxPrice, doubleBed, singleBed, bedroomCount, bathroom,
				facilityNames, facilityCount);
	}

	/* 上架 Room：更改 Room State */
	public boolean publishRoom(Integer roomId) {
		try {
			Room room = roomRepo.findById(roomId).get();
			State state = stateRepo.findById(22).get();
			room.setState(state);
			roomRepo.save(room);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/* 多筆查詢: 根據 providerId 查詢除了「下架」的 Room */
	public Set<Room> findProviderRooms(Integer providerId) {
		if (providerId != null && providerId != 0) {

			// 檢查是否有此 Provider
			Optional<Provider> dbProvider = providerRepo.findById(providerId);
			Set<Room> room = dbProvider.get().getRoom();
			if (!room.isEmpty()) {
				Set<Room> providerRooms = roomRepo.findRoomByProviderId(providerId)
						.stream()
						.filter(r -> !"下架".equals(r.getState().getStateContent()))
						.collect(Collectors.toSet());
				if (!providerRooms.isEmpty()) {
					return providerRooms;
				}
			}
		}
		return null;
	}

	/* 多筆查詢: 根據 providerId 查詢除了「未審核」狀態的 Room */
	public Set<Room> findProviderRoomsByState(Integer providerId) {
		if (providerId != null && providerId != 0) {

			// 檢查是否有此 Provider
			Optional<Provider> dbProvider = providerRepo.findById(providerId);
			Set<Room> room = dbProvider.get().getRoom();
			if (!room.isEmpty()) {
				// Set<Room> providerRooms = roomRepo.findRoomByProviderId(providerId);
				// 過濾掉「未審核」狀態的 Room
				Set<Room> providerRooms = roomRepo.findRoomByProviderId(providerId)
						.stream()
						.filter(r -> !"未審核".equals(r.getState().getStateContent())
								&& !"下架".equals(r.getState().getStateContent()))
						.collect(Collectors.toSet());
				if (!providerRooms.isEmpty()) {
					return providerRooms;
				}
			}
		}
		return null;
	}

	/* 單筆查詢: 根據 roomId 查詢 */
	public Room findoneRoom(Integer roomId) {
		if (roomId != null && roomId != 0) {
			Optional<Room> dbRoom = roomRepo.findById(roomId);
			if (dbRoom.isPresent()) {
				return dbRoom.get();
			}
		}
		return null;
	}

	/* 新增一筆 Room：Step 1 */
	public Room createStepOne(RoomDTO roomRequest) {
		Room insert = new Room();
		insert.setRoomName(roomRequest.getRoomName());

		if (roomRequest.getProviderId() != null) {
			Provider provider = providerRepo.findById(roomRequest.getProviderId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
							"查無此房東" + roomRequest.getProviderId()));
			insert.setProvider(provider);
		}

		if (roomRequest.getStateId() != null) {
			State state = stateRepo.findById(roomRequest.getStateId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
							"查無此狀態" + roomRequest.getStateId()));
			insert.setState(state);
		}

		if (roomRequest.getRoomTypeId() != null) {
			RoomType roomType = roomTypeRepo.findById(roomRequest.getRoomTypeId())
					.orElseThrow(() -> new ResponseStatusException(
							HttpStatus.NOT_FOUND,
							"查無此房源類型 " + roomRequest.getRoomTypeId()));
			insert.setRoomType(roomType);
		}

		if (roomRequest.getCityId() != null) {
			City city = cityRepo.findById(roomRequest.getCityId())
					.orElseThrow(() -> new ResponseStatusException(
							HttpStatus.NOT_FOUND,
							"查無此城市 " + roomRequest.getCityId()));
			insert.setCity(city);
		}
		return roomRepo.save(insert);
	}

	/* 新增一筆 Room：Step 2 */
	public Room createStepTwo(Integer roomId, String roomAddr) {
		// 找 Step 1 中已建立的Room Id
		Room insert = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此房源"));

		// 檢查是否有填
		if (roomAddr == null || roomAddr.isEmpty()) {
			throw new IllegalArgumentException("地址為必填");
		}
		insert.setRoomAddr(roomAddr);
		return roomRepo.save(insert);
	}

	/* 新增一筆 Room：Step 3 */
	public Room createStepThree(Integer roomId, RoomDTO roomRequest) {
		// 找 Step 1 中已建立的Room Id
		Room insert = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此房源"));
		// 檢查是否有填
		if (roomRequest.getRoomSize() == null || roomRequest.getRoomContent() == null ||
				roomRequest.getDoubleBed() == null || roomRequest.getSingleBed() == null ||
				roomRequest.getBedroomCount() == null || roomRequest.getBathroom() == null) {
			throw new IllegalArgumentException("選項未填完！");
		}

		insert.setRoomSize(roomRequest.getRoomSize());
		insert.setRoomContent(roomRequest.getRoomContent());
		insert.setDoubleBed(roomRequest.getDoubleBed());
		insert.setSingleBed(roomRequest.getSingleBed());
		insert.setBedroomCount(roomRequest.getBedroomCount());
		insert.setBathroom(roomRequest.getBathroom());

		return roomRepo.save(insert);
	}

	/* 新增一筆 Room：Step 4 */
	public Room createStepFour(Integer roomId, RoomDTO roomRequest) {
		// 找 Step 1 中已建立的Room Id
		Room insert = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此房源"));

		// 多對多資料的處理
		if (roomRequest.getFacilityId() != null) {
			for (Integer facilityId : roomRequest.getFacilityId()) {

				// 根據 facilityId 拿到Facility物件
				Optional<Facility> dbFacility = facilityRepo.findById(facilityId);
				if (dbFacility.isPresent()) {

					// 創建 RoomFacility 實例
					RoomFacility roomFacility = new RoomFacility();

					roomFacility.setFacility(dbFacility.get());
					roomFacility.setRoom(insert);
					roomFacilityRepository.save(roomFacility);
				}
			}
		}

		if (roomRequest.getRegulationId() != null) {
			for (Integer regulationId : roomRequest.getRegulationId()) {

				// 根據 regulationId 拿到Facility物件
				Optional<Regulation> dbRegulation = regulationRepo.findById(regulationId);
				if (dbRegulation.isPresent()) {

					// 創建 RoomRegulation 實例
					RoomRegulation roomRegulation = new RoomRegulation();

					roomRegulation.setRegulation(dbRegulation.get());
					roomRegulation.setRoom(insert);
					roomRegulationRepo.save(roomRegulation);
				}
			}
		}
		return roomRepo.save(insert);
	}

	/* 新增一筆 Room：Step 5 */
	public Room createStepFive(Integer roomId, MultipartFile mainImage, List<MultipartFile> roomImages) {
		// 找 Step 1 中已建立的Room Id
		Room room = roomRepo.findById(roomId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此房源"));

		// mainImage
		if (mainImage == null || mainImage.isEmpty()) {
			throw new IllegalArgumentException("至少上傳 1 張主照片");
		}
		try {
			room.setMainImage(mainImage.getBytes()); // 將主照片存入 Room 的 mainImage 欄位
		} catch (IOException e) {
			throw new RuntimeException("主照片處理失敗", e);
		}
		roomRepo.save(room);

		// roomImages
		if (roomImages == null || roomImages.size() < 5) {
			throw new IllegalArgumentException("至少上傳 5 張照片");
		}

		List<RoomImages> lst = new ArrayList<>();
		for (MultipartFile roomImage : roomImages) {
			try {
				RoomImages image = new RoomImages();

				image.setRoom(room);
				image.setImage(roomImage.getBytes()); // 從 MultipartFile 獲取圖片數據
				lst.add(image);
			} catch (IOException e) {
				throw new RuntimeException("圖片處理失敗", e);
			}
		}
		roomImagesRepo.saveAll(lst);
		return roomRepo.save(room);
	}

	/* 修改一筆 Room */
	public Room modify(Integer roomId, RoomDTO roomRequest) {
		try {
			if (roomRequest == null) {
				throw new IllegalArgumentException("欄位不可有空白！");
			}

			// 確認傳進來的 roomId 有值
			if (roomId == null) {
				throw new IllegalArgumentException("房間 ID 不可為空！");
			}
			// 確認 db 中此 Room 是存在的
			Optional<Room> dbRoom = roomRepo.findById(roomRequest.getRoomId());
			if (dbRoom.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此房間: " + roomId);
			}

			Room edit = dbRoom.get();
			edit.setRoomName(roomRequest.getRoomName());
			edit.setRoomAddr(roomRequest.getRoomAddr());
			edit.setRoomSize(roomRequest.getRoomSize());
			edit.setRoomContent(roomRequest.getRoomContent());
			edit.setRoomNotice(roomRequest.getRoomNotice());
			edit.setDoubleBed(roomRequest.getDoubleBed());
			edit.setSingleBed(roomRequest.getSingleBed());
			edit.setBedroomCount(roomRequest.getBedroomCount());
			edit.setBathroom(roomRequest.getBathroom());
			edit.setCheckinTime(roomRequest.getCheckinTime());
			edit.setCheckoutTime(roomRequest.getCheckoutTime());

			if (roomRequest.getProviderId() != null) {
				Provider provider = providerRepo.findById(roomRequest.getProviderId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								"查無此業者" + roomRequest.getProviderId()));
				edit.setProvider(provider);
			}

			if (roomRequest.getStateId() != null) {
				State state = stateRepo.findById(roomRequest.getStateId())
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								"查無此狀態" + roomRequest.getStateId()));
				edit.setState(state);
			}

			if (roomRequest.getRoomTypeId() != null) {
				RoomType roomType = roomTypeRepo.findById(roomRequest.getRoomTypeId())
						.orElseThrow(() -> new ResponseStatusException(
								HttpStatus.NOT_FOUND,
								"查無此房源類型 " + roomRequest.getRoomTypeId()));
				edit.setRoomType(roomType);
			}

			if (roomRequest.getCityId() != null) {
				City city = cityRepo.findById(roomRequest.getCityId())
						.orElseThrow(() -> new ResponseStatusException(
								HttpStatus.NOT_FOUND,
								"查無此城市 " + roomRequest.getCityId()));
				edit.setCity(city);
			}

			if (roomRequest.getRefundPolicyId() != null) {
				RefundPolicy refundPolicy = refundPolicyRepo.findById(roomRequest.getRefundPolicyId())
						.orElseThrow(() -> new ResponseStatusException(
								HttpStatus.NOT_FOUND,
								"查無此退款政策" + roomRequest.getRefundPolicyId()));
				edit.setRefundPolicy(refundPolicy);
			}

			Room newRoom = roomRepo.save(edit);

			// 多對多的處理
			if (roomRequest.getFacilityId() != null) {
				for (Integer facilityId : roomRequest.getFacilityId()) {

					// 拿到Facility物件
					Optional<Facility> dbFacility = facilityRepo.findById(facilityId);
					if (dbFacility.isPresent()) {
						// 創建roomFacility實例
						RoomFacility roomFacility = new RoomFacility();

						roomFacility.setFacility(dbFacility.get());
						roomFacility.setRoom(newRoom);
						roomFacilityRepository.save(roomFacility);
					}
				}
			}

			if (roomRequest.getRegulationId() != null) {
				for (Integer regulationId : roomRequest.getRegulationId()) {

					// 拿到Regulation物件
					Optional<Regulation> dbRegulation = regulationRepo.findById(regulationId);
					if (dbRegulation.isPresent()) {
						// 創建roomFacility實例
						RoomRegulation roomRegulation = new RoomRegulation();

						roomRegulation.setRegulation(dbRegulation.get());
						roomRegulation.setRoom(newRoom);
						roomRegulationRepo.save(roomRegulation);
					}
				}
			}

			return newRoom;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/* 刪除一筆 Room */
	public boolean remove(Integer id) {
		if (id != null && roomRepo.existsById(id)) {
			roomRepo.deleteById(id);
			return true;
		}
		return false;
	}
}
