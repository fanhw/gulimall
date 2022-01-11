package fhw.gulimall.order.dao;

import fhw.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 13:33:43
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
