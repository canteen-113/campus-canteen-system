package com.campus.canteen.controller;

import com.campus.canteen.entity.Employee;
import com.campus.canteen.service.EmployeeService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class StaffPageController {

    private final EmployeeService employeeService;

    public StaffPageController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(value = "/canteen-staff-live", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public String staffPage() {
        try {
            ClassPathResource resource = new ClassPathResource("static/canteen-staff-v3.html");
            String html = resource.getContentAsString(StandardCharsets.UTF_8);

            // 替换外部的 <script src="/api/employees/js"> 为内嵌数据
            List<Employee> list = employeeService.getAll();
            StringBuilder sb = new StringBuilder();
            sb.append("var EMP_DATA=[");
            for (int i = 0; i < list.size(); i++) {
                Employee e = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{id:").append(e.getId())
                  .append(",empNo:").append(jsStr(e.getEmpNo()))
                  .append(",name:").append(jsStr(e.getName()))
                  .append(",position:").append(jsStr(e.getPosition()))
                  .append(",canteenId:").append(e.getCanteenId() == null ? "null" : e.getCanteenId())
                  .append(",phone:").append(jsStr(e.getPhone()))
                  .append(",healthCertExpiry:").append(jsStr(e.getHealthCertExpiry() == null ? null : e.getHealthCertExpiry().toString()))
                  .append(",hireDate:").append(jsStr(e.getHireDate() == null ? null : e.getHireDate().toString()))
                  .append(",status:").append(jsStr(e.getStatus() == null ? null : e.getStatus().name()))
                  .append("}");
            }
            sb.append("];");

            // 计算统计
            long active = 0, expiring = 0;
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate thirtyDaysLater = today.plusDays(30);
            for (Employee e : list) {
                if ("ACTIVE".equals(e.getStatus() != null ? e.getStatus().name() : null)) active++;
                if (e.getHealthCertExpiry() != null &&
                    !e.getHealthCertExpiry().isBefore(today) &&
                    !e.getHealthCertExpiry().isAfter(thirtyDaysLater)) expiring++;
            }
            long workHours = active * 8 * 22; // 估计：每人每天8小时，每月22工作日

            String statsJs = "var STATS={active:" + active + ",expiring:" + expiring +
                    ",todayAttendance:" + active + ",workHours:" + workHours + "};";

            html = html.replace("<script src=\"/api/employees/js\"></script>",
                    "<script>" + sb.toString() + statsJs + "</script>");

            // 添加禁用缓存的 meta 标签
            html = html.replace("<head>", "<head><meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate\"><meta http-equiv=\"Pragma\" content=\"no-cache\"><meta http-equiv=\"Expires\" content=\"0\">");

            return html;
        } catch (Exception ex) {
            return "<html><body><h1>Error: " + ex.getMessage() + "</h1></body></html>";
        }
    }

    @GetMapping(value = "/api/employees/js", produces = "application/javascript;charset=UTF-8")
    public String employeeJs() {
        List<Employee> list = employeeService.getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("var EMP_DATA = [");
        for (int i = 0; i < list.size(); i++) {
            Employee e = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{id:").append(e.getId())
              .append(",empNo:").append(jsStr(e.getEmpNo()))
              .append(",name:").append(jsStr(e.getName()))
              .append(",position:").append(jsStr(e.getPosition()))
              .append(",canteenId:").append(e.getCanteenId() == null ? "null" : e.getCanteenId())
              .append(",phone:").append(jsStr(e.getPhone()))
              .append(",healthCertExpiry:").append(jsStr(e.getHealthCertExpiry() == null ? null : e.getHealthCertExpiry().toString()))
              .append(",hireDate:").append(jsStr(e.getHireDate() == null ? null : e.getHireDate().toString()))
              .append(",status:").append(jsStr(e.getStatus() == null ? null : e.getStatus().name()))
              .append("}");
        }
        sb.append("];");
        return sb.toString();
    }

    private String jsStr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
}
