package com.appointment.outside.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.appointment.outside.base.common.ResponseEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by YangDeming on 2018/5/4.
 */
@Controller
@RequestMapping("api/public/appointment")
@Api(description = "视频面签对外接口")
public class AppointmentPublicApiController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentPublicApiController.class);

    @Autowired
    private AppointmentService appointmentService;

    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "查询可用时间段与坐席数")
    @RequestMapping(value = "/queryAptList", method = RequestMethod.POST)
    public Map<String, Object> queryAptList(@ApiParam(name = "queryDate", value = "查询的日期，格式：yyyy-MM-dd")
                                                @RequestParam(value = "queryDate") String queryDate) {
        String message = null;
        Map<String, Object> resultMap = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        boolean isValidated = true;

        if (StringUtils.isBlank(queryDate)) {
            isValidated = false;
            resultMap = Tool.resultMap(ResponseEnum.PARAM_ILLEGAL.getStatus(), ResponseEnum.PARAM_ILLEGAL.getMessage());
        } else {
            try {
                dateFormat.parse(queryDate);
            } catch (ParseException e) {
                isValidated = false;
                resultMap = Tool.resultMap(ResponseEnum.PARAM_ILLEGAL.getStatus(), ResponseEnum.PARAM_ILLEGAL.getMessage());
                logger.info("日期格式不合法_", e);
            }
        }
        if (isValidated) {
            String json = appointmentService.queryAptList(queryDate);
            if (StringUtils.isNotBlank(json)) {
                JSONObject jb = JSON.parseObject(json);
                if (jb != null && !jb.isEmpty()) {
                    boolean success = jb.getBooleanValue("success");
                    message = jb.getString("msg");
                    if (success) {
                        resultMap = Tool.resultMap(ResponseEnum.SUCCESS.getStatus(), ResponseEnum.SUCCESS.getMessage());
                        JSONArray ja = jb.getJSONArray("data");
                        JSONObject item = null;
                        List<AppiontmentInfo> aptList = new ArrayList<>();
                        for (int i = 0; i < ja.size(); i++) {
                            item = ja.getJSONObject(i);
                            if (item == null) {
                                continue;
                            }
                            AppiontmentInfo info = new AppiontmentInfo();
                            info.setId(item.getInteger("id"));
                            info.setAppiontmentDate(item.getString("appiontmentDate"));
                            info.setStartTime(item.getString("startTime"));
                            info.setEndTime(item.getString("endTime"));
                            info.setFreeNum(item.getInteger("operatorFreeNum"));
                            aptList.add(info);
                        }
                        resultMap.put("data", aptList);
                    } else {
                        resultMap = Tool.resultMap(CodeConts.FAILURE, message);
                    }
                } else {
                    message = "获取数据失败";
                    resultMap = Tool.resultMap(CodeConts.FAILURE, message);
                }

            } else {
                message = "返回结果为空";
                resultMap = Tool.resultMap(CodeConts.DATA_IS_NUll, message);
            }
        }
        return resultMap;
    }


}
