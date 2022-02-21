package fhw.gulimall.product;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import fhw.gulimall.product.entity.BrandEntity;
import fhw.gulimall.product.service.BrandService;
import fhw.gulimall.product.service.CategoryService;
import fhw.gulimall.product.utils.WrapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;
    @Autowired
    private OSSClient ossClient;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedisson() {
        System.out.println(redissonClient);
    }

    @Test
    public void testStringRedisTemplate() {
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.set("hello", "world" + UUID.randomUUID().toString(), 3600, TimeUnit.SECONDS);
        String hello = opsForValue.get("hello");
        System.out.println("保存的数据为" + hello);
    }


    @Test
    public void testUtils() {
        //  LambdaQueryWrapper<BrandEntity> queryWrapper = WrapperUtils.getQueryWrapper(BrandEntity.class);
        LambdaQueryWrapper<BrandEntity> queryWrapper = WrapperUtils.getQueryWrapper();
        queryWrapper.eq(BrandEntity::getName, "华为");
        BrandEntity one = brandService.getOne(queryWrapper);
        System.out.println(one);
    }

    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径{}", Arrays.asList(catelogPath));
    }

    @Test
    @DisplayName("品牌测试")
    void contextLoads() {
        List<BrandEntity> brandEntityList = brandService.list(new LambdaQueryWrapper<BrandEntity>().eq(BrandEntity::getBrandId, 1L));
        brandEntityList.forEach(System.out::println);
    }

}
