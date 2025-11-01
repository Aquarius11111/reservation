package com.example.springboot.common;

import lombok.Data;

@Data
public class Result {
    private static final Integer SUCCESS_CODE = 200;
    private static final Integer ERROR_CODE =-1;
    private Integer code;
    private Object data;
    private String msg;

    //返回成功，无data
    public static Result success(){
        Result result = new Result();
        result.setCode(SUCCESS_CODE);
        return result;
    }
    //返回成功，有data
    public static Result success(Object data){
        Result result = new Result();
        result.setCode(SUCCESS_CODE);
        result.setData(data);
        return result;
    }
    //返回失败，传出错误信息
    public static Result error(String msg){
        Result result = new Result();
        result.setCode(ERROR_CODE);
        result.setMsg(msg);
        return result;
    }
    //返回失败，传出出错码和错误信息
    public static Result error(Integer code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
