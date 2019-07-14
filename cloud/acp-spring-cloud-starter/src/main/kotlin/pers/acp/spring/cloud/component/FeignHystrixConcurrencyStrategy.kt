package pers.acp.spring.cloud.component

import com.netflix.hystrix.HystrixThreadPoolKey
import com.netflix.hystrix.HystrixThreadPoolProperties
import com.netflix.hystrix.strategy.HystrixPlugins
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy
import com.netflix.hystrix.strategy.properties.HystrixProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import pers.acp.core.log.LogFactory

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 自定义 Feign Hystrix 并发策略
 * 解决 Feign 开启 Hystrix 后 requestAttributes 无法共享传递的问题
 *
 * @author zhangbin by 12/04/2018 14:28
 * @since JDK 11
 */
@Component
class FeignHystrixConcurrencyStrategy @Autowired
constructor() : HystrixConcurrencyStrategy() {

    private val log = LogFactory.getInstance(this.javaClass)

    private var delegate: HystrixConcurrencyStrategy? = null

    init {
        try {
            this.delegate = HystrixPlugins.getInstance().concurrencyStrategy
            if (this.delegate !is FeignHystrixConcurrencyStrategy) {
                initHystrixPlugins()
            }
        } catch (e: Exception) {
            this.log.error("Failed to register Sleuth Hystrix Concurrency Strategy", e)
        }

    }

    private fun initHystrixPlugins() {
        val eventNotifier = HystrixPlugins.getInstance().eventNotifier
        val metricsPublisher = HystrixPlugins.getInstance().metricsPublisher
        val propertiesStrategy = HystrixPlugins.getInstance().propertiesStrategy
        this.logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher, propertiesStrategy)
        HystrixPlugins.reset()
        HystrixPlugins.getInstance().registerConcurrencyStrategy(this)
        HystrixPlugins.getInstance().registerEventNotifier(eventNotifier)
        HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher)
        HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy)
    }

    private fun logCurrentStateOfHystrixPlugins(eventNotifier: HystrixEventNotifier,
                                                metricsPublisher: HystrixMetricsPublisher,
                                                propertiesStrategy: HystrixPropertiesStrategy) {
        log.debug("Current Hystrix plugins configuration is ["
                + "concurrencyStrategy [" + this.delegate + "]," + "eventNotifier ["
                + eventNotifier + "]," + "metricPublisher [" + metricsPublisher + "],"
                + "propertiesStrategy [" + propertiesStrategy + "]," + "]")
        log.debug("Registering Sleuth Hystrix Concurrency Strategy.")
    }

    override fun <T> wrapCallable(callable: Callable<T>): Callable<T> =
            WrappedCallable(callable, RequestContextHolder.getRequestAttributes()!!)

    override fun getThreadPool(threadPoolKey: HystrixThreadPoolKey,
                               corePoolSize: HystrixProperty<Int>,
                               maximumPoolSize: HystrixProperty<Int>,
                               keepAliveTime: HystrixProperty<Int>, unit: TimeUnit,
                               workQueue: BlockingQueue<Runnable>): ThreadPoolExecutor =
            this.delegate!!.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue)

    override fun getThreadPool(threadPoolKey: HystrixThreadPoolKey, threadPoolProperties: HystrixThreadPoolProperties): ThreadPoolExecutor =
            this.delegate!!.getThreadPool(threadPoolKey, threadPoolProperties)

    override fun getBlockingQueue(maxQueueSize: Int): BlockingQueue<Runnable> =
            this.delegate!!.getBlockingQueue(maxQueueSize)

    override fun <T> getRequestVariable(rv: HystrixRequestVariableLifecycle<T>): HystrixRequestVariable<T> =
            this.delegate!!.getRequestVariable(rv)

    private class WrappedCallable<T>(private val target: Callable<T>, private val requestAttributes: RequestAttributes) : Callable<T> {

        @Throws(Exception::class)
        override fun call(): T {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes)
                return target.call()
            } finally {
                RequestContextHolder.resetRequestAttributes()
            }
        }
    }

}
