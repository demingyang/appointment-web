package com.appointment.outside.base.common;

/**
 * 返回状态码枚举
 *
 * @author chenzhangyan  on 2018/1/10.
 */
public enum ResponseEnum {
    SUCCESS("操作成功", "0000"),
    DATA_IS_NUll("数据为空", "1000"),
    FAILURE("操作失败", "4000"),
    PARAM_ILLEGAL("参数非法", "4001"),
    INSERT_DB_ERR("入库失败", "4002"),
    NOT_UPDATE_DATA("无更新数据", "4003"),
    EXISTS_SENSITIVE_WORD("存在敏感词汇", "4004"),
    MSG_SEND_FAIL_ERR("推送失败", "4005"),
    INSERT_DB_AFTER_ERR("其他异常", "4006"),
    LOGIN_FAILURE("登录失败", "4007"),
    RESULT_FAIURE("返回值错误", "4008"),
    SYS_ERR("服务器无法访问，请稍后再试", "4009"),
    LOGIN_ACCOUNT_ERROR("输入的工号或密码错误，请重新输入", "4010"),
    LOGIN_DISABLED("您的登录次数过于频繁，请两个小时后重新登录", "4011"),
    MOBILE_ILLEGAL("手机号格式错误，请输入11位手机号", "4012"),
    MOBILE_MISMATCH("手机号与用户信息不符，无法发送短信", "4013"),
    SMS_DISABLED("发送短信次数过多,2小时之后再试", "4014"),
    INPUT_ILLEGAL("输入内容格式错误，请重新输入", "4015"),
    MODIFY_PASSWORD_DISABLED("修改密码尝试次数已用尽,请明天重试", "4016"),
    PASSWORD_ILLEGAL("新老密码不可以相同", "4017"),
    OLD_PASSWORD_MISMATCH("您输入的原密码不正确", "4018"),
    INPUT_CODE_ERROR("输入员工信息错误，请重新输入", "4019"),
    IMAGE_CODE_ERROR("图片验证码错误", "4020"),
    INPUT_CUCODE_ERROR("输入客户信息错误，请重新输入", "4019"),
    CUSTOMER_NON("该手机号尚未注册", "4020"),
    CUSTOMER_HAD("该手机号已存在,请直接登录", "4021"),


    AUCD_NOT_NULL("验证码不能为空", "W00001"),
    AUCD_FAIURE("验证码获取失败", "W00002"),
    AUCD_ERROR_LIMIT("验证码错误次数超出限制", "W00003"),
    AUCD_TIMEOUT("验证码错误,请重新发送验证码", "W00004"),
    AUCD_ERROR("验证码错误", "W00005")
    ;

    ResponseEnum(String message, String status) {
        this.message = message;
        this.status = status;
    }

    private String message;
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
