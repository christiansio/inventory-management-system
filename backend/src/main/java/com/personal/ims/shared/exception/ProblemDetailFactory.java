package com.personal.ims.shared.exception;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class ProblemDetailFactory {

    @Value("${app.problem-occurrences.base-url}")
    private String problemOccurrencesBaseUrl;

    @Value("${app.problems.base-url}")
    private String problemsBaseUrl;

    // Builds an RFC 9457 ProblemDetail from the problem code and optional detail
    public ProblemDetail buildProblemDetail(ProblemCode problemCode, String detail) {

        // Set problem detail status
        ProblemDetail problemDetail = ProblemDetail.forStatus(problemCode.getStatus());

        // if detail is null or blank, use default detail instead
        if (detail != null && !detail.isBlank()) {
            problemDetail.setDetail(detail);
        } else {
            problemDetail.setDetail(problemCode.getDefaultDetail());
        }

        // set problem title, type, instance uri
        problemDetail.setTitle(problemCode.getTitle());
        problemDetail.setType(URI.create(problemsBaseUrl + problemCode.getSlug()));
        problemDetail.setInstance(URI.create(problemOccurrencesBaseUrl + UUID.randomUUID()));

        return problemDetail;
    }

}
