package fhw.gulimall.ware.dao;

import fhw.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 13:36:00
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
