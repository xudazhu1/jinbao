package com.hnjbkc.jinbao.utils.restresponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @author  siliqiang
 * @date 2019/12/31
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse<T> implements Serializable {
    private static final long serialVersionUID = -4184183427083579380L;
    private int code;
    private String msg;
    private T data;

    private static final int SUCCESS_CODE = 200;
    private static final String SUCCESS_MSG = "success";


    private RestResponse(int code,String msg) {
        this.code = code;
        this.msg = msg;
    }

    private RestResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() { return code; }

    public String getMsg() {
        return msg;
    }

    public T getData(){
        return this.data;
    }

    @JsonIgnore
    public boolean isSuccess(){
        return this.code == SUCCESS_CODE;
    }

    /**
     * 返回默认的成功应答,状态码为200,msg为success,没有data
     * @return RestResponse<String>
     */
    public static <T> RestResponse<T> success(){
        return new RestResponse<>(SUCCESS_CODE,SUCCESS_MSG);
    }

    /**
     * 返回成功应答,状态码为200,有自定义msg,没有data
     * @param  msg 自定义消息
     * @return RestResponse<T>
     */
    public static <T> RestResponse<T> successWithMsg(String msg){
        return new RestResponse<>(SUCCESS_CODE,msg);
    }

    /**
     * 返回成功应答,状态码为200,msg为success,带有data
     * @param  data 返回数据
     * @return RestResponse<T>
     */
    public static <T> RestResponse<T> success(T data){
        return new RestResponse<>(SUCCESS_CODE,SUCCESS_MSG,data);
    }

    /**
     * 返回成功应答,状态码为200,有自定义msg,有data
     * @param msg   自定义消息
     * @param data  返回数据
     * @return RestResponse<T>
     */
    public static <T> RestResponse<T> success(String msg,T data){
        return new RestResponse<>(SUCCESS_CODE, msg, data);
    }


}
