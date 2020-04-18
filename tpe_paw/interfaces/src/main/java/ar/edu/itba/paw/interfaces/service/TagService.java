package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.models.Tag;

import java.util.Collection;

public interface TagService {
    Collection<Tag> getAllTags();
    Collection<Tag> getFollowedTagsForUser(long userId);
    void followTag(long userId, long tagId);
    void unfollowTag(long userId, long tagId);
}