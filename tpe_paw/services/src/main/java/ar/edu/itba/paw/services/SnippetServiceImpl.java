package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.SnippetDao;
import ar.edu.itba.paw.interfaces.SnippetService;
import ar.edu.itba.paw.models.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SnippetServiceImpl implements SnippetService {

    @Autowired
    private SnippetDao snippetDao;

    @Override
    public Collection<Snippet> getSnippetByTag(String tag) {
        return this.snippetDao.getSnippetByTag(tag);
    }
    @Override
    public Snippet getSnippetById(String id) { return snippetDao.getSnippetById(id);}
}