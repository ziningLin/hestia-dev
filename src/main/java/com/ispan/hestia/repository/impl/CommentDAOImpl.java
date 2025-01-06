package com.ispan.hestia.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.ispan.hestia.model.Comment;
import com.ispan.hestia.util.DateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class CommentDAOImpl implements CommentDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public long count(JSONObject obj) throws JSONException {
        Integer roomId = obj.isNull("roomId") ? null : obj.getInt("roomId");
        Integer userId = obj.isNull("userId") ? null : obj.getInt("userId");
        Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");
        Integer orderRoomId = obj.isNull("orderRoomId") ? null : obj.getInt("orderRoomId");

        // Select count(*) from comment where....
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // from comment
        Root<Comment> table = criteriaQuery.from(Comment.class);

        // SELECT count(*)
        criteriaQuery.select(criteriaBuilder.count(table));

        List<Predicate> predicates = new ArrayList<>();

        // roomId =?
        if (roomId != null) {
            Predicate p = criteriaBuilder.equal(table.get("room").get("roomId"), roomId);
            predicates.add(p);
        }

        // userId =?
        if (userId != null) {
            Predicate p = criteriaBuilder.equal(table.get("order").get("user").get("userId"), userId);
            predicates.add(p);
        }

        // orderId =?
        if (orderId != null) {
            Predicate p = criteriaBuilder.equal(table.get("order").get("orderId"), orderId);
            predicates.add(p);
        }

        // orderRoomId =?
        if (orderRoomId != null) {
            Predicate p = criteriaBuilder.equal(table.get("orderRoomId"), orderRoomId);
            predicates.add(p);
        }

        // where
        if (predicates != null && !predicates.isEmpty()) {
            Predicate[] arr = predicates.toArray(new Predicate[0]);
            criteriaQuery = criteriaQuery.where(arr);
        }

        TypedQuery<Long> typedQuery = em.createQuery(criteriaQuery);
        Long result = typedQuery.getSingleResult();
        if (result != null) {
            return result.longValue();
        }
        return 0;
    }

    @Override
    public List<Comment> find(JSONObject obj) throws JSONException{
        Integer commentId = obj.isNull("commentId") ? null : obj.getInt("commentId");
        Integer roomId = obj.isNull("roomId") ? null : obj.getInt("roomId");
        Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");
        Integer cleanessScore = obj.isNull("cleanessScore") ? null : obj.getInt("cleanessScore");
        Integer comfortScore = obj.isNull("comfortScore") ? null : obj.getInt("comfortScore");
        Integer locationScore = obj.isNull("locationScore") ? null : obj.getInt("locationScore");
        Integer facilityScore = obj.isNull("facilityScore") ? null : obj.getInt("facilityScore");
        Integer pationessScore = obj.isNull("pationessScore") ? null : obj.getInt("pationessScore");
        Integer score = obj.isNull("score") ? null : obj.getInt("score");
        String commentDate = obj.isNull("commentDate") ? null : obj.getString("commentDate");
		String startDate = obj.isNull("startDate") ? null : obj.getString("startDate");
		String endDate = obj.isNull("endDate") ? null : obj.getString("endDate");

		Integer start = obj.isNull("start") ? null : obj.getInt("start");
		Integer max = obj.isNull("max") ? null : obj.getInt("max");
		String order = obj.isNull("order") ? "commentDate" : obj.getString("order");
		boolean desc = obj.isNull("desc") ? true : obj.getBoolean("desc");

//		select * from Comment where .... order by ...
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Comment> criteriaQery = criteriaBuilder.createQuery(Comment.class);
		
//		from Comment
		Root<Comment> table = criteriaQery.from(Comment.class);
		
		List<Predicate> predicates = new ArrayList<>();

//		commentId=?
		if(commentId!=null) {
			Predicate p = criteriaBuilder.equal(table.get("commentId"), commentId);
			predicates.add(p);
		}

//		orderId=?
		if(orderId!=null) {
			Predicate p = criteriaBuilder.equal(table.get("order").get("orderId"), orderId);
			predicates.add(p);
		}

//		roomId=?
		if(roomId!=null) {
			Predicate p = criteriaBuilder.equal(table.get("room").get("roomId"), roomId);
			predicates.add(p);
		}
		
//		cleanessScore > ?
		if(cleanessScore!=null) {
			Predicate p = criteriaBuilder.greaterThan(table.get("cleanessScore"), cleanessScore);
			predicates.add(p);
		}

//		comfortScore > ?
        if(comfortScore!=null) {
            Predicate p = criteriaBuilder.greaterThan(table.get("comfortScore"), comfortScore);
            predicates.add(p);
        }
		
//		locationScore > ?
        if(locationScore!=null) {
            Predicate p = criteriaBuilder.greaterThan(table.get("locationScore"), locationScore);
            predicates.add(p);
        }

//		facilityScore > ?
        if(facilityScore!=null) {
            Predicate p = criteriaBuilder.greaterThan(table.get("facilityScore"), facilityScore);
            predicates.add(p);
        }

//		pationessScore > ?
        if(pationessScore!=null) {
            Predicate p = criteriaBuilder.greaterThan(table.get("pationessScore"), pationessScore);
            predicates.add(p);
        }

//		score > ?
        if(score!=null) {
            Predicate p = criteriaBuilder.greaterThan(table.get("score"), score);
            predicates.add(p);
        }
		
//		Date > ?
        if(startDate!=null && startDate.length()!=0) {
            java.util.Date date = DateUtil.parseDate(startDate, "yyyy-MM-dd");
            Predicate p = criteriaBuilder.greaterThan(table.get("commentDate"), date);
            predicates.add(p);
        }

//		Date < ?
        if(endDate!=null && endDate.length()!=0) {
            java.util.Date date = DateUtil.parseDate(endDate, "yyyy-MM-dd");
            Predicate p = criteriaBuilder.greaterThan(table.get("commentDate"), date);
            predicates.add(p);
        }
		
//		where
		if(predicates!=null && !predicates.isEmpty()) {
			Predicate[] array = predicates.toArray(new Predicate[0]);
			criteriaQery = criteriaQery.where(array);
		}

//		order by
		if(desc) {
			criteriaQery = criteriaQery.orderBy(criteriaBuilder.desc(table.get(order)));
		} else {
			criteriaQery = criteriaQery.orderBy(criteriaBuilder.asc(table.get(order)));
		}
		
		
		TypedQuery<Comment> typedQuery = em.createQuery(criteriaQery);
		if(start!=null) {
			typedQuery = typedQuery.setFirstResult(start);
		}
		if(max!=null) {
			typedQuery = typedQuery.setMaxResults(max);
		}

		List<Comment> result = typedQuery.getResultList();
		if(result!=null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
}
