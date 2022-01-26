package fhw.gulimall.ware.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-24 17:56
 */

@Data
public class MergeVO implements Serializable {
    private static final long serialVersionUID = -8390449154804515007L;
    private Long purchaseId;
    private List<Long> items;

}
