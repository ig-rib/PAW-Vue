package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.dao.SnippetDao;
import ar.edu.itba.paw.interfaces.service.SnippetService;
import ar.edu.itba.paw.models.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class SnippetServiceImpl implements SnippetService {

    @Autowired
    private SnippetDao snippetDao;

    @Override
    public Collection<Snippet> getSnippetByName(String name) { return this.snippetDao.getSnippetByName(name); }

    @Override
    public Collection<Snippet> getSnippetByContent(String code){ return this.snippetDao.getSnippetByContent(code);}

    @Override
    public Optional<Collection<Snippet>> getSnippetByTag(String tag) {
        return this.snippetDao.getSnippetByTag(tag);
    }
    @Override
    public Optional<Snippet> getSnippetById(long id) { return snippetDao.getSnippetById(id);}
}
