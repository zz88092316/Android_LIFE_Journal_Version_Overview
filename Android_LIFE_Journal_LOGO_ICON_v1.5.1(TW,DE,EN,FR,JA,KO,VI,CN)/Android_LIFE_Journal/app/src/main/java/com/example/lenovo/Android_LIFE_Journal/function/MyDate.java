package com.example.lenovo.Android_LIFE_Journal.function;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

/*
返回以下格式的時間
2021年12月07日 10:00:00.
 */
public class MyDate {

    //創建一個日曆實例，台灣當地時間
    Calendar calendar = Calendar.getInstance(Locale.TAIWAN);

    public String getDate ()
    {
        //獲取系統的年日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH)+1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String str = year+"年"+month+"月"+day+"日";        //將當地時間格式化

        return str;
    }

    //此方法用來首位補零
    public String addzero(int temp,int len)
    {
        StringBuffer add=new StringBuffer();
        add.append(temp);
        while(add.length()<len)
        {
            add.insert(0,0);
        }
        return add.toString();

    }

    public String getDateAndTime()
    {
        StringBuffer str=new StringBuffer();
        str.append(getDate()).append(" ");
        str.append(this.addzero((calendar.get(calendar.HOUR)+12)%24, 2)).append(":");
        str.append(this.addzero(calendar.get(calendar.MINUTE), 2)).append(":");
        str.append(this.addzero(calendar.get(calendar.SECOND), 2)).append(".");
        return str.toString();
    }
}
