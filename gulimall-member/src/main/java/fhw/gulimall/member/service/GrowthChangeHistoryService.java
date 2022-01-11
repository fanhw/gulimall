package fhw.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 13:28:11
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

