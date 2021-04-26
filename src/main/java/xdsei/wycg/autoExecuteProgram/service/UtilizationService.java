package xdsei.wycg.autoExecuteProgram.service;

import org.springframework.stereotype.Service;
import xdsei.wycg.autoExecuteProgram.threadRelation.task.Utilization.CpuUtilizationTask;
import xdsei.wycg.autoExecuteProgram.threadRelation.task.Utilization.DiskUtilizationTask;
import xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory.WorkRejectHandler;
import xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory.WorkThreadFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ZPww
 * @since 2021/4/21
 */
@Service
public class UtilizationService {

    /**
     * 全局唯一 获取cpu 或 disk 利用率: CPU_UTILIZATION_TASK.getCpuUtilization(), disk 同
     */
    public static final CpuUtilizationTask CPU_UTILIZATION_TASK = new CpuUtilizationTask();
    public static final DiskUtilizationTask DISK_UTILIZATION_TASK = new DiskUtilizationTask("D:");

    ThreadPoolExecutor utilizationThreadPoolExecutor = new ThreadPoolExecutor(2, 4
            , 0, TimeUnit.SECONDS
            , new LinkedBlockingQueue<>(1)
            , new WorkThreadFactory("定时获取cpuUtilization,diskUtilization")
            , new WorkRejectHandler());

    @PostConstruct
    public void updatingUtilization() {
        utilizationThreadPoolExecutor.execute(CPU_UTILIZATION_TASK);
        utilizationThreadPoolExecutor.execute(DISK_UTILIZATION_TASK);
    }


}
