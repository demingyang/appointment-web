package com.appointment.outside.base.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyn
 * @create 2017/9/22
 *
 * 响应参数
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable{
    private static final long serialVersionUID = -8796566981071025073L;

    /**
     * 成功返回值
     */
    private final static String SUCCESS_CODE = "0000";

    /**
     * 默认失败
     */
    private final static String FAILED_CODE = "9999";

    /**
     * 返回值
     */
    @JsonProperty("status")
    private final String status;
    /**
     * message
     */
    @JsonProperty("message")
    private final String message;

    /**
     * 成功回复报文包含的对象
     */
    @JsonProperty("result")
    private final Object obj;

    /**
     * 其余参数
     */
    @JsonProperty("params")
    private final Map<String, String> params;

    private BaseResponse(final Object obj, final String status, final String message,
                         final Map<String, String> params) {
        this.status = status;
        this.message = message;
        this.obj = obj;
        this.params = params;
    }

    /**
     * 构造成功报文builder
     *
     * @return 成功报文builder
     */
    public static Builder successCustom() {
        return successCustom("操作成功！");
    }

    /**
     * 构造成功报文builder
     *
     * @param message
     * @return
     */
    public static Builder successCustom(final String message) {
        return new Builder(SUCCESS_CODE, message);
    }

    /**
     * 构造错误返回报文builder
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @return 错误返回报文builder
     */
    public static Builder failedCustom(final String errorCode, final String errorMsg) {
        return new Builder(errorCode, errorMsg);
    }

    public static Builder failedCustom(final String errorMsg) {
        return new Builder(FAILED_CODE, errorMsg);
    }

    public static final class Builder {
        /**
         * 返回值
         */
        private final String retCode;
        /**
         * msg
         */
        private final String message;
        /**
         * 其他参数
         */
        private final Map<String, String> params = new HashMap<>();
        /**
         * 任意可json化的对象
         */
        private Object obj;

        private Builder(final String retCode, final String message) {
            this.retCode = retCode;
            this.message = message;
        }

        /**
         * 添加参数信息
         *
         * @param key
         * @param value
         * @return
         */
        public Builder addParam(final String key, final String value) {
            this.params.put(key, value);
            return this;
        }

        /**
         * 设置result obj
         *
         * @param obj
         * @return
         */
        public Builder setObj(final Object obj) {
            this.obj = obj;
            return this;
        }

        /**
         * build BaseResponse
         *
         * @return
         */
        public BaseResponse build() {
            return new BaseResponse(this.obj == null ? "" : this.obj,
                    this.retCode, this.message, this.params);
        }
    }
}
