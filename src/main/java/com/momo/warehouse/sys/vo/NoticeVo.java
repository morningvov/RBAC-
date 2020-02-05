package com.momo.warehouse.sys.vo;

import com.momo.warehouse.sys.bean.Loginfo;
import com.momo.warehouse.sys.bean.Notice;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 对实体类的封装
 * @author momo
 * @create 2019-12-06 下午 19:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeVo extends Notice {

    private static final long serialVersionUID=1L;

    //分页参数
    private Integer page = 1;
    private Integer limit = 10;

    //批量删除id数组
    private Integer[] ids; //接收多个ID

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
