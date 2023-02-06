package com.server.cogito.report.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private String result;

    public ReportResponse(String result) {
        this.result = result;
    }
}
