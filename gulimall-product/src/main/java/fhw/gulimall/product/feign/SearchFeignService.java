package fhw.gulimall.product.feign;

import fhw.gulimall.common.to.es.SkuESModel;
import fhw.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-30 14:35
 */

@FeignClient("gulimall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuESModel> skuESModelList);
}
