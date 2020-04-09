package ar.edu.itba.paw.interfaces.dao;

import ar.edu.itba.paw.models.Snippet;

import java.util.Collection;
import java.util.Optional;

public interface SnippetDao {
    Collection<Snippet> findSnippetsByTag(String tag, String source, String userId);
    Collection<Snippet> findSnippetsByTitle(String title, String source, String userId);
    Collection<Snippet> findSnippetsByContent(String content, String source, String userId);
    Collection<Snippet> getAllSnippets();
    Optional<Snippet> getSnippetById(long id);
    Collection<Snippet> getSnippetByTag(String tag);
}
