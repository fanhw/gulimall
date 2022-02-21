package fhw.gulimall.product.web;

import fhw.gulimall.product.entity.CategoryEntity;
import fhw.gulimall.product.service.CategoryService;
import fhw.gulimall.product.vo.Catalog2VO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-31 11:57
 */

@Controller
//@RequestMapping("/web")
public class IndexController {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        Map<String, List<Catalog2VO>> map = categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        //1.获取一把锁 通过锁的名字来进行区分
        RLock lock = redisson.getLock("my-lock");
        //2.加锁
        //  lock.lock();
        //不会自动续期
        lock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功，执行业务。。。" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            //3.解锁
            System.out.println("释放锁" + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello world";
    }


    @GetMapping("/write")
    public String writeValue() {
        String uuid = "";
        try {
            uuid = UUID.randomUUID().toString();
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return uuid;
    }
}
