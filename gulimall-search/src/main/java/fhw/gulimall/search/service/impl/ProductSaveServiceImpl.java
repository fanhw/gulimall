package fhw.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import fhw.gulimall.common.to.es.SkuESModel;
import fhw.gulimall.search.config.GulimallElasticSearchConfig;
import fhw.gulimall.search.constant.EsConstant;
import fhw.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-30 13:39
 */

@Slf4j
@Service("ProductSaveService")
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuESModel> skuESModelList) throws IOException {
        //建立索引 建立映射关系
        // 给es中保存这些数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuESModel esModel : skuESModelList) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index(EsConstant.PRODUCT_INDEX);
            indexRequest.id(esModel.getSkuId().toString());
            String string = JSON.toJSONString(esModel);
            indexRequest.source(string, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成：{}", collect);
        boolean hasFailures = bulk.hasFailures();
        return hasFailures;
    }
}
