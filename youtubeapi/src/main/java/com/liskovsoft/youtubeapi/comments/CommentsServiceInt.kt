package com.liskovsoft.youtubeapi.comments

import com.liskovsoft.mediaserviceinterfaces.data.CommentGroup
import com.liskovsoft.youtubeapi.comments.gen.getDislikeParams
import com.liskovsoft.youtubeapi.comments.gen.getLikeParams
import com.liskovsoft.youtubeapi.comments.gen.getActiveCommentItem
import com.liskovsoft.youtubeapi.comments.gen.getUnDislikeParams
import com.liskovsoft.youtubeapi.comments.gen.getUnLikeParams
import com.liskovsoft.youtubeapi.comments.gen.isDislikeToggled
import com.liskovsoft.youtubeapi.comments.gen.isLikeToggled
import com.liskovsoft.youtubeapi.comments.impl.CommentGroupImpl
import com.liskovsoft.googlecommon.common.helpers.RetrofitHelper

internal object CommentsServiceInt {
    private val mApi = RetrofitHelper.create(CommentsApi::class.java)

    fun getComments(key: String): CommentGroup? {
        val commentsResult = getCommentsResult(key)
        return commentsResult?.let { CommentGroupImpl(it) }
    }

    fun toggleLike(key: String) {
        val commentsResult = getCommentsResult(key)
        val activeCommentItem = commentsResult?.getActiveCommentItem()
        val likeParam = activeCommentItem?.let { if (it.isLiked == true || it.isLikeToggled()) it.getUnLikeParams() else it.getLikeParams() }
        likeParam?.let { getActionResult(it) }
    }

    fun toggleDislike(key: String) {
        val commentsResult = getCommentsResult(key)
        val activeCommentItem = commentsResult?.getActiveCommentItem()
        val dislikeParam = activeCommentItem?.let { if (it.isDislikeToggled()) it.getUnDislikeParams() else it.getDislikeParams() }
        dislikeParam?.let { getActionResult(it) }
    }

    /** Tries the TV client first (same as the rest of the comments API), then WEB. */
    fun createReply(videoId: String, commentId: String, text: String): Boolean {
        return listOf(false, true).any { web ->
            val response = RetrofitHelper.getResponse(mApi.createCommentReply(CommentsApiParams.getCreateReplyQuery(videoId, commentId, text, web)))
            response?.isSuccessful == true && response.body()?.actionResult?.status.let { it == null || it == "STATUS_SUCCEEDED" }
        }
    }

    private fun getCommentsResult(commentsKey: String) = RetrofitHelper.get(mApi.getComments(CommentsApiParams.getCommentsQuery(commentsKey)))

    private fun getActionResult(actionKey: String) = RetrofitHelper.get(mApi.commentAction(CommentsApiParams.getActionQuery(actionKey)))
}