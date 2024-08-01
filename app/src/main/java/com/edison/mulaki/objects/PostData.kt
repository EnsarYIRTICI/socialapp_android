package com.edison.mulaki.objects

import org.json.JSONObject

class PostData(
    id: String,
    view1: String,
    view2: String,
    view3: String,
    view4: String,
    view5: String,
    description: String,
    location: String,
    sender_uid: String,
    creation_date: String,
    updated_date: String,
    visible: Boolean,
    edited: Boolean,
    liked: Boolean,
    saved: Boolean,
    likes_count: Int,
    comments_count: Int,
    username: String,
    image: String,
) {
    val id: String
    val view1: String
    val view2: String
    val view3: String
    val view4: String
    val view5: String
    val description: String
    val location: String
    val sender_uid: String
    val creation_date: String
    val updated_date: String
    val visible: Boolean
    val edited: Boolean
    val liked: Boolean
    val saved: Boolean
    val likes_count: Int
    val comments_count: Int
    val username: String
    val image: String

    init {
        this.id = id
        this.view1 = view1
        this.view2 = view2
        this.view3 = view3
        this.view4 = view4
        this.view5 = view5
        this.description = description
        this.location = location
        this.sender_uid = sender_uid
        this.creation_date = creation_date
        this.updated_date = updated_date
        this.visible = visible
        this.edited = edited
        this.liked = liked
        this.saved = saved
        this.likes_count = likes_count
        this.comments_count = comments_count
        this.username = username
        this.image = image
    }

    companion object {
        fun fromJson(json: JSONObject): PostData {
            return PostData(
                id = json.optString("id", ""),
                view1 = json.optString("view1", ""),
                view2 = json.optString("view2", ""),
                view3 = json.optString("view3", ""),
                view4 = json.optString("view4", ""),
                view5 = json.optString("view5", ""),
                description = json.optString("description", ""),
                location = json.optString("location", ""),
                sender_uid = json.optString("sender_uid", ""),
                creation_date = json.optString("creation_date", ""),
                updated_date = json.optString("updated_date", ""),
                visible = json.optBoolean("visible", true),
                edited = json.optBoolean("edited", false),
                liked = json.optBoolean("liked", false),
                saved = json.optBoolean("saved", false),
                likes_count = json.optInt("likes_count", 0),
                comments_count = json.optInt("comments_count", 0),
                username = json.optString("username", ""),
                image = json.optString("image", "")
            )
        }

    }

}