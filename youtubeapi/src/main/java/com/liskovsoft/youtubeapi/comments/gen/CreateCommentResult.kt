package com.liskovsoft.youtubeapi.comments.gen

internal data class CreateCommentResult(
    val actionResult: ActionResult?
) {
    data class ActionResult(
        /** e.g. STATUS_SUCCEEDED */
        val status: String?
    )
}
