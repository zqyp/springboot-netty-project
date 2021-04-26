package xdsei.wycg.autoExecuteProgram.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xdsei.util.Tools;
import xdsei.wycg.autoExecuteProgram.DTO.RelatedStatusReportMessage;
import xdsei.wycg.autoExecuteProgram.threadRelation.task.ReportedRunningStatusTask;
import xdsei.wycg.autoExecuteProgram.threadRelation.task.EditRunningStatusTask;
import xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory.ThreadPoolClosable;
import xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory.WorkRejectHandler;
import xdsei.wycg.autoExecuteProgram.threadRelation.threadFactory.WorkThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ZPww
 * @since 2021/4/16
 */
@Service
@Slf4j
public class ReportRunningStatusService {

    /**
     * 修改（查询）上报状态的线程一定要在上报线程之前完成
     * 修改相关状态上报信息线程 是否完成
     */
    public static volatile boolean editRelatedStatusReportMsgFinish = false;


    public void doReportRunningStatus() {

        if(!ConnectedClientService.channelConnectedStatus()) {
            log.error("no one client connected!");
            return;
        }
        //上报状态线程池 （局部，试试超时关闭，因为线程总是不能结束）
        // 换成全局后，任务总是被拒绝，可能的原因是线程没有执行完，且阻塞队列已满
        ThreadPoolExecutor reportStatusThreadPool = new ThreadPoolExecutor(
                2, 2, 60, TimeUnit.SECONDS
                , new LinkedBlockingQueue<>(1),new WorkThreadFactory("定时修改-上报状态线程池")
                , new WorkRejectHandler());

        RelatedStatusReportMessage relatedStatusReportMessage = new RelatedStatusReportMessage();

        reportStatusThreadPool.execute(new EditRunningStatusTask(relatedStatusReportMessage));
        reportStatusThreadPool.execute(new ReportedRunningStatusTask(relatedStatusReportMessage));

        ThreadPoolClosable.closeLocalThreadPool(reportStatusThreadPool, 1000
                ,"定时修改-上报状态线程池");
    }

}
