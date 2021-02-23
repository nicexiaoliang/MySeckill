package com.example.myseckill.result;

public class Result<T> {
    private int code;
    private String codeMsg;
    private T data;

    private Result(int code, String codeMsg) {
        this.code=code;
        this.codeMsg=codeMsg;
    }

    private Result(String codeMsg) {
        this.codeMsg=codeMsg;
    }

    private Result(T data) {
        this.data=data;
    }

    private Result(CodeMsg codeMsg) {
        this.codeMsg=codeMsg.getMsg();
        this.code=codeMsg.getCode();
    }

    //    成功时调用
//    值得注意的是，下面这个方法，如果前面不声明<T> 就会报错，static知识
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

//    失败时调用
    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }
}
