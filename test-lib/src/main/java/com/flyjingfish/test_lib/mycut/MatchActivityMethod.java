package com.flyjingfish.test_lib.mycut;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyjingfish.android_aop_annotation.MatchClassMethod;
import com.flyjingfish.android_aop_annotation.ProceedJoinPoint;
import com.flyjingfish.android_aop_annotation.anno.AndroidAopMatchClassMethod;

import java.lang.reflect.InvocationTargetException;

@AndroidAopMatchClassMethod(targetClassName = "androidx.appcompat.app.AppCompatActivity",methodName = {"startActivity"})
public class MatchActivityMethod implements MatchClassMethod {
    @Nullable
    @Override
    public Object invoke(@NonNull ProceedJoinPoint joinPoint, @NonNull String methodName) {
        Log.e("MatchActivityMethod","=====invoke====="+methodName);
        return joinPoint.proceed();
    }
}
