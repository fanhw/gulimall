package fhw.gulimall.product.vo;

import lombok.Data;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-20 13:01
 */

@Data
public class AttrRespVO extends AttrVO {
    private String catelogName;
    private String groupName;
    private  Long[] catelogPath;
}
