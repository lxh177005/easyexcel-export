package com.xingxin.template;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 采用阿里easyExcel流式导出方式，需要继承该类并实现getData方法；
 * 适用于数据量较大，需要分页查询导出的，可参考com.dtminds.bmall.admin.controller.ZhuGeUserGroupController#exportZhugeUserGroup1；
 * 如果数据量不大，可以一次性加载到内存的，可以参考com.xingxin.utils.EasyExcelUtils#export2Web；
 *
 * @author liuxh
 * @date 2022/3/30
 */
@Slf4j
@Component
public abstract class AbstractExportTemplate<T> {

    private static final int DEFAULT_EXPORT_PAGE_SIZE = 100;
    private static final int EXPORT_TIME_OUT = 3 * 60 * 1000;
    private static final int EASY_EXCEL_EXPORT_KEY_EXPIRE = 5 * 60 * 1000;
    private static final String EASY_EXCEL_EXPORT_KEY = "easyExcelExportKey";

    @Resource
    private RedissonClient redissonClient;


    /**
     * 需要实现该方法，获取数据
     *
     * @param current   当前页
     * @param pageSize  页码
     * @param condition 过滤条件
     * @return data
     */
    protected abstract List<T> getData(Integer current, Integer pageSize, Object condition);

    public void defaultEasyExcelExport(HttpServletResponse response, Class tClass, String excelName) {
        this.easyExcelExport(response, tClass, excelName, excelName, ExcelTypeEnum.XLSX.getValue(), DEFAULT_EXPORT_PAGE_SIZE, null);
    }

    public void defaultEasyExcelExport(HttpServletResponse response, Class tClass, String excelName, Object condition) {
        this.easyExcelExport(response, tClass, excelName, excelName, ExcelTypeEnum.XLSX.getValue(), DEFAULT_EXPORT_PAGE_SIZE, condition);
    }

    public void defaultEasyExcelExport(HttpServletResponse response, Class tClass, String excelName, Integer pageSize, Object condition) {
        this.easyExcelExport(response, tClass, excelName, excelName, ExcelTypeEnum.XLSX.getValue(), pageSize, condition);
    }

    public void easyExcelExport(HttpServletResponse response, Class tClass, String excelName, String sheetName,
                                String excelType, Integer pageSize, Object condition) {
        log.info("easyExcelExport start... excelName={}, pageSize={}, condition={}", excelName, pageSize, JSON.toJSONString(condition));
        long start = System.currentTimeMillis();
        ExcelWriter excelWriter = null;
        RLock lock = redissonClient.getLock(EASY_EXCEL_EXPORT_KEY);
        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(EASY_EXCEL_EXPORT_KEY_EXPIRE, TimeUnit.SECONDS);
            if (!tryLock) {
                log.info("easyExcelExport tryLock is false, excelName={}", excelName);
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().println("当前有导出任务正在执行，请稍后再试");
                return;
            }
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            excelName = URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + excelName + excelType);
            // 这里 需要指定写用哪个class去写
            excelWriter = EasyExcel.write(response.getOutputStream(), tClass).build();
            // 这里注意 如果同一个sheet只要创建一次
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            int current = 1;
            while (true) {
                List<T> result = this.getData(current, pageSize, condition);
                if (CollectionUtils.isEmpty(result)) {
                    break;
                }
                if (System.currentTimeMillis() - start > EXPORT_TIME_OUT) {
                    log.info("easyExcelExport timeout... excelName={}", excelName);
                    break;
                }
                excelWriter.write(result, writeSheet);
                current++;
            }
        } catch (Exception e) {
            log.error("easyExcelExport error, excelName={}, e=", excelName, e);
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
            if (tryLock) {
                lock.unlock();
            }
        }
        long totalSecond = (System.currentTimeMillis() - start) / 1000;
        log.info("easyExcelExport end, excelName={}, totalSecond={}", excelName, totalSecond);
    }
}
