package com.itheima.service;

import java.util.List;
import java.util.Map;

public interface ReportService {
    Map<String,Object> getBusinessReportData();

    List<Map<String,Object>> getPackageReport();
}
