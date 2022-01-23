package fhw.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import fhw.gulimall.common.constant.ProductConstant;
import fhw.gulimall.product.dao.AttrAttrgroupRelationDao;
import fhw.gulimall.product.dao.AttrGroupDao;
import fhw.gulimall.product.dao.CategoryDao;
import fhw.gulimall.product.entity.AttrAttrgroupRelationEntity;
import fhw.gulimall.product.entity.AttrGroupEntity;
import fhw.gulimall.product.entity.CategoryEntity;
import fhw.gulimall.product.service.CategoryService;
import fhw.gulimall.product.utils.WrapperUtils;
import fhw.gulimall.product.vo.AttrGroupRelationVO;
import fhw.gulimall.product.vo.AttrRespVO;
import fhw.gulimall.product.vo.AttrVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.common.utils.Query;

import fhw.gulimall.product.dao.AttrDao;
import fhw.gulimall.product.entity.AttrEntity;
import fhw.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;


    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        // 1.保存基本数据
        this.save(attrEntity);
        // 2. 保存关联关系
        if (null !=attr.getAttrGroupId() && attr.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        LambdaQueryWrapper<AttrEntity> attrEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        attrEntityLambdaQueryWrapper.eq(AttrEntity::getAttrType, "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (!Long.valueOf(0L).equals(catelogId)) {
            attrEntityLambdaQueryWrapper.eq(AttrEntity::getCatelogId, catelogId);
        }
        fuzzyHandle(params, attrEntityLambdaQueryWrapper);
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityLambdaQueryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVO> attrRespVOS = records.stream().map((attrEntity) -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, attrRespVO);
            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
                if (null != relationEntity && null != relationEntity.getAttrGroupId() ) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (null != categoryEntity) {
                attrRespVO.setCatelogName(categoryEntity.getName());
            }
            return attrRespVO;
        }).collect(Collectors.toList());
        pageUtils.setList(attrRespVOS);
        return pageUtils;
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        AttrRespVO attrRespVO = new AttrRespVO();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVO);
        if (attrEntity.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().eq(AttrAttrgroupRelationEntity::getAttrId, attrId));
            if (null != relationEntity && null != relationEntity.getAttrGroupId()) {
                attrRespVO.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (null != attrGroupEntity) {
                    attrRespVO.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVO.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (null != categoryEntity) {
            attrRespVO.setCatelogName(categoryEntity.getName());
        }
        return attrRespVO;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        // 1.保存基本数据
        this.updateById(attrEntity);
        // 2. 保存关联关系
        if (attrEntity.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            LambdaUpdateWrapper<AttrAttrgroupRelationEntity> updateWrapper = new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>();
            updateWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId());
            Long count = relationDao.selectCount(updateWrapper);
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            if (count > 0) {
                relationDao.update(attrAttrgroupRelationEntity, updateWrapper);
            } else {
                relationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = WrapperUtils.getQueryWrapper();
        queryWrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId);
        List<AttrAttrgroupRelationEntity> relationEntityList = relationDao.selectList(queryWrapper);
        List<Long> attrIds = relationEntityList.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(attrIds)) {
            return null;
        }
        List<AttrEntity> attrEntityList = this.listByIds(attrIds);
        return attrEntityList;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVO[] vos) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityList = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrgroupRelationEntity.setAttrId(item.getAttrId());
            attrgroupRelationEntity.setAttrGroupId(item.getAttrGroupId());
            return attrgroupRelationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(attrAttrgroupRelationEntityList);
    }

    /**
     * 获取当前分组没有关联的所有属性
     *
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1、当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        // 2、当前分类只能关联别的分组没有引用的属性
        //2.1 找出当前分类的分组信息
        LambdaQueryWrapper<AttrGroupEntity> queryWrapper = WrapperUtils.getQueryWrapper();
        queryWrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        // queryWrapper.ne(AttrGroupEntity::getAttrGroupId, attrgroupId);
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(queryWrapper);
        List<Long> attrGroupIds = attrGroupEntities.stream().map((item) -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.2 由分组信息找到属性信息
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = new ArrayList<>();
        //  if (!CollectionUtils.isEmpty(attrGroupIds)) {
        attrAttrgroupRelationEntities = relationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds));
        // }
        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        LambdaQueryWrapper<AttrEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //2.3 找到当前分类下的未被其他分组加载的属性信息（排除销售属性)
        lambdaQueryWrapper.eq(AttrEntity::getCatelogId, catelogId);
        lambdaQueryWrapper.eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            lambdaQueryWrapper.notIn(AttrEntity::getAttrId, attrIds);
        }
//        LambdaQueryWrapper<AttrAttrgroupRelationEntity> lambdaQueryWrapperTemp = new LambdaQueryWrapper<>();
//        lambdaQueryWrapperTemp.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId);
//        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntitiesTemp = relationDao.selectList(lambdaQueryWrapperTemp);
//        List<Long> attrIdstemp = attrAttrgroupRelationEntitiesTemp.stream().map((item) -> {
//            return item.getAttrId();
//        }).collect(Collectors.toList());
//        if (!CollectionUtils.isEmpty(attrIdstemp)) {
//            lambdaQueryWrapper.notIn(AttrEntity::getAttrId, attrIdstemp);
//        }
        fuzzyHandle(params, lambdaQueryWrapper);
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), lambdaQueryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    // 模糊查询
    private void fuzzyHandle(Map<String, Object> params, LambdaQueryWrapper<AttrEntity> lambdaQueryWrapper) {
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            lambdaQueryWrapper.and((wrapper) -> {
                wrapper.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
            });
        }
    }
}
