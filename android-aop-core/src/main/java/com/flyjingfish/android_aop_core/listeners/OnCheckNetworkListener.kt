package com.flyjingfish.android_aop_core.listeners

import com.flyjingfish.android_aop_annotation.ProceedJoinPoint
import com.flyjingfish.android_aop_core.annotations.CheckNetwork

interface OnCheckNetworkListener {
    fun invoke(
        joinPoint: ProceedJoinPoint,
        checkNetwork: CheckNetwork,
        availableNetwork: Boolean
    ): Any?
}