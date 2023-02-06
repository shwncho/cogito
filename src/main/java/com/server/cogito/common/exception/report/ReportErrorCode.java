package com.server.cogito.common.exception.report;

import com.server.cogito.common.exception.ErrorEnumCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements ErrorEnumCode {

    DUPLICATED_REPORT_POST("R001","이미 신고된 게시물 입니다."),
    DUPLICATED_REPORT_COMMENT("R002","이미 신고된 댓글 입니다.")
    ;
    private final String code;
    private String message;
}
