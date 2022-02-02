package fhw.gulimall.product.feign;

import fhw.gulimall.common.utils.R;
import fhw.gulimall.ware.vo.SkuHasStockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-30 13:13
 */

@FeignClient("gulimall-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasStock")
    R getSkusHasStock(@RequestBody List<Long> skuIds);
}
