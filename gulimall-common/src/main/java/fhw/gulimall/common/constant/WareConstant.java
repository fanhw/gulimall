package fhw.gulimall.common.constant;

import lombok.Getter;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-24 18:04
 */


public class WareConstant {
    public enum PurchaseStatusEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINISH(3, "已完成"),
        HASERROR(4, "有异常");

        PurchaseStatusEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Getter
        private int code;
        @Getter
        private String description;

    }
    public enum   PurchaseDetailStatusEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISH(3, "已完成"),
        HASERROR(4, "采购失败");

        PurchaseDetailStatusEnum(int code, String description) {
            this.code = code;
            this.description = description;
        }

        @Getter
        private int code;
        @Getter
        private String description;

    }
}
