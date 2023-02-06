package com.server.cogito.common.exception.report;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class DuplicatedReportPostException extends ApplicationException {

    private static final ReportErrorCode CODE = ReportErrorCode.DUPLICATED_REPORT_POST;

    private DuplicatedReportPostException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }
    public DuplicatedReportPostException(){
        this(CODE,CODE.getMessage());
    }
}
