package com.server.cogito.common.exception.report;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class DuplicatedReportCommentException extends ApplicationException {

    private static final ReportErrorCode CODE = ReportErrorCode.DUPLICATED_REPORT_COMMENT;

    private DuplicatedReportCommentException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }

    public DuplicatedReportCommentException(){
        this(CODE,CODE.getMessage());
    }
}
