package com.flyjingfish.android_aop_annotation.utils

import com.flyjingfish.android_aop_annotation.ProceedJoinPoint
import com.flyjingfish.android_aop_annotation.base.BasePointCut
import com.flyjingfish.android_aop_annotation.base.BasePointCutCreator
import com.flyjingfish.android_aop_annotation.base.MatchClassMethod
import com.flyjingfish.android_aop_annotation.base.MatchClassMethodCreator
import java.lang.ref.ReferenceQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal object AndroidAopBeanUtils {
    private val mBasePointCutMap = ConcurrentHashMap<String, BasePointCut<Annotation>>()
    private val mMatchClassMethodMap = ConcurrentHashMap<String, MatchClassMethod?>()
    private val mTargetReferenceMap = ConcurrentHashMap<String, KeyWeakReference<Any>>()
    private val mTargetMethodMap = ConcurrentHashMap<String, MethodMap>()
    private val mTargetKeyReferenceQueue = ReferenceQueue<Any>()
    private val mSingleIO: ExecutorService = Executors.newSingleThreadExecutor()

    fun getCutClassCreator(annotationName: String): BasePointCutCreator? {
        return JoinAnnoCutUtils.getCutClassCreator(annotationName)
    }

    fun getMatchClassCreator(annotationName: String): MatchClassMethodCreator? {
        return JoinAnnoCutUtils.getMatchClassCreator(annotationName)
    }

    fun getBasePointCut(joinPoint: ProceedJoinPoint, annotationName : String,targetClassName:String, methodKey : String): BasePointCut<Annotation>? {
        val key = "$targetClassName-${joinPoint.target}-$methodKey-$annotationName"
        var basePointCut: BasePointCut<Annotation>? = mBasePointCutMap[key]
        if (basePointCut == null) {
            basePointCut = getNewPointCut(annotationName)
            mBasePointCutMap[key] = basePointCut
            observeTarget(joinPoint.target, key)
        }else{
            removeWeaklyReachableObjectsOnIOThread()
        }
        return basePointCut
    }

    private fun getNewPointCut(annotationName: String): BasePointCut<Annotation> {
        val basePointCutCreator = JoinAnnoCutUtils.getCutClassCreator(annotationName)
        if (basePointCutCreator != null){
            return basePointCutCreator.newInstance() as BasePointCut<Annotation>
        }else{
            throw IllegalArgumentException("无法找到 $annotationName 的切面处理类")
        }
    }


    fun getMatchClassMethod(joinPoint: ProceedJoinPoint, cutClassName: String, targetClassName:String,methodKey : String): MatchClassMethod {
        val key = "$targetClassName-${joinPoint.target}-$methodKey-$cutClassName"
        var matchClassMethod: MatchClassMethod? = mMatchClassMethodMap[key]
        if (matchClassMethod == null) {
            matchClassMethod = getNewMatchClassMethod(cutClassName)
            mMatchClassMethodMap[key] = matchClassMethod
            observeTarget(joinPoint.target, key)
        }else{
            removeWeaklyReachableObjectsOnIOThread()
        }
        return matchClassMethod
    }

    private fun getNewMatchClassMethod(clsName: String): MatchClassMethod {
        val matchClassMethodCreator = JoinAnnoCutUtils.getMatchClassCreator(clsName)
        if (matchClassMethodCreator != null){
            return matchClassMethodCreator.newInstance()
        }else{
            throw IllegalArgumentException("无法找到 $clsName 的切面处理类")
        }
    }

    fun getMethodMapCache(key: String): MethodMap? {
        val methodMap = mTargetMethodMap[key]
        if (methodMap != null){
            removeWeaklyReachableObjectsOnIOThread()
        }
        return methodMap
    }

    fun putMethodMapCache(key: String, methodMap:MethodMap, target:Any?) {
        mTargetMethodMap[key] = methodMap
        observeTarget(target,key)
    }

    private fun observeTarget(target : Any?,key :String){
        mSingleIO.execute{
            if (target != null){
                val weakReference = KeyWeakReference(target,mTargetKeyReferenceQueue,key)
                mTargetReferenceMap[key] = weakReference
            }
            removeWeaklyReachableObjects()
        }
    }

    private fun removeWeaklyReachableObjectsOnIOThread(){
        mSingleIO.execute{
            removeWeaklyReachableObjects()
        }
    }

    private fun removeWeaklyReachableObjects() {
        var ref: KeyWeakReference<*>?
        do {
            ref = mTargetKeyReferenceQueue.poll() as KeyWeakReference<*>?
            if (ref != null) {
                mTargetReferenceMap.remove(ref.key)
                mBasePointCutMap.remove(ref.key)
                mMatchClassMethodMap.remove(ref.key)
                mTargetMethodMap.remove(ref.key)
            }
        } while (ref != null)
    }
}