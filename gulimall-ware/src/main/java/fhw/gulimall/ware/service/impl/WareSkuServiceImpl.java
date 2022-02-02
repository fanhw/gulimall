package fhw.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fhw.gulimall.common.utils.R;
import fhw.gulimall.ware.feign.ProductFeignService;
import fhw.gulimall.ware.vo.SkuHasStockVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.common.utils.Query;

import fhw.gulimall.ware.dao.WareSkuDao;
import fhw.gulimall.ware.entity.WareSkuEntity;
import fhw.gulimall.ware.service.WareSkuService;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq(WareSkuEntity::getSkuId, skuId);
            });
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq(WareSkuEntity::getWareId, wareId);
            });
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //判断是否有这个库存记录
        LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WareSkuEntity::getSkuId, skuId);
        queryWrapper.eq(WareSkuEntity::getWareId, wareId);
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(wareSkuEntities)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // todo
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> map = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) map.get("skuName"));
                }
            } catch (Exception e) {
            }
            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVO> getSkusHasStock(List<Long> skuIds) {
        List<SkuHasStockVO> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVO skuHasStockVO = new SkuHasStockVO();
            Long count = baseMapper.getSkuStock(skuId);
            skuHasStockVO.setSkuId(skuId);
            skuHasStockVO.setHasStock(count == null ? false : count > 0);
            return skuHasStockVO;
        }).collect(Collectors.toList());
        return collect;
    }

}
