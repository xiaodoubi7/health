package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.PackageDao;
import com.itheima.pojo.Member;
import com.itheima.service.ReportService;
import com.itheima.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private MemberDao memberDao;

    @Override
    public Map<String, Object> getBusinessReportData() {

        Map<String,Object> resultData = new HashMap<>();

        //今日日期
        String today = DateUtils.date2String(DateUtils.getToday(), DateUtils.YMD);
        //本周第一天
        String Monday = DateUtils.date2String(DateUtils.getThisWeekMonday(), DateUtils.YMD);
        //本周最后一天
        String Sunday = DateUtils.date2String(DateUtils.getSundayOfThisWeek(), DateUtils.YMD);
        //本月第一天
        String firstDayOfMonth = DateUtils.date2String(DateUtils.getFirstDayOfThisMonth(), DateUtils.YMD);
        //本月最后一天
        String lastDayOfMonth = DateUtils.date2String(DateUtils.getLastDayOfThisMonth(), DateUtils.YMD);


        //reportDate:null, 日期
        resultData.put("reportDate",today);
        //todayNewMember :0, 本日新增会员数
        Integer todayNewMember = memberDao.findMemberCountAfterDate(today);
        resultData.put("todayNewMember",todayNewMember);
        //totalMember :0, 总会员数
        Integer totalMember = memberDao.findMemberTotalCount();
        resultData.put("totalMember",totalMember);
        //thisWeekNewMember :0, 本周新增会员数
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(Monday);
        resultData.put("thisWeekNewMember",thisWeekNewMember);
        //thisMonthNewMember :0, 本月新增会员数
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDayOfMonth);
        resultData.put("thisMonthNewMember",thisMonthNewMember);
        //todayOrderNumber:0, 今日预约数
        Integer todayOrderNumber = orderDao.findOrderCountAfterDate(today);
        resultData.put("todayOrderNumber",todayOrderNumber);
        //todayVisitsNumber :0, 今日到诊数
        Integer todayVisitsNumber = orderDao.findVisitsCountAfterDate(today);
        resultData.put("todayVisitsNumber",todayVisitsNumber);
        //thisWeekOrderNumber :0, 本周预约数
        Integer thisWeekOrderNumber = orderDao.findOrderCountBetweenDate(Monday,Sunday);
        resultData.put("thisWeekOrderNumber",thisWeekOrderNumber);
        //thisWeekVisitsNumber :0, 本周到诊数
        Integer thisWeekVisitsNumber = orderDao.findVisitsCountAfterDate(Monday);
        resultData.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        //thisMonthOrderNumber :0, 本月预约数
        Integer thisMonthOrderNumber = orderDao.findOrderCountBetweenDate(firstDayOfMonth, lastDayOfMonth);
        resultData.put("thisMonthOrderNumber",thisMonthOrderNumber);
        //thisMonthVisitsNumber :0, 本月到诊数
        Integer thisMonthVisitsNumber = orderDao.findVisitsCountAfterDate(firstDayOfMonth);
        resultData.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        //hotPackage :[ 	{name: 套餐名称,count:预约数量,proportion:占比,remark:备注},
        //                  {name: 套餐名称,count:预约数量,proportion:占比,remark:备注} ]
        List<Map<String, Object>> hotPackage = orderDao.findHotPackage();
        resultData.put("hotPackage",hotPackage);

        return resultData;
    }

    @Override
    public List<Map<String, Object>> getPackageReport() {
        return orderDao.getPackageReport();
    }
}
