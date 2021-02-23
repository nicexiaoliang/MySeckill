package com.example.myseckill.exception;

import com.example.myseckill.result.CodeMsg;

public class GlobalException extends RuntimeException{
    private static final long servialVersionUID = 1L;

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg=codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return this.codeMsg;
    }

}
