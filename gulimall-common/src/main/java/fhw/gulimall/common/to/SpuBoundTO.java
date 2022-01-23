package fhw.gulimall.common.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-21 23:01
 */
@Data
public class SpuBoundTO implements Serializable {
    private static final long serialVersionUID = 7397043956846039114L;

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
