package com.liskovsoft.youtubeapi.comments

import android.util.Base64
import com.google.gson.Gson
import com.liskovsoft.youtubeapi.common.helpers.PostDataHelper
import java.io.ByteArrayOutputStream
import java.net.URLEncoder

internal object CommentsApiParams {
    private const val COMMENT_ACTION_TEMPLATE: String = "\"actions\":[\"%s\"]"

    fun getCommentsQuery(commentsKey: String): String {
        val chatData = String.format("\"continuation\":\"%s\"", commentsKey)
        return PostDataHelper.createQueryTV(chatData)
    }

    fun getCreateReplyQuery(videoId: String, commentId: String, text: String, web: Boolean): String {
        val data = String.format("\"createReplyParams\":\"%s\",\"commentText\":%s", getCreateReplyParams(videoId, commentId), Gson().toJson(text))
        return if (web) PostDataHelper.createQueryWeb(data) else PostDataHelper.createQueryTV(data)
    }

    /**
     * The TV comments response has no reply endpoint, so the params are built by hand.
     * Protobuf: 2: videoId, 4: commentId, 5: { 1: 0 }, 10: 7 (same layout YouTube.js uses).
     */
    private fun getCreateReplyParams(videoId: String, commentId: String): String {
        val out = ByteArrayOutputStream()
        fun writeVarint(value: Int) {
            var v = value
            while (v and 0x7F.inv() != 0) { out.write((v and 0x7F) or 0x80); v = v ushr 7 }
            out.write(v)
        }
        fun writeString(field: Int, value: String) {
            val bytes = value.toByteArray(Charsets.UTF_8)
            writeVarint(field shl 3 or 2); writeVarint(bytes.size); out.write(bytes)
        }
        writeString(2, videoId)
        writeString(4, commentId)
        writeVarint(5 shl 3 or 2); writeVarint(2); writeVarint(1 shl 3); writeVarint(0)
        writeVarint(10 shl 3); writeVarint(7)
        return URLEncoder.encode(Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP), "UTF-8")
    }

    fun getActionQuery(actionKey: String): String {
        return PostDataHelper.createQueryTV(String.format(COMMENT_ACTION_TEMPLATE, actionKey))
    }
}