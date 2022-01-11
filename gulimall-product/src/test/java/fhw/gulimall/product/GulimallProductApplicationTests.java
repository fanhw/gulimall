package fhw.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fhw.gulimall.product.entity.BrandEntity;
import fhw.gulimall.product.service.BrandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Test
    @DisplayName("品牌测试")
    void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setDescript("国产品牌");
//        brandEntity.setBrandId(1L);
//        brandService.updateById(brandEntity);
        List<BrandEntity> brandEntityList = brandService.list(new LambdaQueryWrapper<BrandEntity>().eq(BrandEntity::getBrandId, 1L));
        brandEntityList.forEach(System.out::println);
    }

}
