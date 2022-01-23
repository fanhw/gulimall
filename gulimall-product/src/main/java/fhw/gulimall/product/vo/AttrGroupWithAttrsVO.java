package fhw.gulimall.product.vo;

import fhw.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-20 21:14
 */

@Data
public class AttrGroupWithAttrsVO implements Serializable {

    private static final long serialVersionUID = 3695573568748993741L;
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
