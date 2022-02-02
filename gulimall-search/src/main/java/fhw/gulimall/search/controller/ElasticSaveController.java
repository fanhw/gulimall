package fhw.gulimall.search.controller;

import fhw.gulimall.common.exception.BizCodeEnum;
import fhw.gulimall.common.to.es.SkuESModel;
import fhw.gulimall.common.utils.R;
import fhw.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-30 13:34
 */

@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuESModel> skuESModelList) {
        boolean flag = false;
        try {
            flag = productSaveService.productStatusUp(skuESModelList);
        } catch (IOException e) {
            log.error("ElasticSaveController商品上架错误：{}", e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION);
        }
        if (!flag) {
            return R.ok();
        } else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION);
        }
    }
}
