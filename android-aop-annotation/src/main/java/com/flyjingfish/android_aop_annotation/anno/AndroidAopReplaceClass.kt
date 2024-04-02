package com.flyjingfish.android_aop_annotation.anno

import com.flyjingfish.android_aop_annotation.enums.MatchType

/**
 * 定义替换类的方法调用的注解，使用这个注解的类需要配合 [AndroidAopReplaceMethod] 注解使用，并且这个类也是处理切面的类
 * [wiki 文档使用说明](https://github.com/FlyJingFish/AndroidAOP/wiki/@AndroidAopReplaceClass)
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AndroidAopReplaceClass(
    /**
     * @return 目标类名（包含包名）
     */
    val value: String,
    /**
     *
     * @return 返回匹配类型 [MatchType]
     */
    val type: MatchType = MatchType.EXTENDS,
    /**
     *
     * @return 排除继承中的类名数组（包含包名）
     */
    val excludeClasses: Array<String> = []
)
