package com.example.demo.asyndownload;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class TestExportExcel extends BaseDTO{
    @ExcelProperty("客户名称")
    private String partnerName;
    //    @ExcelProperty("身份证号码")
//    private String partnerIdNumber;
    @ExcelProperty("手机号")
    private String partnerMobile;
    @ExcelProperty("预约日期")
    @DateTimeFormat("yyyy-MM-dd")
    private Date meetingDate;
    @ExcelProperty("赴约人数")
    private Integer mealsCount;
    @ExcelProperty("是否需要午餐")
    private String needMeals;
    @ExcelProperty("大区")
    private String regionalName;
    @ExcelProperty("意向区域")
    private String area;
    @ExcelProperty("面谈类型")
    private String meetingTypeName;
    @ExcelProperty("面谈性质")
    private String meetingProperty;
    @ExcelProperty("项目类型")
    private String projectTypeName;
    @ExcelProperty("面谈负责人")
    private String managerName;
    @ExcelProperty("审批状态")
    private String ticketResult;
}
