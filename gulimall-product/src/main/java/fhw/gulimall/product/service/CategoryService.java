package fhw.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.product.entity.CategoryEntity;
import fhw.gulimall.product.vo.Catalog2VO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 10:50:46
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> catIds);

    /**
     * 找到catelogId的完整路径
     *
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);


    /**
     * 级联更新所有关联数据
     *
     * @param category
     */
    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catalog2VO>> getCatalogJson();

}

