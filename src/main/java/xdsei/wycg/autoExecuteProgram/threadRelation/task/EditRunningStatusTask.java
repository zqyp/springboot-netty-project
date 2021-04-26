package xdsei.wycg.autoExecuteProgram.threadRelation.task;

import lombok.extern.slf4j.Slf4j;
import xdsei.util.Tools;
import xdsei.wycg.autoExecuteProgram.DTO.RelatedStatusReportMessage;
import xdsei.wycg.autoExecuteProgram.service.ReportRunningStatusService;
import xdsei.wycg.autoExecuteProgram.service.UtilizationService;
import xdsei.wycg.autoExecuteProgram.util.SpringContextUtil;
import xdsei.wycg.autoExecuteProgram.util.TimeUtil;

/**
 * @author ZPww
 * @since 2021/4/18
 */
@Slf4j
public class EditRunningStatusTask implements Runnable{

    private RelatedStatusReportMessage relatedStatusReportMessage;

    public EditRunningStatusTask(RelatedStatusReportMessage relatedStatusReportMessage) {
        this.relatedStatusReportMessage = relatedStatusReportMessage;
    }

    @Override
    public void run() {
        while (ReportRunningStatusService.editRelatedStatusReportMsgFinish) {
            try {
                //log.error("edit report status msg finish!sleep... [1...]");
                Thread.sleep(1 );
            } catch (InterruptedException ie) {
                Tools.logException("report running status thread interrupted exception! [{}]", ie);
            }
        }
        editRelatedStatusReportMsg(relatedStatusReportMessage);
    }

    public void editRelatedStatusReportMsg(RelatedStatusReportMessage relatedStatusReportMessage) {
        //log.error("editing report status msg! [1...]");
        relatedStatusReportMessage.setMachineType("53");
        TimeUtil timeUtil = SpringContextUtil.getBean(TimeUtil.class);
        String utcStrBySeconds = timeUtil.getUtcStrBySeconds(timeUtil.getUTCTime());
        relatedStatusReportMessage.setTimeUnix(utcStrBySeconds);

        String cpuUtilization = UtilizationService.CPU_UTILIZATION_TASK.getCpuUtilization();
        relatedStatusReportMessage.setCpuUtilization(cpuUtilization);
        String diskUtilization = UtilizationService.DISK_UTILIZATION_TASK.getDiskUtilization();
        relatedStatusReportMessage.setDiskUtilization(diskUtilization);
        System.out.println("cpu:"+cpuUtilization+"--disk:"+diskUtilization);
        relatedStatusReportMessage.setErrCode("999");

        ReportRunningStatusService.editRelatedStatusReportMsgFinish = true;
    }

}
