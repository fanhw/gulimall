package fhw.gulimall.product.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-20 16:09
 */

@Data
public class AttrGroupRelationVO implements Serializable {

    private static final long serialVersionUID = -2702619092106555691L;
    private Long attrId;
    private Long attrGroupId;
}
