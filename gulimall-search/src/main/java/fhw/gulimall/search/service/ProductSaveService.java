package fhw.gulimall.search.service;

import fhw.gulimall.common.to.es.SkuESModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-30 13:37
 */


public interface ProductSaveService {

    boolean productStatusUp(List<SkuESModel> skuESModelList) throws IOException;

}
