package com.mzll.gmall.service.impl;

import com.mzll.gmall.bean.UmsMember;
import com.mzll.gmall.bean.service.UmsMemberService;
import com.mzll.gmall.mapper.UmsMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UmsMemberImpl implements UmsMemberService {

    @Autowired
    private UmsMemberMapper umsMemberMapper;
    @Override
    public List<UmsMember> getAll() {
        return umsMemberMapper.selectAll();
    }
}
