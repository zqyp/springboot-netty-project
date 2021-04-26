package xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ZPww
 * @since 2021/4/12
 */

@Slf4j
public class WorkRejectHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.error("task Rejected! {}", executor.toString());
    }
}
