package com.ruoyi.wms.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.utils.MapstructUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.wms.domain.bo.ItemBo;
import com.ruoyi.wms.domain.bo.ItemSkuBo;
import com.ruoyi.wms.domain.entity.Item;
import com.ruoyi.wms.domain.entity.ItemBrand;
import com.ruoyi.wms.domain.entity.ItemCategory;
import com.ruoyi.wms.domain.entity.Merchant;
import com.ruoyi.wms.domain.vo.ItemBrandVo;
import com.ruoyi.wms.domain.vo.ItemCategoryVo;
import com.ruoyi.wms.domain.vo.ItemSkuVo;
import com.ruoyi.wms.domain.vo.ItemVo;
import com.ruoyi.wms.domain.vo.MerchantVo;
import com.ruoyi.wms.mapper.ItemBrandMapper;
import com.ruoyi.wms.mapper.ItemCategoryMapper;
import com.ruoyi.wms.mapper.ItemMapper;
import com.ruoyi.wms.utils.WarehouseScopeUtils;
import com.ruoyi.wms.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class ItemService {

    private final ItemMapper itemMapper;
    private final ItemSkuService itemSkuService;
    private final ItemCategoryMapper itemCategoryMapper;
    private final ItemBrandMapper itemBrandMapper;
    private final MerchantMapper merchantMapper;

    /**
     * 查询物料
     */

    public ItemVo queryById(Long id) {
        ItemVo vo = itemMapper.selectVoById(id);
        if (vo != null) {
            fillExtra(List.of(vo));
        }
        return vo;
    }

    /**
     * 查询物料
     *
     * @param itemIds ids
     */

    public List<ItemVo> queryById(List<Long> itemIds) {
        if (CollUtil.isEmpty(itemIds)) {
            return CollUtil.newArrayList();
        }
        LambdaQueryWrapper<Item> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(Item::getId, itemIds);
        List<ItemVo> list = itemMapper.selectVoList(lambdaQueryWrapper);
        fillExtra(list);
        return list;
    }

    /**
     * 查询物料列表
     */

    public TableDataInfo<ItemVo> queryPageList(ItemBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Item> lqw = buildQueryWrapper(bo);
        Page<ItemVo> result = itemMapper.selectVoPage(pageQuery.build(), lqw);
        fillExtra(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 查询物料列表
     */

    public List<ItemVo> queryList(ItemBo bo) {
        LambdaQueryWrapper<Item> lqw = buildQueryWrapper(bo);
        List<ItemVo> list = itemMapper.selectVoList(lqw);
        fillExtra(list);
        return list;
    }

    /**
     * 填充分类、品牌、供应商、主规格信息
     */
    private void fillExtra(List<ItemVo> itemVoList) {
        if (CollUtil.isEmpty(itemVoList)) {
            return;
        }
        // 分类
        Set<Long> categoryIds = itemVoList.stream()
            .map(ItemVo::getItemCategory)
            .filter(StrUtil::isNotBlank)
            .map(Long::valueOf)
            .collect(Collectors.toSet());
        Map<Long, ItemCategoryVo> categoryMap = CollUtil.isEmpty(categoryIds)
            ? Map.of()
            : itemCategoryMapper.selectVoList(Wrappers.<ItemCategory>lambdaQuery().in(ItemCategory::getId, categoryIds))
                .stream().collect(Collectors.toMap(ItemCategoryVo::getId, Function.identity(), (a, b) -> a));
        // 品牌
        Set<Long> brandIds = itemVoList.stream().map(ItemVo::getItemBrand).filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, ItemBrandVo> brandMap = CollUtil.isEmpty(brandIds)
            ? Map.of()
            : itemBrandMapper.selectVoList(Wrappers.<ItemBrand>lambdaQuery().in(ItemBrand::getId, brandIds))
                .stream().collect(Collectors.toMap(ItemBrandVo::getId, Function.identity(), (a, b) -> a));
        // 供应商
        Set<Long> supplierIds = itemVoList.stream().map(ItemVo::getSupplierId).filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, MerchantVo> supplierMap = CollUtil.isEmpty(supplierIds)
            ? Map.of()
            : merchantMapper.selectVoList(Wrappers.<Merchant>lambdaQuery().in(Merchant::getId, supplierIds))
                .stream().collect(Collectors.toMap(MerchantVo::getId, Function.identity(), (a, b) -> a));

        for (ItemVo vo : itemVoList) {
            if (StrUtil.isNotBlank(vo.getItemCategory())) {
                ItemCategoryVo categoryVo = categoryMap.get(Long.valueOf(vo.getItemCategory()));
                vo.setItemCategoryInfo(categoryVo);
                if (categoryVo != null) {
                    vo.setItemCategoryName(categoryVo.getCategoryName());
                }
            }
            if (vo.getItemBrand() != null && brandMap.containsKey(vo.getItemBrand())) {
                vo.setItemBrandName(brandMap.get(vo.getItemBrand()).getBrandName());
            }
            if (vo.getSupplierId() != null && supplierMap.containsKey(vo.getSupplierId())) {
                vo.setSupplierName(supplierMap.get(vo.getSupplierId()).getMerchantName());
            }
            List<ItemSkuVo> skuList = itemSkuService.queryByItemId(vo.getId());
            if (CollUtil.isNotEmpty(skuList)) {
                ItemSkuVo sku = skuList.get(0);
                vo.setSkuId(sku.getId());
                vo.setSkuName(sku.getSkuName());
                vo.setLength(sku.getLength());
                vo.setWidth(sku.getWidth());
                vo.setHeight(sku.getHeight());
                vo.setVolume(sku.getVolume());
                vo.setGrossWeight(sku.getGrossWeight());
                vo.setNetWeight(sku.getNetWeight());
                vo.setCostPrice(sku.getCostPrice());
            }
        }
    }

    private LambdaQueryWrapper<Item> buildQueryWrapper(ItemBo bo) {
        LambdaQueryWrapper<Item> lqw = Wrappers.lambdaQuery();
        lqw.eq(StrUtil.isNotBlank(bo.getItemCode()), Item::getItemCode, bo.getItemCode());
        // 主键集合
        lqw.in(!CollUtil.isEmpty(bo.getIds()), Item::getId, bo.getIds());
        lqw.like(StrUtil.isNotBlank(bo.getItemName()), Item::getItemName, bo.getItemName());
        if (!StrUtil.isBlank(bo.getItemCategory())) {
            Long parentId = Long.valueOf(bo.getItemCategory());
            List<Long> subIdList = this.buildSubItemCategoryIdList(parentId);
            subIdList.add(parentId);
            lqw.in(Item::getItemCategory, subIdList);
        }
        lqw.eq(StrUtil.isNotBlank(bo.getUnit()), Item::getUnit, bo.getUnit());
        lqw.eq(bo.getWarehouseId() != null, Item::getWarehouseId, bo.getWarehouseId());
        // 仓库数据权限过滤
        List<Long> warehouseScopeIds = WarehouseScopeUtils.getWarehouseIds();
        lqw.in(CollUtil.isNotEmpty(warehouseScopeIds), Item::getWarehouseId, warehouseScopeIds);
        lqw.eq(bo.getSupplierId() != null, Item::getSupplierId, bo.getSupplierId());
        lqw.eq(StrUtil.isNotBlank(bo.getItemLevel()), Item::getItemLevel, bo.getItemLevel());
        lqw.like(StrUtil.isNotBlank(bo.getItemModel()), Item::getItemModel, bo.getItemModel());
        lqw.eq(bo.getItemBrand() != null, Item::getItemBrand, bo.getItemBrand());
        lqw.orderByDesc(Item::getId);
        return lqw;
    }

    private List<Long> buildSubItemCategoryIdList(Long parentId) {
        LambdaQueryWrapper<ItemCategory> itemTypeWrapper = new LambdaQueryWrapper<>();
        itemTypeWrapper.eq(ItemCategory::getParentId, parentId);
        return itemCategoryMapper.selectList(itemTypeWrapper).stream().map(ItemCategory::getId).collect(Collectors.toList());
    }

    /**
     * 新增物料
     *
     * @param bo
     */
    @Transactional
    public void insertByForm(ItemBo bo) {
        validateBoBeforeSave(bo);
        Item item = MapstructUtils.convert(bo, Item.class);
        itemMapper.insert(item);
        itemSkuService.setItemId(bo.getSku(), item.getId());
        normalizeSku(bo.getSku(), item);
        itemSkuService.saveOrUpdateBatchByBo(bo.getSku());
    }

    /**
     * 修改物料
     *
     * @param bo
     */
    @Transactional
    public void updateByForm(ItemBo bo) {
        validateBoBeforeSave(bo);
        itemMapper.updateById(MapstructUtils.convert(bo, Item.class));
        itemSkuService.setItemId(bo.getSku(), bo.getId());
        normalizeSku(bo.getSku(), MapstructUtils.convert(bo, Item.class));
        itemSkuService.saveOrUpdateBatchByBo(bo.getSku());
    }

    /**
     * 规格名称/编码不再由页面填写：为空时补默认值；体积=长*宽*高
     */
    private void normalizeSku(List<ItemSkuBo> skuList, Item item) {
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        for (ItemSkuBo sku : skuList) {
            if (StrUtil.isBlank(sku.getSkuName())) {
                sku.setSkuName(item.getItemName());
            }
            if (StrUtil.isBlank(sku.getSkuCode())) {
                sku.setSkuCode(item.getItemCode());
            }
            if (sku.getLength() != null && sku.getWidth() != null && sku.getHeight() != null) {
                BigDecimal volume = sku.getLength()
                    .multiply(sku.getWidth())
                    .multiply(sku.getHeight())
                    .setScale(4, RoundingMode.HALF_UP);
                sku.setVolume(volume);
            } else if (sku.getVolume() == null) {
                sku.setVolume(BigDecimal.ZERO);
            }
        }
    }

    /**
     * 保存前的数据校验
     * <p>
     * 商品名称允许重复（不同编号、不同仓库的商品可以同名），因此不校验名称唯一性；
     * 商品编号（item_code）具有唯一性，需要校验；
     * 规格编号（sku_code/条码）允许重复，因此不做唯一性校验
     */
    private void validateBoBeforeSave(ItemBo itemBo) {
        // 统一去除编号首尾空格，避免因空格差异绕过唯一性校验
        itemBo.setItemCode(StrUtil.trim(itemBo.getItemCode()));
        validateItemCode(itemBo);
        validateItemSkuName(itemBo.getSku());
        validateWarehouseScope(itemBo);
    }

    /**
     * 校验商品编号是否唯一（编辑时排除自身）
     */
    private void validateItemCode(ItemBo itemBo) {
        String itemCode = itemBo.getItemCode();
        if (StrUtil.isBlank(itemCode)) {
            return;
        }
        LambdaQueryWrapper<Item> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Item::getItemCode, itemCode);
        queryWrapper.ne(itemBo.getId() != null, Item::getId, itemBo.getId());
        Assert.isTrue(itemMapper.selectCount(queryWrapper) == 0, "商品编号【{}】已存在，请更换商品编号", itemCode);
    }

    /**
     * 校验仓库是否在数据权限范围内
     */
    private void validateWarehouseScope(ItemBo itemBo) {
        if (itemBo.getWarehouseId() != null) {
            Assert.isTrue(WarehouseScopeUtils.contains(itemBo.getWarehouseId()), "无权操作该仓库的商品");
        }
    }

    /**
     * 校验同一商品下的规格名称是否重复
     * <p>
     * 规格名称为空时不参与重复校验（导入或手工录入时允许存在多个未填写规格名称的规格）
     */
    private void validateItemSkuName(List<ItemSkuBo> skuVoList) {
        if (CollUtil.isEmpty(skuVoList)) {
            return;
        }
        List<String> skuNames = skuVoList.stream()
            .map(ItemSkuBo::getSkuName)
            .filter(StrUtil::isNotBlank)
            .map(String::trim)
            .collect(Collectors.toList());
        Assert.isTrue(skuNames.stream().distinct().count() == skuNames.size(), "商品规格重复");
    }



    /**
     * 批量删除物料
     */
    @Transactional
    public void deleteById(Long id) {
        List<Long> skuIds = itemSkuService.queryByItemId(id).stream().map(ItemSkuVo::getId).toList();
        itemMapper.deleteById(id);
        itemSkuService.deleteByIds(skuIds);
    }

}
