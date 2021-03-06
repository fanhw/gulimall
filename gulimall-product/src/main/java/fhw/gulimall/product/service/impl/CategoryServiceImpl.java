package fhw.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fhw.gulimall.product.service.CategoryBrandRelationService;
import fhw.gulimall.product.utils.WrapperUtils;
import fhw.gulimall.product.vo.Catalog2VO;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.common.utils.Query;

import fhw.gulimall.product.dao.CategoryDao;
import fhw.gulimall.product.entity.CategoryEntity;
import fhw.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    private static final String firstLevel = "0";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter(categoryEntity ->
                ObjectUtils.equals(categoryEntity.getParentCid(), new Long(firstLevel))
        ).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, categoryEntities));
            return categoryEntity;
        }).sorted(Comparator.comparing(CategoryEntity::getSort, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> catIds) {
        // todo  ??????????????????
        baseMapper.deleteBatchIds(catIds);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> result = findParentPath(catelogId, paths);
        Collections.reverse(result);
        return result.toArray(new Long[0]);
    }

    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = WrapperUtils.getQueryWrapper();
        queryWrapper.eq(CategoryEntity::getParentCid, 0L);
        List<CategoryEntity> categoryEntities = baseMapper.selectList(queryWrapper);
        return categoryEntities;
    }

    @Override
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        //1.??????????????????
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isBlank(catalogJson)) {
            //2.???????????? ????????????
            System.out.println("??????????????????????????????????????????????????????");
            Map<String, List<Catalog2VO>> catalogJsonFromDB = getCatalogJsonFromDBWithRedisLock();
            return catalogJsonFromDB;
        }
        System.out.println("??????????????????????????????????????????");
        Map<String, List<Catalog2VO>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2VO>>>() {
        });
        return result;
    }

    // ????????????????????????????????????
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDBWithRedisLock() {
        // ???redis ??????????????????
        // ????????????????????????
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("?????????????????????????????????");
            Map<String, List<Catalog2VO>> dataFromDB;
            //????????????..????????????
            try {
                dataFromDB = getDataFromDB();
            } finally {
                //????????????????????????????????????=????????????
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                //?????????
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList("lock"), uuid);
            }
            return dataFromDB;
        } else {
            System.out.println("?????????????????????????????????????????????");
            // ????????????
            // ??????1000ms??????
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            return getCatalogJsonFromDBWithRedisLock();
        }
    }

    private Map<String, List<Catalog2VO>> getDataFromDB() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isNotBlank(catalogJson)) {
            Map<String, List<Catalog2VO>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2VO>>>() {
            });
            return result;
        }
        System.out.println("??????????????????????????????");
        //1?????????????????????
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2???????????????1?????????
        List<CategoryEntity> level1Categorys = getCategoryEntities(entities, 0L);
        //3.????????????
        Map<String, List<Catalog2VO>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getCategoryEntities(entities, v.getCatId());
            List<Catalog2VO> catalog2VOS = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catalog2VOS = categoryEntities.stream().map(level2 -> {
                    Catalog2VO catalog2VO = new Catalog2VO(v.getCatId().toString(), null, level2.getCatId().toString(), level2.getName());
                    List<CategoryEntity> categoryEntities1 = getCategoryEntities(entities, level2.getCatId());
                    if (!CollectionUtils.isEmpty(categoryEntities1)) {
                        List<Catalog2VO.Catalog3VO> collect1 = categoryEntities1.stream().map(level3 -> {
                            Catalog2VO.Catalog3VO catalog3VO = new Catalog2VO.Catalog3VO(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catalog3VO;
                        }).collect(Collectors.toList());
                        catalog2VO.setCatalog3List(collect1);
                    }
                    return catalog2VO;
                }).collect(Collectors.toList());
            }
            return catalog2VOS;
        }));
        String jsonString = JSON.toJSONString(collect);
        stringRedisTemplate.opsForValue().set("catalogJson", jsonString, 1, TimeUnit.DAYS);
        return collect;
    }

    // ????????????????????????????????????
    public Map<String, List<Catalog2VO>> getCatalogJsonFromDBWithLocalLock() {
        synchronized (this) {
            return getDataFromDB();
        }
    }

    private List<CategoryEntity> getCategoryEntities(List<CategoryEntity> list, Long parentCid) {
        List<CategoryEntity> collect = list.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findParentPath(Long catalogId, List<Long> paths) {
        paths.add(catalogId);
        CategoryEntity byId = this.getById(catalogId);
        if (null != byId.getParentCid() && byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }


    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> collect = all.stream().filter(categoryEntity -> ObjectUtils.equals(categoryEntity.getParentCid(), root.getCatId()))
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                }).sorted(Comparator.comparing(CategoryEntity::getSort, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
        return collect;
    }
}
