package fhw.gulimall.product.feign;

import fhw.gulimall.common.to.SkuReductionTO;
import fhw.gulimall.common.to.SpuBoundTO;
import fhw.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-21 22:51
 */

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBouds(@RequestBody SpuBoundTO spuBoundTO);

    @PostMapping("coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTO);
}
