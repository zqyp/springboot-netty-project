package xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZPww
 * @since 2021/4/12
 */
@Slf4j
public class WorkThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public WorkThreadFactory(String whatFeatureOfGroup) {
        this.namePrefix = whatFeatureOfGroup + "-worker-";
    }

    @Override
    public Thread newThread(Runnable task) {
        String name = namePrefix + nextId.getAndIncrement();
        Thread thread = new Thread(null, task, name, 0);
        log.info("thread 's name is: {}", thread.getName());
        return thread;
    }
}
