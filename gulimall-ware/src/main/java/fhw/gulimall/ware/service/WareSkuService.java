package fhw.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.ware.entity.WareSkuEntity;
import fhw.gulimall.ware.vo.SkuHasStockVO;

import java.util.List;
import java.util.Map;

/**
 * εεεΊε­
 *
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 13:36:00
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVO> getSkusHasStock(List<Long> skuIds);
}

