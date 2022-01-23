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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;
    @Autowired
    private OSSClient ossClient;

    @Autowired
    private CategoryService categoryService;


    @Test
    public void  testUtils(){
      //  LambdaQueryWrapper<BrandEntity> queryWrapper = WrapperUtils.getQueryWrapper(BrandEntity.class);
        LambdaQueryWrapper<BrandEntity> queryWrapper = WrapperUtils.getQueryWrapper();
        queryWrapper.eq(BrandEntity::getName,"华为");
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

    @Test
    void testUpload() throws FileNotFoundException {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        // String endpoint = "oss-cn-shanghai.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        //String accessKeyId = "LTAI5tAmxSDZCAbRmyLr88UC";
        //  String accessKeySecret = "FMeowFTJ8PD0BzIGUInMbndE2YiTnK";

        // 创建OSSClient实例。
        //OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\fanha\\Desktop\\0d40c24b264aa511.jpg");

        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("gulimall-fhw", "honor.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传完成");
    }

}
