package fhw.gulimall.ware.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-30 13:01
 */

@Data
public class SkuHasStockVO implements Serializable {
    private static final long serialVersionUID = -112128338853004551L;
    private Long skuId;
    private Boolean hasStock;
}
