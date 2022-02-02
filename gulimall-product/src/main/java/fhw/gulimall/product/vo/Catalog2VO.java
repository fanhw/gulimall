package fhw.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-31 13:44
 */


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog2VO {

    private String catalog1Id;
    private List<Catalog3VO> catalog3List;
    private String id;
    private String name;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catalog3VO {
        private String catalog2Id;
        private String id;
        private String name;

    }
}
