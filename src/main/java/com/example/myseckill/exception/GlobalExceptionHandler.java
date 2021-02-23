package com.example.myseckill.exception;

import com.example.myseckill.result.CodeMsg;
import com.example.myseckill.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandle(Exception e) {
        e.printStackTrace();
        System.out.println("##############@@@@@@@@^^^###########");
        System.out.println("处理异常中");
        if (e instanceof GlobalException) {
            return Result.error(((GlobalException) e).getCodeMsg());
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
