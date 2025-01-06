package com.ispan.hestia.repository.impl;

import java.util.List;

import org.json.JSONObject;

import com.ispan.hestia.model.Comment;

public interface CommentDAO {
    public abstract long count(JSONObject obj);
    public abstract List<Comment> find(JSONObject obj);
}
