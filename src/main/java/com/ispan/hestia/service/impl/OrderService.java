package com.ispan.hestia.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.hestia.dto.CheckOutRequest;
import com.ispan.hestia.dto.OrderProviderDTO;
import com.ispan.hestia.dto.OrderProviderMostOrderedRoomsDTO;
import com.ispan.hestia.dto.OrderProviderTopSellingRoomsDTO;
import com.ispan.hestia.dto.OrderReservedRoomDTO;
import com.ispan.hestia.dto.OrderReservedRoomDetailDTO;
import com.ispan.hestia.dto.OrderUserDTO;
import com.ispan.hestia.dto.SalesNumbersDTO;
import com.ispan.hestia.dto.SalesNumbersSumDTO;
import com.ispan.hestia.model.CartRoom;
import com.ispan.hestia.model.Order;
import com.ispan.hestia.model.OrderDetails;
import com.ispan.hestia.model.OrderRefundRecord;
import com.ispan.hestia.model.Provider;
import com.ispan.hestia.model.RefundRequest;
import com.ispan.hestia.model.RefundRequestProvider;
import com.ispan.hestia.model.RoomAvailableDate;
import com.ispan.hestia.model.State;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.CartRoomRepository;
import com.ispan.hestia.repository.OrderDetailsRepository;
import com.ispan.hestia.repository.OrderRefundRecordRepository;
import com.ispan.hestia.repository.OrderRepository;
import com.ispan.hestia.repository.ProviderRepository;
import com.ispan.hestia.repository.RefundRequestProviderRepository;
import com.ispan.hestia.repository.RefundRequestRepository;
import com.ispan.hestia.repository.RoomAvailableDateRepository;
import com.ispan.hestia.repository.StateRepository;
import com.ispan.hestia.repository.UserRepository;
import com.ispan.hestia.util.DateUtil;

@Service
public class OrderService {
	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private StateRepository stateRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private OrderDetailsRepository orderDetailsRepo;

	@Autowired
	private RoomAvailableDateRepository roomADRepo;

	@Autowired
	private OrderRefundRecordRepository orrRepo;

	@Autowired
	private RefundRequestRepository refundReqRepo;

	@Autowired
	private RefundRequestProviderRepository refundReqProvRepo;

	@Autowired
	private RoomAvailableDateRepository roomAvailableDateRepo;

	@Autowired
	private PromotionServiceImpl promotionService;

	@Autowired
	private CartRoomRepository cartRoomRepo;
	
	public boolean exists(Integer id) {
		if (id != null) {
			return orderRepo.existsById(id);
		}
		return false;
	}

	@Autowired
	private ProviderRepository providerRepo;

	public Integer getProviderId(Integer userId) {
		return userRepo.findById(userId).get().getProvider().getProviderId();
	}

	public String getUserEmailByOrderId(Integer orderId) {
		Optional<Order> orderOp = orderRepo.findById(orderId);
		if (orderOp.isPresent()) {
			return orderOp.get().getUser().getEmail();
		}
		return null;
	}

	@Transactional
	public void automaticallyCancelOrders() {// 定期將未付款的訂單改為未付款取消 之後可以把更新OrderDetail改寫
		Date currentTime = new Date();
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(currentTime);
		// calendar.add(Calendar.MINUTE, -2);
		// Date timeMinusTwoMinutes = calendar.getTime();
		Date timeMinusTwoMinutes = new Date(currentTime.getTime() - 2 * 60 * 1000);
		System.out.println("timeMinusTwoMinutes" + timeMinusTwoMinutes);
		System.out.println("currentTime" + currentTime);

		List<Order> unpaidOrders = orderRepo.findUnpaidOrders(timeMinusTwoMinutes);

		for (Order unpaidOrder : unpaidOrders) {
			for (OrderDetails orderDetail : unpaidOrder.getOrderDetails()) {
				RoomAvailableDate roomAvailableDate = orderDetail.getRoomAvailableDate();
				roomAvailableDate.setRoomSum(roomAvailableDate.getRoomSum() + 1);
				// 訂單被取消後要把房間加回去
				roomADRepo.save(roomAvailableDate);
			}
		}
		State uppaidState = stateRepo.findById(30).get(); // 找到未付款狀態
		State uppaidCancelState = stateRepo.findById(33).get(); // 找到未付款取消

		// roomADRepo.updateRoomSum(timeMinusTwoMinutes, uppaidState);// 把房間加回去

		orderRepo.updateUnpaidOrderState(timeMinusTwoMinutes, uppaidState, uppaidCancelState);// 更新所有未付款訂單

		orderDetailsRepo.updateUnpaidOrderDetailsState(timeMinusTwoMinutes, uppaidState, uppaidCancelState); // 更新所有未付款詳細訂單

		// for (Order unpaidOrder : unpaidOrders) {
		// unpaidOrder.setState(state);

		// Set<OrderDetails> orderDetails = unpaidOrder.getOrderDetails();
		// for (OrderDetails orderDetail : orderDetails) {
		// RoomAvailableDate roomAvailableDate = orderDetail.getRoomAvailableDate();
		// roomAvailableDate.setRoomSum(roomAvailableDate.getRoomSum() + 1);
		// // 訂單被取消後要把房間加回去
		// roomADRepo.save(roomAvailableDate);

		// orderDetail.setState(state);
		// orderDetailsRepo.save(orderDetail);
		// }
		// orderRepo.save(unpaidOrder);
		// }
	}

	@Transactional
	public void updateOrderAndCheckCompletion(Integer orderId, Integer roomId, Date checkInDate) {
		// 更新 OrderDetails 的狀態
		int updatedCount = orderDetailsRepo.updateOrderDetailsToComplete(orderId, roomId, checkInDate);
		System.out.println("updatedCount" + updatedCount);
		// 檢查 Order 中的所有 OrderDetails 狀態是否都是 38
		boolean allCompleted = true;
		List<OrderDetails> orderDetailsList = orderDetailsRepo.findByOrderId(orderId);
		for (OrderDetails od : orderDetailsList) {
			if (od.getState().getStateId() != 38) {
				allCompleted = false;
				break;
			}
		}

		// 如果所有狀態都為 38，更新 Order 狀態
		if (allCompleted) {
			int count = orderRepo.updateOrderState(orderId, 38); // 自訂方法更新 Order 狀態
			System.out.println("count" + count);
		}
	}

	@Transactional
	public Page<OrderReservedRoomDTO> findUncompletedOrderUser(Integer userId, Integer currentPage,
			Date startSearchDate, Date endSearchDate) {
		Pageable pageable = PageRequest.of(currentPage, 10, Sort.by("checkInDate").ascending());
		try {
			Date currentTime = new Date();
			System.out.println("有被呼叫");
			return orderRepo.findUncompletedRoomOrderUser(userId, currentTime, pageable, startSearchDate,
					endSearchDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Transactional
	public Page<OrderReservedRoomDTO> findUncompletedOrderProvider(Integer providerId, Integer currentPage,
			Date startSearchDate, Date endSearchDate) {
		Pageable pageable = PageRequest.of(currentPage, 10, Sort.by("checkInDate").ascending());

		try {
			Date currentTime = new Date();
			System.out.println("有被呼叫");
			System.out.println("providerId" + providerId);
			return orderRepo.findUncompletedRoomOrderProvider(providerId, currentTime, pageable, startSearchDate,
					endSearchDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<OrderReservedRoomDetailDTO> findUncompletedOrderDetail(Integer roomId, Integer orderId,
			Date checkInDate) {

		try {
			System.out.println("checkInDate" + checkInDate);
			System.out.println("orderId" + orderId);
			System.out.println("roomId" + roomId);
			return orderRepo.checkUncompletedRoomOrderDetail(roomId, orderId, checkInDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Transactional
	public Integer checkIfRoomIsEnoughForOrder(Integer orderId) {
		Optional<Order> orderOptional = orderRepo.findById(orderId);
		if (orderOptional.isEmpty()) {
			System.out.println("is empty");
			return 1; // 訂單不存在
		}
		Order order = orderOptional.get();
		boolean enough = true;
		for (OrderDetails orderDetail : order.getOrderDetails()) {
			RoomAvailableDate rad = orderDetail.getRoomAvailableDate();
			if (rad.getRoomSum() == 0) {
				return 2; // 訂單中有房間的數量不足

			}
		}
		if (enough) {
			return 3;// 房間數量足夠
		}
		return 2; // 訂單中有房間的數量不足
	}

	@Transactional
	public boolean updateOrderStateToSuccess(Integer orderId) {// 完成付款 把狀態改為已付款
		try {
			// 確認訂單是否存在
			Optional<Order> orderOptional = orderRepo.findById(orderId);
			if (orderOptional.isEmpty()) {
				System.out.println("is empty");
				return false;
			}

			// 獲取目標狀態
			State state = stateRepo.findById(31).get(); // 已付款

			// 更新訂單及其詳細信息的狀態
			Order order = orderOptional.get();
			System.out.println("order id: " + order.getOrderId());
			order.setState(state);
			for (OrderDetails orderDetail : order.getOrderDetails()) {
				orderDetail.setState(state);
				orderDetailsRepo.save(orderDetail);
				RoomAvailableDate rad = orderDetail.getRoomAvailableDate();
				rad.setRoomSum(rad.getRoomSum() - 1);
				roomADRepo.save(rad);
			}

			// 保存訂單（會自動級聯保存 OrderDetails）
			orderRepo.save(order);

			return true;

		} catch (Exception e) {
			// 記錄錯誤，避免無處理的例外
			e.printStackTrace();
			return false;
		}
	}

	// @Transactional
	// public void updateOrderStateToSuccess(Integer orderId) {// 完成付款 把狀態改為成功
	// Optional<Order> orderOptional = orderRepo.findById(orderId);
	// if (orderOptional != null) {

	// State state = stateRepo.findById(31).get();
	// Order order = orderOptional.get();
	// Set<OrderDetails> orderDetails = order.getOrderDetails();
	// for (OrderDetails orderDetail : orderDetails) {
	// orderDetail.setState(state);
	// orderDetailsRepo.save(orderDetail);
	// }
	// order.setState(state);

	// orderRepo.save(order);

	// }

	// }

	@Transactional
	public boolean checkIfOrderExist(Integer orderId) {
		if (orderRepo.findById(orderId).isPresent()) {
			return true;
		}
		return false;
	}

	@Transactional
	public List<OrderProviderTopSellingRoomsDTO> getTopSellingRooms(Date startDate, Date endDate, Integer providerId) {
		Pageable pageable = PageRequest.of(0, 10);
		return orderRepo.getTopSellingRooms(startDate, endDate, providerId, pageable);
	}

	@Transactional
	public List<OrderProviderMostOrderedRoomsDTO> getMostOrderedRooms(Date startDate, Date endDate,
			Integer providerId) {
		Pageable pageable = PageRequest.of(0, 10);
		return orderRepo.getMostOrderedRooms(startDate, endDate, providerId, pageable);
	}

	@Transactional // 按照月份提供收入報表
	public SalesNumbersSumDTO getMonthlySalesAndOrders(Date startDate, Date endDate, Integer providerId) {

		List<SalesNumbersDTO> salesNumbers = orderRepo.getMonthlySalesAndOrdersAvailableDate(startDate, endDate,
				providerId);
		SalesNumbersSumDTO salesNumbersSumDTO = null;
		for (Object[] obj : orderRepo.getTotalSalesAndOrders(startDate, endDate,
				providerId)) {
			salesNumbersSumDTO = new SalesNumbersSumDTO(salesNumbers, (Long) obj[1], (Long) obj[2]);
			System.out.println(obj[1]);
			System.out.println(obj[2]);
		}
		// salesNumbersSumDTO = new SalesNumbersSumDTO(salesNumbers, (Long) obj[1],
		// (Long) obj[2]);
		// salesNumbers.add(orderRepo.getTotalSalesAndOrders(startDate, endDate,
		// providerId));

		return salesNumbersSumDTO;
	}

	@Transactional
	public Page<OrderUserDTO> getRoomSum(Date startDate, Date endDate, Integer userId, Integer stateId,
			String searchInput, Integer pageNum, Integer pageSize) {
		return orderRepo.findOrderSumForUser(startDate, endDate, userId, stateId, searchInput, pageNum, pageSize);
	}

	@Transactional // 查詢使用者訂單
	public Page<OrderUserDTO> findUserOrders(Date startDate, Date endDate, Integer userId, Integer stateId,
			String searchInput, Integer pageNum, Integer pageSize) {
		return orderRepo.findOrderForUserPage(startDate, endDate, userId, stateId, searchInput, pageNum, pageSize);
	}

	@Transactional // 查詢房東訂單
	public Page<OrderProviderDTO> findProviderOrders(Date startDate, Date endDate, Integer providerId, Integer stateId,
			String searchInput, Integer pageNum, Integer pageSize) {
		return orderRepo.findOrderForProviderPage(startDate, endDate, providerId, stateId, searchInput, pageNum,
				pageSize);
	}

	@Transactional // 檢查是否符合退款資格
	public boolean checkIfAutoRefundable(Integer orderId) {
		Date currentTime = new Date();
		Date orderDate = new Date(orderRepo.checkOrderDate(orderId).getTime() + 5 * 60 * 1000);// orderDate
																								// 找到訂單時間然後加5分鐘再去比較
		if (orderDate.compareTo(currentTime) < 0) {
			System.out.println("orderDate" + orderDate);
			System.out.println("currentTime" + currentTime);
			return false;
		}
		return true;
	}

	@Transactional // 自動退款
	public boolean applyAutoRefundOrder(Integer orderId, Integer preState, Integer postState) {
		try {
			// 確認訂單是否存在
			Optional<Order> orderOptional = orderRepo.findById(orderId);
			if (orderOptional.isEmpty()) {
				return false;
			}
			Order order = orderOptional.get();
			State successState = stateRepo.findById(preState).get();// 找到原本的狀態
			State refundingState = stateRepo.findById(postState).get();// 找到退款中的狀態
			State refundRequestState = stateRepo.findById(41).get();// 退款申請 通過 狀態
			System.out.println("壞掉0");
			RefundRequest refundRequest = new RefundRequest();
			refundRequest.setOrder(order);
			refundRequest.setDate(new Date());
			refundRequest.setUser(order.getUser());
			System.out.println("壞掉1");
			Integer totalPriceRefund = 0;
			for (OrderDetails od : order.getOrderDetails()) {
				totalPriceRefund += od.getPurchasedPrice();
			}
			refundRequest.setTotalPriceRefund(totalPriceRefund);
			refundRequest.setState(refundRequestState);
			refundReqRepo.save(refundRequest);
			System.out.println("壞掉2");
			order.setState(refundingState);
			int updated = orderDetailsRepo.updateOrderDetailsState(orderId, successState, refundingState);
			System.out.println("共更新了 " + updated + " 筆");
			orderRepo.save(order);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Transactional
	public boolean applyRefundOrderDetails(Integer orderRoomId, String refundReason) {
		Optional<OrderDetails> orderDetailsOp = orderDetailsRepo.findById(orderRoomId);
		if (orderDetailsOp.isEmpty()) {
			return false;
		}
		try {

			State refundAppliedState = stateRepo.findById(40).get();
			OrderDetails orderDetails = orderDetailsOp.get();

			orderDetails.setActiveRefundRequest(1);
			Order order = orderDetails.getOrder();
			boolean orderDetailsCheck = true;
			for (OrderDetails result : order.getOrderDetails()) {
				if (result.getActiveRefundRequest() == 0) {
					orderDetailsCheck = false;
					break;
				}

			}
			if (orderDetailsCheck) {
				order.setActiveRefundRequest(1);
			}
			RefundRequest refundRequest = new RefundRequest();

			// Integer totalPriceRefund = 0;
			Provider provider = orderDetails.getRoomAvailableDate().getRoom().getProvider();

			refundRequest.setOrderDetails(orderDetails);
			refundRequest.setDate(new Date());
			refundRequest.setRefundReason(refundReason);
			refundRequest.setState(refundAppliedState);
			refundRequest.setTotalPriceRefund(orderDetails.getPurchasedPrice());
			refundRequest.setUser(orderDetails.getOrder().getUser());
			refundReqRepo.save(refundRequest);

			RefundRequestProvider refundRequestProv = new RefundRequestProvider();
			refundRequestProv.setProvider(provider);
			refundRequestProv.setState(refundAppliedState);
			refundRequestProv.setRefundRequest(refundRequest);
			refundRequestProv.setTotalPriceRefund(orderDetails.getPurchasedPrice());
			refundReqProvRepo.save(refundRequestProv);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	@Transactional
	public boolean approveOrDenialOrderDetailRefund(Integer providerId, Integer refundReqId, Integer stateId) {
		State preRefState = stateRepo.findById(31).get();// 狀態 已付款
		State postRefState = stateRepo.findById(36).get();// 狀態 退款中

		State preRefundReqState = stateRepo.findById(40).get();
		State postRefundReqState = stateRepo.findById(stateId).get();// 狀態 退款中
		Optional<RefundRequest> refundReqOp = refundReqRepo.findById(refundReqId);
		Optional<Provider> providerOp = providerRepo.findById(providerId);
		if (providerOp.isEmpty() || refundReqOp.isEmpty()) {
			return false;
		}
		try {
			Provider provider = providerOp.get();
			RefundRequest refundReq = refundReqOp.get();// 把 refReqProv 改成 postState
			for (RefundRequestProvider refReqProv : refundReq.getRefundRequestProvider()) {
				if (refReqProv.getProvider().equals(provider)) {
					refReqProv.setState(postRefundReqState);
				}
			}
			refundReq.setState(postRefundReqState);

			OrderDetails orderDetails = refundReq.getOrderDetails();
			Order order = orderDetails.getOrder();
			orderDetails.setActiveRefundRequest(0);
			boolean refundReqCheck = true;
			for (OrderDetails orderDetail : order.getOrderDetails()) {
				if (orderDetail.getActiveRefundRequest() == 1) {
					refundReqCheck = false;
					break;
				}
			}
			if (refundReqCheck) {
				order.setActiveRefundRequest(0);
			}

			if (stateId == 41) {
				orderDetails.setState(postRefState);
				boolean checkState = true;
				for (OrderDetails orderDetail : orderDetails.getOrder().getOrderDetails()) {
					if (!orderDetail.getState().equals(postRefState)) {
						checkState = false;
						break;
					}
				}
				if (checkState) {
					orderDetails.getOrder().setState(postRefState);
				}
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	@Transactional
	public boolean applyRefundOrder(Integer orderId, String refundReason) {
		Optional<Order> orderOptional = orderRepo.findById(orderId);
		if (orderOptional.isEmpty()) {
			return false;
		}

		try {
			Order order = orderOptional.get();
			RefundRequest refundRequest = new RefundRequest();
			order.setActiveRefundRequest(1);
			State successState = stateRepo.findById(31).get(); // 已付款
			Integer totalPriceRefund = 0;
			for (OrderDetails orderDetail : order.getOrderDetails()) {
				if (orderDetail.getState().equals(successState)) {
					orderDetail.setActiveRefundRequest(1);
					totalPriceRefund += orderDetail.getPurchasedPrice();
				}
			}
			// 找到 退款申請中
			State refundAppliedState = stateRepo.findById(40).get();

			refundRequest.setOrder(order);
			refundRequest.setDate(new Date());
			refundRequest.setRefundReason(refundReason);
			refundRequest.setState(refundAppliedState);
			refundRequest.setTotalPriceRefund(totalPriceRefund);
			refundRequest.setUser(order.getUser());
			refundReqRepo.save(refundRequest);
			Set<Provider> providers = new HashSet<>();
			Map<Provider, Integer> refundAmout = new HashMap<>();
			for (OrderDetails singleOD : order.getOrderDetails()) {
				Provider provider = singleOD.getRoomAvailableDate().getRoom().getProvider();
				providers.add(provider);
				if (refundAmout.get(provider) == null) {
					refundAmout.put(provider, singleOD.getPurchasedPrice());
				} else {
					Integer amount = refundAmout.get(provider) + singleOD.getPurchasedPrice();
					refundAmout.put(provider, amount);
				}

			}

			for (Provider provider : providers) {
				RefundRequestProvider refundRequestProv = new RefundRequestProvider();
				refundRequestProv.setProvider(provider);
				refundRequestProv.setState(refundAppliedState);
				refundRequestProv.setRefundRequest(refundRequest);
				refundRequestProv.setTotalPriceRefund(refundAmout.get(provider));
				refundReqProvRepo.save(refundRequestProv);
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Transactional // 改成42是拒絕 改成41是同意
	public boolean approveOrDenialOrderRefund(Integer providerId, Integer refundReqId, Integer stateId) {
		State preRefState = stateRepo.findById(31).get();// 狀態 已付款
		State postRefState = stateRepo.findById(36).get();// 狀態 退款完成

		State preRefundReqState = stateRepo.findById(40).get();
		State postRefundReqState = stateRepo.findById(stateId).get();// 狀態 退款中
		Optional<RefundRequest> refundReqOp = refundReqRepo.findById(refundReqId);
		Optional<Provider> providerOp = providerRepo.findById(providerId);

		if (providerOp.isEmpty() || refundReqOp.isEmpty()) {
			return false;
		}
		try {
			Provider provider = providerOp.get();
			RefundRequest refundReq = refundReqOp.get();// 把 refReqProv 改成 postState
			for (RefundRequestProvider refReqProv : refundReq.getRefundRequestProvider()) {
				if (refReqProv.getProvider().equals(provider)) {
					refReqProv.setState(postRefundReqState);
				}
			}
			Order order = refundReq.getOrder();
			Set<OrderDetails> orderDetails = order.getOrderDetails();// 檢查 如果是這個Provider 就把進行中的退款改成否
																		// 也就是改成0 (1代表有)
			for (OrderDetails orderDetail : orderDetails) {
				if (orderDetail.getRoomAvailableDate().getRoom().getProvider().equals(provider)) {
					orderDetail.setActiveRefundRequest(0);
				}
				if (orderDetail.getRefundRequest().size() > 0) {
					refundReqRepo.updateRelatedRefund(preRefundReqState, postRefundReqState, orderDetail);
				}
			}
			// 檢查 Order 中 orderDetail是不是都有 ActiveRefundRequest
			boolean orderActiveRefundRequest = true;
			for (OrderDetails orderDetail : orderDetails) {
				if (orderDetail.getActiveRefundRequest() == 1) {
					orderActiveRefundRequest = false;
					break;
				}
			}
			if (orderActiveRefundRequest) {
				order.setActiveRefundRequest(0);
			}

			boolean check = true; // 檢查退款申請中所有 provider表
			for (RefundRequestProvider refReqProv : refundReq.getRefundRequestProvider()) {
				if (!refReqProv.getState().equals(postRefundReqState)) {
					check = false;
					break;
				}
			}
			if (stateId == 41) {
				// int count = orderDetailsRepo.orderRefundProciderApproved(order, preRefState,
				// postRefState, provider);
				for (OrderDetails orderDetail : order.getOrderDetails()) {
					if (orderDetail.getRoomAvailableDate().getRoom().getProvider().equals(provider)
							&& orderDetail.getState().equals(preRefState)) {
						orderDetail.setState(postRefState);
					}
				}
				order = orderRepo.findById(order.getOrderId()).orElseThrow();
				// System.out.println("更新的筆數" + count);
				boolean checkState = true;
				System.out.println("  TEST ");
				int i = 1;
				for (OrderDetails orderDetail : order.getOrderDetails()) {
					System.out.println("i=" + i);

					System.out.println("orderD 狀態:" + !orderDetail.getState().equals(postRefState));
					System.out.println("回圈判別式外");
					System.out.println("orderDetail 狀態" + orderDetail.getState().getStateId());
					System.out.println("postRefState狀態" + postRefState.getStateId());
					if (!orderDetail.getState().equals(postRefState)) {
						checkState = false;
						System.out.println("回圈判別式內");
						System.out.println("orderDetail 狀態" + orderDetail.getState().getStateId());
						System.out.println("postRefState狀態" + postRefState.getStateId());
						System.out.println("檢查 checkState 的狀態 回圈內" + checkState);
						break;
					}

				}

				System.out.println("檢查 checkState 的狀態 回圈外" + checkState);
				if (checkState) {
					order.setState(postRefState);
					order.setActiveRefundRequest(0);
					orderRepo.save(order);
				}
			}
			if (check) {
				refundReq.setState(postRefundReqState);
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	// @Transactional // 申請退款 並且符合退款資格
	// public boolean autoRefund(Integer orderId) {
	// try {
	// // 確認訂單是否存在
	// Optional<Order> orderOptional = orderRepo.findById(orderId);
	// if (orderOptional.isEmpty()) {
	// return false;
	// }
	// Order order = orderOptional.get();
	// State successState = stateRepo.findById(31).get();// 找到完成的狀態
	// State refundingState = stateRepo.findById(36).get();// 找到退款中的狀態

	// order.setState(refundingState);
	// int updated = orderDetailsRepo.updateOrderDetailsState(orderId, successState,
	// refundingState);
	// orderRepo.save(order);
	// return true;
	// } catch (Exception e) {
	// return false;
	// }
	// }

	// @Transactional // 申請退款 不符合退款資格 手動提出退款申請
	// public boolean manualRefund(Integer orderId) {
	// try {
	// // 確認訂單是否存在
	// Optional<Order> orderOptional = orderRepo.findById(orderId);
	// if (orderOptional.isEmpty()) {
	// return false;
	// }
	// Order order = orderOptional.get();
	// State successState = stateRepo.findById(31).get();// 找到完成的狀態
	// State applyingRefundState = stateRepo.findById(34).get();// 找到申請退款的狀態
	// order.setState(applyingRefundState);
	// orderDetailsRepo.updateOrderDetailsState(orderId, successState,
	// applyingRefundState); // 將相應的OrderDetail
	// // 改為申請退款
	// orderRepo.save(order);
	// return true;
	// } catch (Exception e) {
	// return false;
	// }
	// }

	// @Transactional // 申請退款 手動同意退款
	// public boolean manualRefundAccepted(Integer orderId) {
	// try {
	// // 確認訂單是否存在
	// Optional<Order> orderOptional = orderRepo.findById(orderId);
	// if (orderOptional.isEmpty()) {
	// return false;
	// }
	// Order order = orderOptional.get();
	// State applyingRefundState = stateRepo.findById(34).get(); // 找到申請退款的狀態
	// State refundingState = stateRepo.findById(36).get();// 找到退款中的狀態
	// order.setState(refundingState);
	// orderDetailsRepo.updateOrderDetailsState(orderId, applyingRefundState,
	// refundingState);
	// orderRepo.save(order);
	// return true;
	// } catch (Exception e) {
	// return false;
	// }

	// }

	@Transactional // 申請退款 手動拒絕退款
	public boolean manualRefundDeclined(Integer orderId, Integer preState, Integer postState) {
		try {
			// 確認訂單是否存在
			Optional<Order> orderOptional = orderRepo.findById(orderId);
			if (orderOptional.isEmpty()) {
				return false;
			}

			Order order = orderOptional.get();
			OrderRefundRecord orderRefundRecord = new OrderRefundRecord();
			orderRefundRecord.setDate(new Date());
			orderRefundRecord.setOrder(order);
			orrRepo.save(orderRefundRecord);
			State applyingRefundState = stateRepo.findById(preState).get(); // 找到申請退款的狀態
			State successState = stateRepo.findById(postState).get();// 把狀態變回成功
			order.setState(successState);
			orderDetailsRepo.updateOrderDetailsState(orderId, applyingRefundState, successState);
			orderRepo.save(order);
			return true;
		} catch (Exception e) {
			System.err.println("處理訂單失敗，Order ID: " + orderId);
			e.printStackTrace();
			return false;
		}
	}

	/* checkOut */
	public List<OrderDetails> checkOut(JSONArray jsonArr, Integer userId){
		List<CheckOutRequest> requestList = organizeRequest(jsonArr);

		List<OrderDetails> insertOrderDetails = new ArrayList<>();
		if( userId != null){
			Optional<User> optUser = userRepo.findById(userId);
			State dbState = stateRepo.findByStateId(30);
			State soldOutState = stateRepo.findByStateId(26);
			Order dborder = null;
			OrderDetails dborderDetail = null;

			// 確認 user 存在
			if( optUser.isPresent()){
				Order order = new Order();
				order.setUser(optUser.get());
				order.setDate(new Date());
				order.setState(dbState);
				dborder = orderRepo.save(order);
			}else return null;

			if( dborder != null){
				for(CheckOutRequest request:requestList){

					OrderDetails orderDetails = null;
	
					for(Date date: request.dates()){
						orderDetails = new OrderDetails();
	
						RoomAvailableDate dRoomAvailableDate = roomAvailableDateRepo.findAvailableDatesByRoomIdAndDate(request.roomId(), date);
						if(dRoomAvailableDate!= null && dRoomAvailableDate.getState().getStateId()==22) {
							if(dRoomAvailableDate.getRoomSum()==1) {
								dRoomAvailableDate.setState(soldOutState);
								
							}else if(dRoomAvailableDate.getRoomSum()>1){
								dRoomAvailableDate.setRoomSum(dRoomAvailableDate.getRoomSum()-1);
							} 
							orderDetails.setOrder(dborder);
							orderDetails.setRoomAvailableDate(dRoomAvailableDate);
							orderDetails.setRoomAvailableDate(dRoomAvailableDate);
							orderDetails.setCheckInDate(date);
	
							// 如果有 promotion 要重新算價錢
							if(request.promotionCode()!= null){
								Map<String, BigDecimal> map = promotionService.calculateFinalPrice(
									request.roomId(),
									request.promotionCode(),
									request.dates().get(0),
									request.dates().get(request.dates().size() - 1)
								);
		
								// Get the final price from the map
								BigDecimal finalPrice = map.get("finalPrice");
		
								// Calculate the average price
								int numberOfDays = request.dates().size();
								BigDecimal averagePrice = finalPrice.divide(
									BigDecimal.valueOf(numberOfDays), // 將 numberOfDays 轉換為 BigDecimal
									2, // 指定小數位數，例如保留 2 位
									RoundingMode.CEILING // 使用 RoundingMode.CEILING 進行向上取整
								);
	
								// Set the calculated price
								orderDetails.setPurchasedPrice(averagePrice.setScale(0, RoundingMode.CEILING).intValue());
							}else{
								// Set original price
								orderDetails.setPurchasedPrice(dRoomAvailableDate.getPrice());
							}
	
							orderDetails.setState(dbState);
							dborderDetail = orderDetailsRepo.save(orderDetails);
	
							if( dborderDetail != null){
								insertOrderDetails.add(dborderDetail);
							}
						}else{
							return null;
						}
					}
					for(Integer cartId: request.cartId()){
						Optional<CartRoom> opt = cartRoomRepo.findById(cartId);
						if(opt.isPresent()){
							cartRoomRepo.delete(opt.get());
						}
					}
				}
			}
		}
		return insertOrderDetails;
	}

	private List<CheckOutRequest> organizeRequest(JSONArray request){
		List<CheckOutRequest> result= new ArrayList<>();
		Integer roomId;
		List<Date> dates;
		String promotionCode;
		List<Integer> cartId;
		// 迭代 JSON 陣列，轉換為 CheckOutRequest 物件
		for (int i = 0; i < request.length(); i++) {
            JSONObject jsonObject = request.getJSONObject(i);

			roomId = (Integer)jsonObject.getInt("roomId");
			promotionCode = jsonObject.isNull("promotionCode") ? null : jsonObject.getString("promotionCode");
			cartId = new ArrayList<>();

			for( Object obj: jsonObject.getJSONArray("cartId").toList()){
				
				// 將 Object 轉換為 Integer
				cartId.add((Integer) obj);
			}

			dates = new ArrayList<>();
			// for( Object obj: jsonObject.getJSONArray("dates").toList()){
				
			// 	// 將 Object 轉換為 String，然後解析為 Date
			// 	String dateStr = (String) obj;
			// 	Date formattedDate = DateUtil.parseDate(new SimpleDateFormat("yyyy/MM/dd").format(dateStr), "yyyy/MM/dd");
			// 	// Date date = DateUtil.parseDate(dateStr);
			// 	dates.add(formattedDate);
			// }
			// 解析 dates 陣列
			for (Object obj : jsonObject.getJSONArray("dates").toList()) {
				// 直接將 Object 轉換為 String
				String dateStr = (String) obj;
				
				// 使用 SimpleDateFormat 解析字串為 Date
				try {
					Date formattedDate = new SimpleDateFormat("yyyy/MM/dd").parse(dateStr);
					dates.add(formattedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
            result.add(new CheckOutRequest(roomId, dates, promotionCode, cartId));
		}
		return result;
	}
}
