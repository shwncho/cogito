package com.server.cogito.common.exception.post;

import com.server.cogito.common.exception.ApplicationException;
import com.server.cogito.common.exception.ErrorEnumCode;

public class PostNotFoundException extends ApplicationException {

    private static final PostErrorCode CODE = PostErrorCode.POST_NOT_FOUND;

    private PostNotFoundException(ErrorEnumCode errorEnumCode, String message){
        super(errorEnumCode, message);
    }

    public PostNotFoundException(){
        this(CODE, CODE.getMessage());
    }
}
