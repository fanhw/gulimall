package fhw.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fhw.gulimall.ware.vo.SkuHasStockVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import fhw.gulimall.ware.entity.WareSkuEntity;
import fhw.gulimall.ware.service.WareSkuService;
import fhw.gulimall.common.utils.PageUtils;
import fhw.gulimall.common.utils.R;


/**
 * 商品库存
 *
 * @author fhw
 * @email fanhaowena@163.com
 * @date 2022-01-11 13:36:00
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;


    @PostMapping("/hasStock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVO> result = wareSkuService.getSkusHasStock(skuIds);
        return R.ok().setData(result);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
