package com.xingxin.template.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingxin.dao.UserMapper;
import com.xingxin.entity.User;
import com.xingxin.template.AbstractExportTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuxh
 * @date 2022/3/30
 */
@Slf4j
@Component
public class UserExportService extends AbstractExportTemplate<User> {

    @Resource
    private UserMapper userMapper;

    @Override
    protected List<User> getData(Integer current, Integer pageSize, Object condition) {
        int pageNo = (current - 1) * pageSize;
        // condition 可以转换为实体对象，作为mybatis条件
        return userMapper.selectList(new LambdaQueryWrapper<User>().last("limit " + pageNo + "," + pageSize));
    }
}
