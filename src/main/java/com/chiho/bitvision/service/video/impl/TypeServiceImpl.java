package com.chiho.bitvision.service.video.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.video.Type;
import com.chiho.bitvision.mapper.video.TypeMapper;
import com.chiho.bitvision.service.video.TypeService;
import org.springframework.stereotype.Service;

@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

}
