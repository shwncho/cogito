package com.server.cogito.common.exception.comment;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class CommentNotFoundException extends ApplicationException {

    private static final CommentErrorCode CODE = CommentErrorCode.COMMENT_NOT_FOUND;

    private CommentNotFoundException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }

    public CommentNotFoundException(){
        this(CODE, CODE.getMessage());
    }
}
