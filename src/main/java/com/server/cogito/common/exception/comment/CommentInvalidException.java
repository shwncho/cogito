package com.server.cogito.common.exception.comment;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class CommentInvalidException extends ApplicationException {

    private static final CommentErrorCode CODE = CommentErrorCode.COMMENT_PARENT_INVALID;

    private CommentInvalidException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }

    public CommentInvalidException(ErrorEnumCode errorEnumCode){
        this(CODE,CODE.getMessage());
    }
}
