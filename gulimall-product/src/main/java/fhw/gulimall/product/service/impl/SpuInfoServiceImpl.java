package fhw.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fhw.gulimall.common.constant.ProductConstant;
import fhw.gulimall.common.to.SkuReductionTO;
import fhw.gulimall.common.to.SpuBoundTO;
import fhw.gulimall.common.to.es.SkuESModel;
import fhw.gulimall.common.utils.R;
import fhw.gulimall.product.entity.*;
import fhw.gulimall.product.feign.CouponFeignService;
import fhw.gulimall.product.feign.SearchFeignService;
import fhw.gulimall.product.feign.WareFeignService;
import fhw.gulimall.product.service.*;
import fhw.gulimall.product.utils.WrapperUtils;
import fhw.gulimall.product.vo.*;
import fhw.gulimall.ware.vo.SkuHasStockVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.common.utils.Query;

import fhw.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private WareFeignService wareFeignService;


    @Autowired
    private BrandService brandService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVO spuSaveVO) {
        //1.保存spu基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVO, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
        //2.保存spu描述图片
        List<String> decript = spuSaveVO.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
        //3.保存spu图片集
        List<String> images = spuSaveVO.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);
        //4.保存spu的规格参数
        List<BaseAttrs> baseAttrs = spuSaveVO.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(baseAttr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            // 设置attr的name
            AttrEntity attrEntity = attrService.getById(baseAttr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrId(baseAttr.getAttrId());
            productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
            productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);
        //4.保存spu的积分参数
        Bounds bounds = spuSaveVO.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBouds(spuBoundTO);
        if (!r.getCode().equals(Integer.valueOf(0))) {
            log.error("远程保存spu积分信息失败");
        }
        //5.保存当前spu对应的所有sku信息
        //5.1.保存sku基本信息
        List<Skus> skus = spuSaveVO.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.forEach(sku -> {
                String defaultImg = "";
                for (Images img : sku.getImages()) {
                    if (img.getDefaultImg() == 1) {
                        defaultImg = img.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoEntity.setSaleCount(0L);
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                List<Images> skuImages = sku.getImages();
                List<SkuImagesEntity> skuImagesEntities = skuImages.stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    return StringUtils.isNotBlank(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2.保存sku图片信息
                skuImagesService.saveBatch(skuImagesEntities);
                //5.3保存sku销售属性信息
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //5.4保存sku优惠信息
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(sku, skuReductionTO);
                skuReductionTO.setSkuId(skuId);
                skuReductionTO.setMemberPrice(sku.getMemberPrice());
                if (skuReductionTO.getFullCount() > 0 || skuReductionTO.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTO);
                    if (!r1.getCode().equals(Integer.valueOf(0))) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }
    }


    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);

    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<SpuInfoEntity> queryWrapper = WrapperUtils.getQueryWrapper();
        fuzzyHandle(params, queryWrapper);
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void spuUp(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        List<ProductAttrValueEntity> attrListForSpu = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIdList = attrListForSpu.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> ids = attrService.selectSearchAttrIds(attrIdList);
        Set<Long> idSet = new HashSet<>(ids);
        List<SkuESModel.Attrs> result = attrListForSpu.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuESModel.Attrs attrs1 = new SkuESModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());
        Map<Long, Boolean> stockMap = null;
        try {
            R r = wareFeignService.getSkusHasStock(skuIdList);
            TypeReference<List<SkuHasStockVO>> typeReference = new TypeReference<List<SkuHasStockVO>>() {
            };
            stockMap = r.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVO::getSkuId, item -> item.getHasStock()));
        } catch (Exception e) {
            log.error("库存服务异常：原因{}", e);
        }
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuESModel> collect = skuInfoEntities.stream().map(item -> {
            SkuESModel esModel = new SkuESModel();
            BeanUtils.copyProperties(item, esModel);
            esModel.setSkuPrice(item.getPrice());
            esModel.setSkuImg(item.getSkuDefaultImg());
            // 设置品牌相关信息
            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());
            // 设置分类相关信息
            CategoryEntity categoryEntity = categoryService.getById(item.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());
            esModel.setAttrs(result);
            esModel.setHotScore(0L);
            if (null == finalStockMap) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(item.getSkuId()));
            }
            return esModel;
        }).collect(Collectors.toList());
        R r = searchFeignService.productStatusUp(collect);
        if (r.getCode() == 0) {
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // 重复调用 接口幂等性 重试机制
        }
    }

    // 模糊查询
    private void fuzzyHandle(Map<String, Object> params, LambdaQueryWrapper<SpuInfoEntity> lambdaQueryWrapper) {
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(key)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.eq(SpuInfoEntity::getId, key).or().like(SpuInfoEntity::getSpuName, key);
            });
        }
        if (StringUtils.isNotBlank(status)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.eq(SpuInfoEntity::getPublishStatus, status);
            });
        }
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.eq(SpuInfoEntity::getBrandId, brandId);
            });
        }
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
            });
        }

    }

}
