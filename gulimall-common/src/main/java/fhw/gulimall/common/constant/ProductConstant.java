package fhw.gulimall.common.constant;

import lombok.Getter;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-20 15:20
 */


public class ProductConstant {

    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "规格参数"),
        ATTR_TYPE_SALE(0, "销售属性");

        AttrEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Getter
        private int code;
        @Getter
        private String description;

    }

    public enum StatusEnum {
        NEW_SPU(0, "新建"),
        SPU_UP(1, "商品上架"),
        SPU_DOWN(2, "商品下架");

        StatusEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Getter
        private int code;
        @Getter
        private String description;

    }
}
