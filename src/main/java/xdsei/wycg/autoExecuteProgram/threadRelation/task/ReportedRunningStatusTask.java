package xdsei.wycg.autoExecuteProgram.threadRelation.task;


import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xdsei.util.Tools;
import xdsei.wycg.autoExecuteProgram.DTO.RelatedStatusReportMessage;
import xdsei.wycg.autoExecuteProgram.service.ConnectedClientService;
import xdsei.wycg.autoExecuteProgram.service.ReportRunningStatusService;
import xdsei.wycg.autoExecuteProgram.util.SpringContextUtil;

import java.net.InetAddress;
import java.util.Map;

/**
 * 向客户端报告运行状态，具体哪些状态，待定。。。
 *
 * @author ZPww
 * @since 2021/4/12
 */

@Slf4j
public class ReportedRunningStatusTask implements Runnable {

    private RelatedStatusReportMessage relatedStatusReportMessage;

    public ReportedRunningStatusTask(RelatedStatusReportMessage relatedStatusReportMessage) {
        this.relatedStatusReportMessage = relatedStatusReportMessage;
    }


    @Override
    public void run() {
        // do something
        while (!ReportRunningStatusService.editRelatedStatusReportMsgFinish) {
            try {
                //log.error("edit report status msg not finish! sleep...[1...]");
                Thread.sleep(1 );
            } catch (InterruptedException ie) {
                Tools.logException("report running status thread interrupted exception! [{}]", ie);
            }
        }
        //log.error("edit report status msg finish![2]");
        reportRunningStatusMsg(relatedStatusReportMessage);
    }


    @SneakyThrows
    public void reportRunningStatusMsg(RelatedStatusReportMessage relatedStatusReportMessage) {
        //log.error("reporting status msg! [3]");
        Map<String, ChannelHandlerContext> clients = ConnectedClientService.getClients();
        for(Map.Entry<String, ChannelHandlerContext> entry: clients.entrySet()) {
            if(!entry.getValue().channel().isActive()) {
                log.error("client connect failed! key is [{}], client name is [{}]",entry.getKey()
                        , InetAddress.getLocalHost().getHostName());
                continue;
            }
            // 随便发点啥
            StringBuilder msg = new StringBuilder();
            msg.append(relatedStatusReportMessage.getFrameHeader())
                    .append(relatedStatusReportMessage.getCpuUtilization())
                    .append(relatedStatusReportMessage.getDiskUtilization())
                    .append("}");
            entry.getValue().writeAndFlush(msg.toString());
            log.error("report status msg finish! [4]");
        }
        ReportRunningStatusService.editRelatedStatusReportMsgFinish = false;
    }
}
