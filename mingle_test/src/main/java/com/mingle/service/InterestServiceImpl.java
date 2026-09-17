package com.mingle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.mapper.UserMapper;
import com.mingle.vo.InterestVO;

@Service
public class InterestServiceImpl implements InterestService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InterestVO> getInterests() {
        return userMapper.selectInterests();
    }
}
