package com.hncboy.beehive.cell.core.annotation;

import com.hncboy.beehive.base.domain.entity.RoomDO;
import com.hncboy.beehive.cell.core.hander.RoomHandler;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigFactory;
import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/6/13
 * 配置项校验切面
 */
@Aspect
@Component
public class CellConfigCheckAspect {

    @Resource
    private CellConfigFactory cellConfigFactory;

    @Pointcut("@annotation(com.hncboy.beehive.cell.core.annotation.CellConfigCheck)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object checkContent(ProceedingJoinPoint point) throws Throwable {
        MethodSignature ms = (MethodSignature) point.getSignature();
        // 获取方法上的注解
        CellConfigCheck cellConfigCheck = ms.getMethod().getAnnotation(CellConfigCheck.class);

        Long roomId = parseRoomId(point, cellConfigCheck.roomId());
        // 校验房间是否存在并且可用
        RoomDO roomDO = RoomHandler.checkRoomExistAndCellCanUse(roomId);

        // 获取 cell 配置项策略
        CellConfigStrategy cellConfigStrategy = cellConfigFactory.getCellConfigStrategy(roomDO.getCellCode());
        // 获取房间配置项参数，这个方法里包含了校验逻辑
        cellConfigStrategy.getRoomConfigParamAsMap(roomId);

        return point.proceed();
    }

    /**
     * 解析 roomIdSpEl 表达式
     *
     * @param point      切点
     * @param roomIdSpEl roomIdSpEl 表达式
     * @return roomId
     */
    private Long parseRoomId(ProceedingJoinPoint point, String roomIdSpEl) {
        // 创建一个 ExpressionParser 实例，用于解析SpEl表达式
        ExpressionParser parser = new SpelExpressionParser();

        // 获取方法的签名信息
        MethodSignature methodSignature = (MethodSignature) point.getSignature();

        // 解析SpEl表达式
        Expression expression = parser.parseExpression(roomIdSpEl);

        // 创建ParameterNameDiscoverer实例，用于解析方法参数的名称
        DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

        // 创建MethodBasedEvaluationContext实例，设置 SpEl 表达式的上下文
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                // 目标对象
                point.getTarget(),
                // 方法对象
                methodSignature.getMethod(),
                // 方法参数
                point.getArgs(),
                // 参数名称发现器
                parameterNameDiscoverer
        );

        // 通过表达式和上下文获取SpEl表达式的值，并指定返回类型为Long
        return expression.getValue(context, Long.class);
    }
}
