package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.dao.ReportDao;
import ar.edu.itba.paw.interfaces.service.EmailService;
import ar.edu.itba.paw.interfaces.service.ReportService;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDao reportDao;
    @Autowired
    private EmailService emailService;

    private static final int MIN_REPUTATION_TO_REPORT = 3;

    @Override
    public Optional<Report> getReport(long userId, long snippetId){
        return this.reportDao.getReport(userId,snippetId);
    }

    @Transactional
    @Override
    public boolean reportSnippet(User user, Snippet snippet, String reportDetail, String baseUrl){
        boolean result = this.reportDao.reportSnippet(user.getId(), snippet.getId(), reportDetail);

        if(result) {
            this.emailService.sendReportedEmail(
                    baseUrl + "/snippet/" + snippet.getId(),
                    snippet.getTitle(),
                    snippet.getOwner().getEmail(),
                    snippet.getOwner().getUsername(),
                    reportDetail,
                    user.getUsername(),
                    user.getLocale());
        }

        return result;
    }

    @Override
    public boolean canReport(User user) {
        return user.getReputation() >= MIN_REPUTATION_TO_REPORT;
    }
}
