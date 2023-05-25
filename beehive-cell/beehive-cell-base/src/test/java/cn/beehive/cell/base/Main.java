package cn.beehive.cell.base;

import cn.beehive.cell.base.domain.bo.CompositeCellConfigListBO;
import cn.beehive.cell.base.domain.bo.SimpleCellConfig;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.setting.yaml.YamlUtil;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        ClassPathResource classPathResource = new ClassPathResource("test.yml");
        try {
            InputStream inputStream = classPathResource.getInputStream();
            CompositeCellConfigListBO yamlDict = YamlUtil.load(inputStream, CompositeCellConfigListBO.class);
            for (SimpleCellConfig simpleCellConfig : yamlDict.getConfigItemList()) {
                System.out.println(simpleCellConfig);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
