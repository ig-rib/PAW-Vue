package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.dao.LanguageDao;
import ar.edu.itba.paw.interfaces.service.LanguageService;
import ar.edu.itba.paw.models.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    private LanguageDao languageDao;

    @Override
    public Optional<Language> findById(long id) {
        return languageDao.findById(id);
    }

    @Override
    public Collection<Language> getAll() {
        return languageDao.getAll();
    }
}