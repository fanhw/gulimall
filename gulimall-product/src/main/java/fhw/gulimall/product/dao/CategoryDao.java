package fhw.gulimall.product.dao;

import fhw.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 10:50:46
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
