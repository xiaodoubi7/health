package com.itheima.quartzJobDetail;

import com.itheima.dao.JobDetailDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyJobDetail {
    @Autowired
    private JobDetailDao jobDetailDao;

    public void timer(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        Date time = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(time);
        jobDetailDao.deleteData(date);
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("当天清理任务结束...");
        System.out.println("-------------------------------------------------------------------------------");
    }
}
