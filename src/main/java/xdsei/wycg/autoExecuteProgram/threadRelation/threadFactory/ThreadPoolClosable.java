package xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory;

import lombok.extern.slf4j.Slf4j;
import xdsei.util.Tools;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 关闭局部线程池, 全局也可以关，不过全局没有关的意义
 * @author ZPww
 * @since 2021/4/19
 */
@Slf4j
public class ThreadPoolClosable {

    /**
     * 关闭局部线程池
     * @param threadPoolExecutor 线程执行器
     * @param timeOut 关闭局部线程池的超时时间（超时即关闭）
     * @param threadPoolName 线程池名（关闭时打印日志），如果在超时时间内正常结束则不打印日志
     */
    public static void closeLocalThreadPool(ThreadPoolExecutor threadPoolExecutor, int timeOut, String threadPoolName) {

        try {
            // 向线程发出关闭通知
            threadPoolExecutor.shutdown();

            // 向线程传达 XX时间之内关闭
            // (所有的任务都结束的时候，返回TRUE)
            if(!threadPoolExecutor.awaitTermination(timeOut, TimeUnit.MILLISECONDS)){
                // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                threadPoolExecutor.shutdownNow();
                log.error("{}已经关闭!", threadPoolName);
            }
        } catch (InterruptedException e) {
            // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
            Tools.logException("awaitTermination interrupted: [{}]", e);
            threadPoolExecutor.shutdownNow();
        }
    }
}
