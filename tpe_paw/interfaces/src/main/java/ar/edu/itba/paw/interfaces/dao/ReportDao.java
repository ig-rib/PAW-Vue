package ar.edu.itba.paw.interfaces.dao;

import ar.edu.itba.paw.models.Report;

import java.util.Optional;

public interface ReportDao {

    boolean reportSnippet(long userId, long snippetId, String reportDetail);
    Optional<Report> getReport(long userId, long snippetId);
    boolean isReportedAndNotDismissed(long snippetId);
    void deleteReportsForSnippet(long snippetId, long reporterId);
    void dismissAllReportsForSnippet(long snippetId);
}
