package xdsei.wycg.autoExecuteProgram.netty.tcpServer.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import xdsei.util.Tools;
import xdsei.wycg.autoExecuteProgram.service.ExecuteExternalProgramService;
import xdsei.wycg.autoExecuteProgram.util.SpringContextUtil;


/**
 * 执行外部程序的处理器,负责开启和关闭执行程序的线程
 * @author ZPww
 * @since 2021/4/15
 */
@Slf4j
public class ExternalProgramHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // netty中不能直接通过注解获取bean
        ExecuteExternalProgramService executeExternalProgramService = SpringContextUtil
                .getBean(ExecuteExternalProgramService.class);
        // 假定start是启动指令
        if("start".equals(msg)) {
            log.info("i am here1...");
            executeExternalProgramService.doExecuteExternalProgram();
        }
        if("end".equals(msg)) {
            log.info("i am here2...");
            executeExternalProgramService.doDestroyExecuteExternalProgram();
        }

    }


    /**
     * 异常处理
     * @param ctx ctx
     * @param cause cs
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        Tools.logException("netty server get msg failed.", cause);
        ctx.close();
    }
}
