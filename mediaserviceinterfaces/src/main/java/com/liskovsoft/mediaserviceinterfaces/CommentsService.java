package com.liskovsoft.mediaserviceinterfaces;

import com.liskovsoft.mediaserviceinterfaces.data.CommentGroup;
import io.reactivex.Observable;

public interface CommentsService {
    Observable<CommentGroup> getCommentsObserve(String key);
    Observable<Void> toggleLikeObserve(String key);
    Observable<Void> toggleDislikeObserve(String key);
    /**
     * Posts a reply to the comment. Emits true when YouTube accepted it.
     */
    Observable<Boolean> createReplyObserve(String videoId, String commentId, String text);
}
