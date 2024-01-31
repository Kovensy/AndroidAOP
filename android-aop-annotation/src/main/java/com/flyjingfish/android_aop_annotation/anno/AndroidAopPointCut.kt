package com.flyjingfish.android_aop_annotation.anno

import com.flyjingfish.android_aop_annotation.base.BasePointCut
import kotlin.reflect.KClass


/**
 * 定义注解切面的注解，使用这个注解的类需要是注解类
 * [wiki 文档使用说明](https://github.com/FlyJingFish/AndroidAOP/wiki/@AndroidAopPointCut)
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AndroidAopPointCut(
    /**
     *
     * @return 处理切面的类
     */
    val value: KClass<out BasePointCut<out Annotation>>
)