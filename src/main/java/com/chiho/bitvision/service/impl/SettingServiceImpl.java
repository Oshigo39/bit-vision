package com.chiho.bitvision.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.entity.Setting;
import com.chiho.bitvision.mapper.SettingMapper;
import com.chiho.bitvision.service.SettingService;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements SettingService {
}
