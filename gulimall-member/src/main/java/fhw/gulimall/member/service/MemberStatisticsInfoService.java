package fhw.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 13:28:11
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

