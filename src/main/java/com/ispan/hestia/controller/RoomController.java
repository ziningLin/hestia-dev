package com.ispan.hestia.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.hestia.dto.RoomDTO;
import com.ispan.hestia.dto.RoomResponse;
import com.ispan.hestia.model.Provider;
import com.ispan.hestia.dto.RoomsDTO;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomFacility;
import com.ispan.hestia.model.RoomImages;
import com.ispan.hestia.model.RoomRegulation;
import com.ispan.hestia.repository.ProviderRepository;
import com.ispan.hestia.service.ProviderService;
import com.ispan.hestia.service.impl.RoomService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/room")
@CrossOrigin
public class RoomController {

	@Autowired
	private RoomService roomService;
	@Autowired
	private ProviderService providerService;
	@Autowired
	private ProviderRepository providerRepo;

	// 查詢已上架的全部房間
	@GetMapping
	public List<RoomsDTO> getAllRooms() {
		List<Room> room = roomService.findAllRoom();
		return transIntoDTO(room);
	}

	// 根據房間id查詢(已上架)
	@GetMapping("/search/id/{roomId}")
	public List<RoomsDTO> getRoomsById(@PathVariable Integer roomId) {
		List<Room> room = roomService.findAllRoomByRoomId(roomId);
		return transIntoDTO(room);
	}

	// 根據房間名稱模糊查詢(已上架)
	@GetMapping("/search/name")
	public List<Room> getRoomsByName(@RequestParam String roomName) {
		return roomService.findAllRoomByRoomName(roomName);
	}

	// 根據房源類型(住宿類型)查詢(已上架)
	@GetMapping("/search/type")
	public List<Room> getRoomsByType(@RequestParam String roomTypeName) {
		return roomService.findAllRoomByRoomType(roomTypeName);
	}

	// 根據縣市名稱查詢(已上架)
	@GetMapping("/search/city")
	public List<Room> getRoomsByCity(@RequestParam String cityName) {
		return roomService.findAllRoomByCity(cityName);
	}

	// 根據地址模糊查詢(已上架)
	@GetMapping("/search/address")
	public List<Room> getRoomsByAddress(@RequestParam String roomAddr) {
		return roomService.findAllRoomByRoomAddr(roomAddr);
	}

	// 根據入住人數查詢(已上架)
	@GetMapping("/search/room-size")
	public List<Room> getRoomsBySize(@RequestParam Integer roomSize) {
		return roomService.findAllRoomByRoomSize(roomSize);
	}

	// 根據雙人床數量查詢(已上架)
	@GetMapping("/search/double-bed")
	public List<Room> getRoomsByDoubleBed(@RequestParam Integer doubleBed) {
		return roomService.findAllRoomByDoubleBed(doubleBed);
	}

	// 根據單人床數量查詢(已上架)
	@GetMapping("/search/single-bed")
	public List<Room> getRoomsBySingleBed(@RequestParam Integer singleBed) {
		return roomService.findAllRoomBySingleBed(singleBed);
	}

	// 根據臥室數量查詢(已上架)
	@GetMapping("/search/bedroom")
	public List<Room> getRoomsByBedroom(@RequestParam Integer bedroom) {
		return roomService.findAllRoomByBedroom(bedroom);
	}

	// 根據衛浴數量查詢(已上架)
	@GetMapping("/search/bathroom")
	public List<Room> getRoomsByBathroom(@RequestParam Integer bathroom) {
		return roomService.findAllRoomByBathroom(bathroom);
	}

	// 根據價格範圍查詢所有符合的房間(已上架)
	@GetMapping("/search/price")
	public List<Room> getRoomsByPriceRange(@RequestParam Integer minPrice, @RequestParam Integer maxPrice) {
		return roomService.findAllRoomByPrice(minPrice, maxPrice);
	}

	// 根據日期範圍查詢所有符合的房間(已上架)
	@GetMapping("/search/date")
	public List<Room> getRoomsByDateRange(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkinDate,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkoutDate) {
		return roomService.findAllRoomByDate(checkinDate, checkoutDate);
	}

	// 根據所選設施查詢所有符合的房間(已上架)
	@GetMapping("/search/facilities")
	public List<Room> getRoomsByFacilities(@RequestParam List<String> facilityNames) {
		return roomService.findAllRoomByFacilities(facilityNames);
	}

	// 根據房間名稱 or 縣市 or 地址 or 入住人數 or 日期範圍查詢(已上架)
	@GetMapping("/search/search-bar")
	public List<RoomsDTO> searchRooms(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer roomSize,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkinDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkoutDate) {
		keyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword;
		List<Room> room = roomService.findAllRoomBySearchBar(keyword, roomSize, checkinDate, checkoutDate);
		return transIntoDTO(room);
	}

	// 根據價格範圍 or 雙人床數量 or 單人床數量 or 臥室數量 or 衛浴數量 or 所選設施查詢(已上架)
	@GetMapping("/search/filter")
	public List<RoomsDTO> filterRooms(@RequestParam(required = false) Integer minPrice,
			@RequestParam(required = false) Integer maxPrice, @RequestParam(required = false) Integer doubleBed,
			@RequestParam(required = false) Integer singleBed, @RequestParam(required = false) Integer bedroomCount,
			@RequestParam(required = false) Integer bathroom,
			@RequestParam(required = false) List<String> facilityNames) {
		List<Room> room = roomService.findAllRoomByFilter(minPrice, maxPrice, doubleBed, singleBed, bedroomCount,
				bathroom, facilityNames);
		return transIntoDTO(room);
	}

	/* 上架 Room：更改 Room State */
	@GetMapping("/provider/publish/{roomId}")
	public RoomResponse publishRoom(@PathVariable Integer roomId) {
		System.out.println("有被呼叫 ID:" + roomId);
		try {
			roomService.publishRoom(roomId);
			// Integer roomId = jsonObj.isNull("roomId") ? null : jsonObj.getInt("roomId")
			return new RoomResponse(0, null, true, "成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new RoomResponse(0, null, false, "失敗");
		}
	}

	/* 多筆查詢: 根據 providerId 查詢除了「下架」的 Room */
	@GetMapping("/provider/findByProvider/{providerId}")
	public RoomResponse findProviderRooms(@PathVariable Integer providerId) {
		// 檢查是否真的有此 ProviderId
		if (providerId == null) {
			return new RoomResponse(0, null, false, "Id是必要欄位");
		} else if (!providerService.exists(providerId)) {
			return new RoomResponse(0, null, false, "Id不存在");
		} else {

			// 組合成一個 JSONObject 去 RoomService 找筆數
			String jsonObj = "{ \"provider\":" + providerId + "}";
			// long count = roomService.count(jsonObj);

			Set<Room> dbProviderRooms = roomService.findProviderRooms(providerId);
			if (dbProviderRooms != null && dbProviderRooms.size() != 0) {
				List<RoomDTO> lst = dbProviderRooms.stream()
						.map(room -> new RoomDTO(
								room.getRoomId(),
								room.getRoomName(),
								room.getRoomAddr(),
								room.getRoomSize(),
								room.getRoomContent(),
								room.getRoomNotice() != null ? room.getRoomNotice() : null, // 防止空值null,
								room.getDoubleBed(),
								room.getSingleBed(),
								room.getBedroomCount(),
								room.getBathroom(),
								room.getCheckinTime(),
								room.getCheckoutTime(),
								room.getProvider().getProviderId(),
								room.getRoomType().getRoomTypeId(),
								room.getCity().getCityId(),
								room.getState().getStateId(),
								room.getRefundPolicy() != null ? room.getRefundPolicy().getRefundPolicyId() : null, // 防止空值null,
								room.getMainImage(),
								room.getRoomImages()
										.stream()
										.collect(Collectors.toMap(
												RoomImages::getId, // Key: imageId
												RoomImages::getImage // Value: image (byte[])
										)),
								room.getRoomFacility().stream()
										.map(roomFacility -> roomFacility.getFacility().getFacilityId())
										.collect(Collectors.toSet()),
								room.getRoomRegulation().stream()
										.map(roomRegulation -> roomRegulation.getRegulation().getRoomRegulationId())
										.collect(Collectors.toSet())))
						.collect(Collectors.toList());
				return new RoomResponse(0, lst, true, "查詢成功");
			}
		}
		return new RoomResponse(0, null, false, "目前無房源資料");
	}

	/* 多筆查詢: 根據 providerId 查詢除了「未審核」的 Room */
	@GetMapping("/provider/findProviderRoomsByState/{providerId}")
	public RoomResponse findProviderRoomsByState(@PathVariable Integer providerId) {
		// 檢查是否真的有此 ProviderId
		if (providerId == null) {
			return new RoomResponse(0, null, false, "Id是必要欄位");
		} else if (!providerService.exists(providerId)) {
			return new RoomResponse(0, null, false, "Id不存在");
		} else {

			Set<Room> dbProviderRooms = roomService.findProviderRoomsByState(providerId);
			if (dbProviderRooms != null && dbProviderRooms.size() != 0) {
				List<RoomDTO> lst = dbProviderRooms.stream()
						.map(room -> new RoomDTO(
								room.getRoomId(),
								room.getRoomName(),
								room.getRoomAddr(),
								room.getRoomSize(),
								room.getRoomContent(),
								room.getRoomNotice() != null ? room.getRoomNotice() : null, // 防止空值null,
								room.getDoubleBed(),
								room.getSingleBed(),
								room.getBedroomCount(),
								room.getBathroom(),
								room.getCheckinTime(),
								room.getCheckoutTime(),
								room.getProvider().getProviderId(),
								room.getRoomType().getRoomTypeId(),
								room.getCity().getCityId(),
								room.getState().getStateId(),
								room.getRefundPolicy() != null ? room.getRefundPolicy().getRefundPolicyId() : null, // 防止空值null,
								room.getMainImage(),
								room.getRoomImages()
										.stream()
										.collect(Collectors.toMap(
												RoomImages::getId, // Key: imageId
												RoomImages::getImage // Value: image (byte[])
										)),
								room.getRoomFacility().stream()
										.map(roomFacility -> roomFacility.getFacility().getFacilityId())
										.collect(Collectors.toSet()),
								room.getRoomRegulation().stream()
										.map(roomRegulation -> roomRegulation.getRegulation().getRoomRegulationId())
										.collect(Collectors.toSet())))
						.collect(Collectors.toList());
				return new RoomResponse(0, lst, true, "查詢成功");
			}
		}
		return new RoomResponse(0, null, false, "目前無房源資料");
	}

	/* 單筆查詢: 根據 roomId 查詢 */
	@GetMapping("/provider/findByRoomId/{roomId}")
	public RoomResponse findoneRoom(@PathVariable Integer roomId) {
		// 檢查是否提供了 roomId
		if (roomId == null || roomId == 0) {
			return new RoomResponse(0, null, false, "RoomId 是必要欄位");
		}

		// 呼叫 RoomService 的方法查詢資料
		Room room = roomService.findoneRoom(roomId);

		// 如果找到 Room 資料
		if (room != null) {
			RoomDTO roomDTO = new RoomDTO(
					room.getRoomId(),
					room.getRoomName(),
					room.getRoomAddr(),
					room.getRoomSize(),
					room.getRoomContent(),
					room.getRoomNotice() != null ? room.getRoomNotice() : null, // 防止空值 null
					room.getDoubleBed(),
					room.getSingleBed(),
					room.getBedroomCount(),
					room.getBathroom(),
					room.getCheckinTime(),
					room.getCheckoutTime(),
					room.getProvider().getProviderId(),
					room.getRoomType().getRoomTypeId(),
					room.getCity().getCityId(),
					room.getState().getStateId(),
					room.getRefundPolicy() != null ? room.getRefundPolicy().getRefundPolicyId() : null, // 防止空值 null
					room.getMainImage(),
					room.getRoomImages()
							.stream()
							.collect(Collectors.toMap(
									RoomImages::getId, // Key: imageId
									RoomImages::getImage // Value: image (byte[])
							)),
					room.getRoomFacility().stream()
							.map(roomFacility -> roomFacility.getFacility().getFacilityId())
							.collect(Collectors.toSet()),
					room.getRoomRegulation().stream()
							.map(roomRegulation -> roomRegulation.getRegulation().getRoomRegulationId())
							.collect(Collectors.toSet())
			// room.getRoomRegulation().stream().map(RoomRegulation::getId).collect(Collectors.toSet())
			);
			return new RoomResponse(1, List.of(roomDTO), true, "查詢成功");
		}

		// 如果找不到 Room 資料
		return new RoomResponse(0, null, false, "RoomId 不存在或查詢失敗");
	}

	/* 新增一筆 Room：Step 1 */
	@PostMapping("/provider/create/step-one")
	public RoomResponse createStepOne(@RequestBody RoomDTO roomRequest) {
		Room created = roomService.createStepOne(roomRequest);
		if (created != null) {
			// List<Room> roomList = List.of(created); // 使用 List.of 建立不可變列表
			List<Room> roomList = new ArrayList<>(List.of(created));
			return new RoomResponse(1, roomList, true, "新增成功");
		} else {
			return new RoomResponse(0, null, false, "新增失敗");
		}
	}

	/* 新增一筆 Room：Step 2 */
	@PostMapping("/provider/create/step-two/{roomId}")
	public RoomResponse createStepTwo(@PathVariable Integer roomId, @RequestBody RoomDTO roomRequest) {
		Room created = roomService.createStepTwo(roomId, roomRequest.getRoomAddr());
		if (created != null) {
			List<Room> roomList = new ArrayList<>(List.of(created));
			return new RoomResponse(1, roomList, true, "新增成功");
		} else {
			return new RoomResponse(0, null, false, "新增失敗");
		}
	}

	/* 新增一筆 Room：Step 3 */
	@PostMapping("/provider/create/step-three/{roomId}")
	public RoomResponse createStepThree(@PathVariable Integer roomId, @RequestBody RoomDTO roomRequest) {
		Room created = roomService.createStepThree(roomId, roomRequest);
		if (created != null) {
			List<Room> roomList = new ArrayList<>(List.of(created));
			return new RoomResponse(1, roomList, true, "新增成功");
		} else {
			return new RoomResponse(0, null, false, "新增失敗");
		}
	}

	/* 新增一筆 Room：Step 4 */
	@PostMapping("/provider/create/step-four/{roomId}")
	public RoomResponse createStepFour(@PathVariable Integer roomId, @RequestBody RoomDTO roomRequest) {
		Room created = roomService.createStepFour(roomId, roomRequest);
		if (created != null) {
			List<Room> roomList = new ArrayList<>(List.of(created));
			return new RoomResponse(1, roomList, true, "新增成功");
		} else {
			return new RoomResponse(0, null, false, "新增失敗");
		}
	}

	/* 新增一筆 Room：Step 5 */
	@PostMapping("/provider/create/step-five/{roomId}")
	public RoomResponse createStepFive(@PathVariable Integer roomId,
			@RequestParam("mainImage") MultipartFile mainImage,
			@RequestParam("roomImages") List<MultipartFile> roomImages) {

		Room created = roomService.createStepFive(roomId, mainImage, roomImages);
		if (created != null) {
			List<Room> roomList = new ArrayList<>(List.of(created));
			return new RoomResponse(1, roomList, true, "新增成功");
		} else {
			return new RoomResponse(0, null, false, "新增失敗");
		}

	}

	/* 修改一筆 Room */
	@PutMapping("/provider/modify/{roomId}")
	public RoomResponse modify(@PathVariable Integer roomId, @RequestBody RoomDTO roomRequest) {
		if (roomId == null) {
			return new RoomResponse(0, null, false, "id是必要欄位");
		} else if (!roomService.exists(roomId)) {
			return new RoomResponse(0, null, false, "資料不存在");
		} else {
			Room modified = roomService.modify(roomId, roomRequest);
			List<Room> roomList = new ArrayList<>(List.of(modified));
			if (modified != null) {
				return new RoomResponse(1, roomList, true, "修改成功");
			} else {
				return new RoomResponse(0, null, false, "修改失敗");
			}
		}
	}

	/* 刪除一筆 Room */
	@DeleteMapping("/provider/remove/{roomId}")
	public RoomResponse remove(@PathVariable Integer roomId) {
		if (roomId == null) {
			return new RoomResponse(0, null, false, "id是必要欄位");
		} else if (!roomService.exists(roomId)) {
			return new RoomResponse(0, null, false, "id不存在");
		} else {
			if (roomService.remove(roomId)) {
				return new RoomResponse(1, null, true, "刪除成功");
			} else {
				return new RoomResponse(0, null, false, "刪除失敗");
			}
		}
	}

	private List<RoomsDTO> transIntoDTO(List<Room> rooms) {
		List<RoomsDTO> roomsDTO = rooms.stream()
				.map(room -> new RoomsDTO(room.getRoomId(), room.getRoomName(), room.getRoomType().getRoomTypeId(),
						room.getRoomType().getName(), room.getCity().getCityId(), room.getCity().getCityName(),
						room.getRoomAddr(), room.getRoomSize(), room.getRoomContent(), room.getRoomNotice(),
						room.getProvider().getProviderId(), room.getProvider().getUser().getUserId(),
						room.getProvider().getUser().getName(), room.getProvider().getUser().getPhoto(),
						room.getState().getStateId(), room.getState().getStateContent(), room.getDoubleBed(),
						room.getSingleBed(), room.getBedroomCount(), room.getBathroom(), room.getCheckinTime(),
						room.getCheckoutTime(), room.getMainImage(),
						room.getRoomImages().stream().map(RoomImages::getImage).collect(Collectors.toSet()),
						room.getRoomRegulation().stream()
								.map(roomRegulation -> roomRegulation.getRegulation().getRoomRegulationName())
								.collect(Collectors.toSet())))
				.collect(Collectors.toList());
		return roomsDTO;
	}
}
