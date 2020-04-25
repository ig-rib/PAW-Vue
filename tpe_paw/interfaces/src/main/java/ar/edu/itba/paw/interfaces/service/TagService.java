package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.models.Tag;

import java.util.Collection;
import java.util.Optional;

public interface TagService {
    Collection<Tag> getAllTags();
    Collection<Tag> getFollowedTagsForUser(long userId);
    void followTag(long userId, long tagId);
    void unfollowTag(long userId, long tagId);
    Optional<Tag> findTagById(long tagId);
}