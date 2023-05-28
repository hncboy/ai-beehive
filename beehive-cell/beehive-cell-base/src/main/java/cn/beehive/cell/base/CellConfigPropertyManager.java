package cn.beehive.cell.base;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.cell.base.convertor.CellConfigConvertor;
import cn.beehive.cell.base.convertor.CellDOConvertor;
import cn.beehive.cell.base.domain.bo.CompositeCellConfigListBO;
import cn.beehive.cell.base.domain.bo.SimpleCellConfig;
import cn.beehive.cell.base.mapper.CellConfigMapper;
import cn.beehive.cell.base.mapper.CellMapper;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Cell 配置属性初始化，该步骤会在各个 Cell 中完成
 * 主要做了以下几件事：
 * 1. 初始化 CellDO（如果没有持久化则持久化）
 * 2. 持久化 Cell 的配置属性（如某配置项是否必填、示例值、默认值、是否可见等等）
 * 3. 对外提供获取 Cell 配置属性的操作
 *
 * @author CoDeleven
 */
@Slf4j
public class CellConfigPropertyManager {
    private static final String CELL_CONFIG_INIT_YAML = "init-cell-config.yml";
    @Resource
    private CellConfigMapper cellConfigMapper;
    @Resource
    private CellMapper cellMapper;
    private CellDO belongCell;
    /**
     * 简单版 Cell 配置属性缓存
     */
    private final Map<String, CellConfigDO> cellConfigPropertyCache = new HashMap<>();

    // ================== 对外查询操作 =========================

    /**
     * 根据 Cell 配置项名称，获取 CellConfigDO
     *
     * @param key 配置项名称
     * @return 配置项属性
     */
    public CellConfigDO getConfigProperty(String key) {
        return cellConfigPropertyCache.get(key);
    }

    /**
     * 获取该 Cell 的所有配置项属性
     *
     * @return 所有配置项属性
     */
    public Map<String, CellConfigDO> getAll() {
        return cellConfigPropertyCache;
    }

    // TODO 获取单个属性： isRequired，isVisible ...

    // ================== 初始化操作 ==========================
    public CellConfigPropertyManager() {
        // CellConfigItem 列表
        CompositeCellConfigListBO compositeCellConfig = loadInitCellConfig();
        // 根据 configGroupIdentity 获取/初始化唯一 cell 记录
        this.belongCell = this.initCell(compositeCellConfig);
        // 检查 cellConfigList 中的配置项是否都载入数据库了，如果没有则插入，有的话不更新，数据库存在配置文件中不存在的移除数据库中的配置
        List<CellConfigDO> newestCellConfigItemList = this.persistentConfigItem(this.belongCell.getId(), compositeCellConfig);
        // 将配置属性载入内存中
        cacheCellAttribute(newestCellConfigItemList);
        // TODO 启动定时任务，轮询数据库修改
    }

    /**
     * 将 Cell 配置属性 载入内存中，方便直接查询
     *
     * @param cellConfigList 配置属性列表
     */
    private void cacheCellAttribute(List<CellConfigDO> cellConfigList) {
        for (CellConfigDO cellConfigDO : cellConfigList) {
            cellConfigPropertyCache.put(cellConfigDO.getKeyName(), cellConfigDO);
        }
    }

    /**
     * 初始化 CellDO
     *
     * @param compositeCellConfig Cell配置
     * @return CellDO
     */
    private CellDO initCell(CompositeCellConfigListBO compositeCellConfig) {
        this.belongCell = cellMapper.selectOne(new LambdaQueryWrapper<CellDO>().eq(CellDO::getCode, compositeCellConfig.getCellCode()));
        if (Objects.isNull(this.belongCell)) {
            CellDO cellDO = CellDOConvertor.INSTANCE.convertFrom(compositeCellConfig);
            this.cellMapper.insert(cellDO);
            return cellDO;
        }
        return this.belongCell;
    }

    /**
     * 根据 CELL_CONFIG_INIT_YAML 加载初始化配置项
     *
     * @return 配置项集合
     */
    private CompositeCellConfigListBO loadInitCellConfig() {
        ClassPathResource classPathResource = new ClassPathResource(CELL_CONFIG_INIT_YAML);
        try {
            InputStream inputStream = classPathResource.getInputStream();
            return YamlUtil.load(inputStream, CompositeCellConfigListBO.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取某个Cell的配置组唯一标识
     *
     * @return 配置组唯一标识
     */
    public final String getCellIdentityCode() {
        return this.belongCell.getCode();
    }

    /**
     * 获取 Cell 的ID
     *
     * @return Cell的唯一ID
     */
    public final Integer getCellId() {
        return this.belongCell.getId();
    }

    /**
     * 持久化配置项属性
     * 检查 cellConfigList 中的配置项是否都载入数据库了：
     * - 如果没有则插入
     * - 如果已经插入，则不更新
     * - 如果数据库存在，配置文件中不存在的，移除数据库中的配置
     *
     * @param cellId              配置模型ID
     * @param compositeCellConfig Cell的所有配置
     * @return 最新的 CellDO 配置属性列表
     */
    private List<CellConfigDO> persistentConfigItem(Integer cellId, CompositeCellConfigListBO compositeCellConfig) {
        // 现有配置文件里 配置项列表
        List<SimpleCellConfig> fileConfigItemList = compositeCellConfig.getConfigItemList();
        // 现有数据库中，该 cellId 的配置项列表
        List<CellConfigDO> dbConfigItemList = cellConfigMapper.selectList(new LambdaQueryWrapper<CellConfigDO>()
                .eq(CellConfigDO::getCellId, cellId).eq(CellConfigDO::getIsDelete, 0));

        // 保存了当前最新的 Cell 配置属性
        List<CellConfigDO> returnCellConfigList = new ArrayList<>();

        // 检查 file 和 db 的差别
        List<String> fileConfigItemKey = fileConfigItemList.stream().map(SimpleCellConfig::getKeyName).toList();
        List<String> dbConfigItemKey = dbConfigItemList.stream().map(CellConfigDO::getKeyName).toList();

        // 需要新增到数据库中的 configKey
        List<String> needInsertKey = CollectionUtil.subtractToList(fileConfigItemKey, dbConfigItemKey);
        for (SimpleCellConfig simpleCellConfig : fileConfigItemList) {
            if(needInsertKey.contains(simpleCellConfig.getKeyName())) {
                CellConfigDO newCellConfigItem = CellConfigConvertor.INSTANCE.convertFrom(cellId, simpleCellConfig);
                cellConfigMapper.insert(newCellConfigItem);
                returnCellConfigList.add(newCellConfigItem);
                log.info("【Cell-{} 新增配置】{}", compositeCellConfig.getCellName(), simpleCellConfig.getKeyName());
            }
        }

        // 需要从数据库中删除的 configKey
        List<String> needRemoveKey = CollectionUtil.subtractToList(fileConfigItemKey, dbConfigItemKey);
        for (CellConfigDO dbCellConfig : dbConfigItemList) {
            if(needRemoveKey.contains(dbCellConfig.getKeyName())) {
                cellConfigMapper.deleteById(dbCellConfig);
                log.info("【Cell-{} 移除配置】{}", compositeCellConfig.getCellName(), dbCellConfig.getKeyName());
            } else {
                // 不用移除的就是数据库中最新的
                returnCellConfigList.add(dbCellConfig);
            }
        }

        return returnCellConfigList;
    }
}

