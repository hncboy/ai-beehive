package cn.beehive.web.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.beehive.base.domain.entity.FrontUserBaseDO;
import cn.beehive.base.mapper.FrontUserBaseMapper;
import cn.beehive.base.util.WebUtil;
import cn.beehive.web.service.FrontUserBaseService;
import org.springframework.stereotype.Service;

/**
 * 基础用户业务实现类
 *
 * @author CoDeleven
 */
@Service
public class FrontUserBaseServiceImpl extends ServiceImpl<FrontUserBaseMapper, FrontUserBaseDO> implements FrontUserBaseService {

    @Override
    public FrontUserBaseDO createEmptyBaseUser() {
        FrontUserBaseDO userBaseDO = new FrontUserBaseDO();
        userBaseDO.setNickname("StarGPT_" + RandomUtil.randomString(6));
        userBaseDO.setLastLoginIp(null);
        userBaseDO.setDescription(null);
        userBaseDO.setAvatarVersion(0);
        this.save(userBaseDO);
        return userBaseDO;
    }

    @Override
    public FrontUserBaseDO findUserInfoById(Integer baseUserId) {
        return getById(baseUserId);
    }

    @Override
    public void updateLastLoginIp(Integer baseUserId) {
        update(new FrontUserBaseDO(), new LambdaUpdateWrapper<FrontUserBaseDO>()
                .set(FrontUserBaseDO::getLastLoginIp, WebUtil.getIp())
                .eq(FrontUserBaseDO::getId, baseUserId));
    }
}
