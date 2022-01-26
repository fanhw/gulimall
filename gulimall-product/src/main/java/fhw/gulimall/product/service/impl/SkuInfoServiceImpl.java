package fhw.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fhw.gulimall.product.utils.WrapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.common.utils.Query;

import fhw.gulimall.product.dao.SkuInfoDao;
import fhw.gulimall.product.entity.SkuInfoEntity;
import fhw.gulimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = WrapperUtils.getQueryWrapper();
        fuzzyHandle(params, queryWrapper);
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    // 模糊查询
    private void fuzzyHandle(Map<String, Object> params, LambdaQueryWrapper<SkuInfoEntity> lambdaQueryWrapper) {
        String key = (String) params.get("key");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        String max = (String) params.get("max");
        String min = (String) params.get("min");
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.eq(SkuInfoEntity::getBrandId, brandId);
            });
        }
        if (StringUtils.isNotBlank(key)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.eq(SkuInfoEntity::getSkuId, key).or().like(SkuInfoEntity::getSkuName, key);
            });
        }
        if (StringUtils.isNotBlank(min)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.ge(SkuInfoEntity::getPrice, min);
            });
        }
        if (StringUtils.isNotBlank(max)) {
            try {
                BigDecimal bigDecimalMax = new BigDecimal(max);
                if (bigDecimalMax.compareTo(new BigDecimal("0")) == 1) {
                    lambdaQueryWrapper.and((wrapper) -> {
                        wrapper.le(SkuInfoEntity::getPrice, max);
                    });
                }
            } catch (Exception e) {

            }
        }
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.eq(SkuInfoEntity::getCatalogId, catelogId);
            });
        }


    }
}


