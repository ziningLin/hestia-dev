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

import com.ispan.hestia.dto.CommentDTO;
import com.ispan.hestia.dto.CommentRequest;
import com.ispan.hestia.dto.CommentResponse;
import com.ispan.hestia.dto.RoomEvaluationDTO;
import com.ispan.hestia.dto.RoomEvaluationResponse;
import com.ispan.hestia.model.Comment;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.service.impl.CommentService;
import com.ispan.hestia.service.impl.RoomService;


@RestController
@RequestMapping("/order/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private RoomService roomService;

    // 用一個 roomId 新增一筆comment
    @PostMapping("/add")
    public CommentResponse addComment(@RequestBody CommentRequest request) {
        Comment comment = commentService.addComment(request);
        if (comment != null) {
            return new CommentResponse(1, null, true, "新增成功");
        }
        return new CommentResponse(0, null, false, "新增失敗");
    }

    // 多筆模糊查詢
    @PostMapping("/find")
    public CommentResponse findRoomComment(@RequestBody String entity){
        long count = commentService.count(entity);
        
        // 用 roomId 查詢此 room 的相關 Comment
        List<Comment> dbComments = commentService.find(entity);
        
        if (dbComments != null && dbComments.size() != 0) {
            List<CommentDTO> lst = dbComments.stream()
                    .map(comment -> new CommentDTO(
                            comment.getCommentId(),
                            comment.getOrder().getOrderId(),
                            comment.getRoom().getRoomId(),
                            comment.getOrder().getUser().getName(),
                            comment.getOrder().getUser().getPhoto(),
                            comment.getCommentDate(),
                            comment.getCleanessScore(),
                            comment.getComfortScore(),
                            comment.getLocationScore(),
                            comment.getFacilityScore(),
                            comment.getPationessScore(),
                            comment.getRecommendationScore(),
                            comment.getOverallScore(),
                            comment.getUseful(),
                            comment.getCommentContent()))
                    .collect(Collectors.toList());
            return new CommentResponse(count, lst, true, "查詢成功");
        }
        return new CommentResponse(count, null, false, "尚未評價");
    }

    // 查某 room 的平均分
    @GetMapping("/avg/{roomId}")
    public RoomEvaluationResponse getRoomEvaluation(@PathVariable Integer roomId) {

    	// 傳進來的 roomId 沒值
        if (roomId == null) {
            return new RoomEvaluationResponse(null, false, "roomId是必要欄位");
        }

        // 傳進來的 comment 不存在
        else if (!roomService.exists(roomId)) {
            return new RoomEvaluationResponse(null, false, "roomId 不存在");
        }
        
        else {
            List<Room> room = roomService.findAllRoomByRoomId(roomId);
            RoomEvaluationDTO dto = commentService.calculateRoomComment(room.get(0));
            return new RoomEvaluationResponse(dto, true, "查詢成功");
        }

    }
    
    
    // 修改 comment
    @PostMapping("/edit/{commentId}")
    public CommentResponse editCommeomment(@PathVariable Integer commentId, @RequestBody CommentRequest request) {

        // 傳進來的 request 沒值或 commentId 沒值
        if (request == null || commentId == null) {
            return new CommentResponse(0, null, false, "commentId是必要欄位");
        }

        // 傳進來的 comment 不存在
        else if (!commentService.exists(commentId)) {
            return new CommentResponse(0, null, false, "commentId不存在");
        }

        // 都有才繼續做修改動作
        else {
            commentService.editComment(commentId, request);
            return new CommentResponse(1, null, true, "修改成功");
        }
    }

    // 刪除 comment
    @GetMapping("/delete/{commentId}")
    public CommentResponse removeComment(@PathVariable Integer commentId) {
        // 傳進來的 request 沒值或 commentId 沒值
        if (commentId == null) {
            return new CommentResponse(0, null, false, "commentId是必要欄位");
        }

        // 傳進來的 comment 不存在
        else if (!commentService.exists(commentId)) {
            return new CommentResponse(0, null, false, "commentId 不存在");
        }

        // 都有才繼續做修改動作
        else {
            commentService.deleteComment(commentId);
            return new CommentResponse(0, null, true, "刪除成功");
        }
    }
}
