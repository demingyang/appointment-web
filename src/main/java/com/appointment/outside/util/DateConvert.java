package com.appointment.outside.util;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
  * 对象方式接受页面参数,字符串转日期格式
  * ClassName: DateConvert.
  * User: lyc
  * Date: 2017/9/16
  * Time: 18:29
 */

public class DateConvert implements Converter<String, Date> {
    @Override
    public Date convert(String stringDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
