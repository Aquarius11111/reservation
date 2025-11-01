package com.example.springboot.exception;

public class ServiceException extends RuntimeException{

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public ServiceException(String message){
        super(message);
    }

    public ServiceException(Integer code, String message){
        super(message); // 这一行要放在最前面，基础知识
        this.code = code;
    }
}
