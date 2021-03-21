package org.jeecg.modules.demo.wt.service.impl;

import org.jeecg.modules.demo.wt.entity.WuYp;
import org.jeecg.modules.demo.wt.mapper.WuYpMapper;
import org.jeecg.modules.demo.wt.service.IWuYpService;
import org.jeecg.modules.demo.wt.vo.WuYpVO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class WuYpServiceImplTest {

    @Autowired
    private IWuYpService wuYpService;
    @Resource
    private WuYpMapper wuYpMapper;

    @Test
    void buildTree() {

        List<WuYp> list = wuYpService.list();

        List<WuYpVO> wuYpVOS = wuYpService.buildTree(list);
        System.out.println(wuYpVOS);
    }
}