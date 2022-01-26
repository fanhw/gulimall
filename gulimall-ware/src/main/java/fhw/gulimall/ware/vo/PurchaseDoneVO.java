package fhw.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-25 09:03
 */

@Data
public class PurchaseDoneVO implements Serializable {
    private static final long serialVersionUID = 4644357378039469897L;
    @NotNull(message = "采购单ID不能为null")
    private Long id;
    private List<PurchaseItemDoneVO> items;
}
