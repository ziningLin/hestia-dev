package com.ispan.hestia.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import com.ispan.hestia.model.Favorite;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class FavoriteDAOImpl implements FavoriteDAO {
    @PersistenceContext
    private EntityManager em;

    @Override
    public long count(JSONObject obj) throws JSONException {
        Integer roomId = obj.isNull("roomId") ? null : obj.getInt("roomId");
        Integer userId = obj.isNull("userId") ? null : obj.getInt("userId");

        // Select count(*) from CartRoom where....
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // from comment
        Root<Favorite> table = criteriaQuery.from(Favorite.class);

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
            Predicate p = criteriaBuilder.equal(table.get("user").get("userId"), userId);
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
}
