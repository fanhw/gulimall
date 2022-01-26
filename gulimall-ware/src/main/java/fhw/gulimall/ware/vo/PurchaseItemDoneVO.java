package fhw.gulimall.ware.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-25 09:06
 */

@Data
public class PurchaseItemDoneVO implements Serializable {
    private static final long serialVersionUID = 4899166484325284468L;
    // "itemId":1,"status":3,"reason":""
    private Long itemId;
    private Integer status;
    private String reason;
}
