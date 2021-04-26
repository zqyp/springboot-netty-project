package xdsei.wycg.autoExecuteProgram.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xdsei.util.Tools;
import xdsei.util.exec.CmdExecutor;
import xdsei.wycg.autoExecuteProgram.threadRelation.task.ExecuteExternalProgramTask;
import xdsei.wycg.autoExecuteProgram.config.ExecuteExternalProgramCmdConfig;
import xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory.ThreadPoolClosable;
import xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory.WorkRejectHandler;
import xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory.WorkThreadFactory;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author ZPww
 * @since 2021/4/14
 */

@Service
@Slf4j
public class ExecuteExternalProgramService {

    @Autowired
    private ExecuteExternalProgramCmdConfig executeExternalProgramConfig;

    /**
     * 存储 2 个执行程序的执行器，以便停止程序
     */
    private static final Stack<CmdExecutor> CMD_EXECUTORS = new Stack<>();


    /**
     * 执行外部程序入口
     */
    public void doExecuteExternalProgram() {
        List<String> executeExternalProgramPaths = getExecuteExternalProgramPaths();
        if(executeExternalProgramPaths == null)
           return;

        // 局部线程池
        // 全局线程池的问题参考ReportRunningStatusService中注释
        ThreadPoolExecutor executeExternalProgramThreadPool = new ThreadPoolExecutor(
                2, 2, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1), new WorkThreadFactory("执行外部程序线程池")
                , new WorkRejectHandler());

        CmdExecutor cmdExecutor;
        for(String executeExternalPath: executeExternalProgramPaths) {
            // n个线程执行n个程序，这里只执行两个程序
            cmdExecutor = new CmdExecutor();
            CMD_EXECUTORS.push(cmdExecutor);
            // 如果任务阻塞超时，线程得不到释放，没看见好的方法主动停止超时线程
            // 正确的做法，是不要去停止线程，线程正常消亡才是正常的逻辑
            executeExternalProgramThreadPool.execute(new ExecuteExternalProgramTask("windows"
                   ,"gbk", executeExternalPath, cmdExecutor));
        }
        ThreadPoolClosable.closeLocalThreadPool(executeExternalProgramThreadPool, 15000
                , "执行外部程序线程池");
    }

    /**
     * 停止外部程序入口
     */
    public void doDestroyExecuteExternalProgram() {
        log.warn("执行程序的执行器数量是 [{}]",CMD_EXECUTORS.size());
        boolean isDestroy = true;
        while (!CMD_EXECUTORS.empty()) {
            CmdExecutor cmdExecutor = CMD_EXECUTORS.peek();
            if(null != cmdExecutor) {
                // 单独测试一个.jar程序,让线程sleep100秒，之后网控制台简单的打印了两句话，
                // 打印完了意味着线程执行就完了（我测试观察是这样）？但是打印之前也就是sleep中调用 cmdExecutor.destoryProcess()停止程序
                // 却无法停止，在100秒后依旧还会打印。
                // 换成局部线程池后，关闭程序可以通过关线程池来实现
                try {
                    cmdExecutor.destoryProcess();
                } catch (Exception e) {
                    isDestroy = false;
                    Tools.logException("destroy external program failed!", e);
                }
                if(isDestroy)
                    CMD_EXECUTORS.pop();
            }
        }
    }

    /**
     * 获取待执行程序的路径
     * @return 待执行程序的路径集合
     */
    public List<String> getExecuteExternalProgramPaths() {

        List<String> externalProgramPaths = executeExternalProgramConfig.getExternalProgramPaths();

        if(null == externalProgramPaths || externalProgramPaths.isEmpty()) {
            log.warn("WARNING: execute external program's path is not config.");
            return null;
        }
        return externalProgramPaths;
    }


}
