package com.ispan.hestia.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.dto.CommentRequest;
import com.ispan.hestia.dto.RoomEvaluationDTO;
import com.ispan.hestia.model.Comment;
import com.ispan.hestia.model.Order;
import com.ispan.hestia.model.OrderDetails;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.User;
import com.ispan.hestia.repository.CommentRepository;
import com.ispan.hestia.repository.OrderDetailsRepository;
import com.ispan.hestia.repository.OrderRepository;
import com.ispan.hestia.repository.RoomRepository;
import com.ispan.hestia.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CommentService {

	@Autowired
	private CommentRepository commentRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private OrderDetailsRepository orderDetailsRepo;

	@Autowired
	private RoomRepository roomRepo;

	/* 確認此 comment 是否存在 */
	public boolean exists(Integer id) {
		if (id != null) {
			System.out.print(commentRepo.existsById(id));
			return commentRepo.existsById(id);
		}
		return false;
	}

	/* count 此 room 總共有幾筆 comment */
	public long count(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			System.out.println(obj.toString());
			return commentRepo.count(obj);
		} catch (JSONException e) {
			System.err.println("Invalid JSON format: " + e.getMessage());
		}
		return 0;
	}

	/* 新增一則 comment */
	public Comment addComment(CommentRequest request) {

		// 確認傳進來的 request 有東西
		if (request != null) {

			// check this order is exist
			if (request.orderId() != null) {
				Optional<Order> dbOrder = orderRepo.findById(request.orderId());

				// 如果 order存在
				if (dbOrder.isPresent()) {

					// 確認此 room 在此筆 order 中存在
					Set<OrderDetails> dbods = dbOrder.get().getOrderDetails();
					boolean flag = false; // 確認傳進來的 roomId 是否有在 Set 中的開關
					for (OrderDetails dbod : dbods) {
						Integer id = dbod.getRoomAvailableDate().getRoom().getRoomId();
						if (id == request.roomId())
							flag = true;
					}

					// 確認 comment 是否已經被新增過
					Comment dbComment = commentRepo.findByOrderIdAndRoomId(request.orderId(), request.roomId());

					// 傳進來的 roomId 正確 且 comment 沒有在 db 中存在才繼續 save
					if (flag && dbComment == null) {

						Comment comment = new Comment();
						comment.setOrder(dbOrder.get());
						comment.setRoom(roomRepo.findById(request.roomId()).get());
						comment.setCommentDate(new Date());
						comment.setCleanessScore(request.cleanessScore());
						comment.setComfortScore(request.comfortScore());
						comment.setLocationScore(request.locationScore());
						comment.setFacilityScore(request.facilityScore());
						comment.setPationessScore(request.pationessScore());
						comment.setRecommendationScore(request.recommendationScore());
						comment.setOverallScore(calculateCommentOverallScore(request));
						comment.setCommentContent(request.commentContent());
						commentRepo.save(comment);
						return comment;
					}
				}
			}
		}
		return null;
	}

	/* 修改一則 comment */
	public Comment editComment(Integer commentId, CommentRequest request) {

		// 確認傳進來的 commentId 有值
		if (commentId != null) {

			// check if this comment is already exist
			Optional<Comment> dbComment = commentRepo.findById(commentId);

			// 有這筆 OrderDetails 且 db 有這筆 comment 才繼續修改
			if (dbComment.isPresent()) {
				Comment newComment = dbComment.get();
				newComment.setCommentDate(new Date());
				newComment.setCleanessScore(request.cleanessScore());
				newComment.setComfortScore(request.comfortScore());
				newComment.setLocationScore(request.locationScore());
				newComment.setFacilityScore(request.facilityScore());
				newComment.setPationessScore(request.pationessScore());
				newComment.setRecommendationScore(request.recommendationScore());
				newComment.setOverallScore(calculateCommentOverallScore(request));
				newComment.setCommentContent(request.commentContent());
				commentRepo.save(newComment);
				return newComment;
			}
		}
		return null;
	}

	/* 刪除一則comment */
	public Comment deleteComment(Integer commentId) {
		// commentId 有值才繼續做
		if (commentId != null && commentId != 0) {

			// check if this comment is already exist
			Optional<Comment> dbComment = commentRepo.findById(commentId);

			// db 中有這筆 comment 才 delete
			if (dbComment.isPresent()) {
				commentRepo.delete(dbComment.get());
				return dbComment.get();
			}
		}
		return null;
	}

	/* 查詢某 comment */
	public List<Comment> find(String json){
		try {
			JSONObject obj = new JSONObject(json);
			return commentRepo.find(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* 計算某 comment 的評價*/
	public Double calculateCommentOverallScore(CommentRequest request){
		if (request != null){
			return ((request.cleanessScore()
					+request.comfortScore()
					+request.facilityScore()
					+request.locationScore()
					+request.pationessScore()
					+request.recommendationScore())/6.0
			);
		}
		return null;
	}

	/* 計算 room 的各項評價平均分*/
	public RoomEvaluationDTO calculateRoomComment(Room room) {
		List<Comment> comments = commentRepo.findByRoomId(room.getRoomId());
        int cleanessTotalScore = 0;
        int comfortTotalScore = 0;
        int locationTotalScore = 0;
        int facilityTotalScore = 0;
        int pationessTotalScore = 0;
        int recommendationTotalScore = 0;
        Double count = Double.parseDouble(comments.size()+"");
        
        for (Comment comment : comments) {
        	cleanessTotalScore += comment.getCleanessScore() != null ? comment.getCleanessScore() : 0;
        	comfortTotalScore += comment.getComfortScore() != null ? comment.getComfortScore() : 0;
        	locationTotalScore += comment.getLocationScore() != null ? comment.getLocationScore() : 0;
        	facilityTotalScore += comment.getFacilityScore() != null ? comment.getFacilityScore() : 0;
        	pationessTotalScore += comment.getPationessScore() != null ? comment.getPationessScore() : 0;
        	recommendationTotalScore += comment.getPationessScore() != null ? comment.getPationessScore() : 0;
        }
        
        // 計算平均分數
        double overallTotalScore = (cleanessTotalScore
                + comfortTotalScore
                + locationTotalScore
                + facilityTotalScore
                + pationessTotalScore
                + recommendationTotalScore)/ count / 6;

        // 格式化為小數點後兩位
        String formatted = String.format("%.1f", overallTotalScore);

        // 將格式化的字串轉換回 double
        double formattedDouble = Double.parseDouble(formatted);
        
        RoomEvaluationDTO dto = new RoomEvaluationDTO(
        		cleanessTotalScore/count,
        		comfortTotalScore/count,
        		locationTotalScore/count,
        		facilityTotalScore/count,
        		pationessTotalScore/count,
        		recommendationTotalScore/count,
        		formattedDouble);
        
        return dto;  // Calculate average score
	}

}
