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
}
