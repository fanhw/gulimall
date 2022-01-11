package fhw.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.order.entity.OrderReturnReasonEntity;

import java.util.Map;

/**
 * 退货原因
 *
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 13:33:43
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

