package xdsei.wycg.autoExecuteProgram.netty.tcpServer.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import xdsei.wycg.autoExecuteProgram.service.ConnectedClientService;
import xdsei.wycg.autoExecuteProgram.service.ReportRunningStatusService;
import xdsei.wycg.autoExecuteProgram.util.SpringContextUtil;

import java.net.InetAddress;

/**
 * 用来监控客户端下发的指令
 * @author ZPww
 * @since 2021/4/10
 */
@Slf4j
public class ClientMonitorHandler extends ChannelInboundHandlerAdapter {

    /**
     * 建立连接时，存储已连接的客户端
     * @param ctx ctx
     * @throws Exception ex
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        SocketChannel channel = (SocketChannel) ctx.channel();
        String key = channel.id().asLongText();
        log.info("new client connected... client 's address is [{}]", channel.localAddress());
        // 注意 ctx.writeAndFlush 和 ctx.channel().writeAndFlush()的区别
        ctx.writeAndFlush("welcome to "+ InetAddress.getLocalHost().getHostName());
        ConnectedClientService.addClient(key, ctx);
    }


    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("client disconnected...,address is {}", ctx.channel().localAddress().toString());
    }


    /**
     *  NioEventLoop作为Reactor线程，负责TCP连接的创建和接入，以及TCP消息的读写，防止
     *  业务代码阻塞 NioEventLoop 线程非常重要
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("i am ClientMonitorHandler, i received your msg [{}]", msg);
        // 对收到的信息进行应答
        // 应答代码
        // 暂时模拟下上报状态报文
        if("report".equals(msg)) {
            ReportRunningStatusService reportStatusService = SpringContextUtil
                    .getBean(ReportRunningStatusService.class);
            reportStatusService.doReportRunningStatus();
        }else {
            // 交给业务处理器
            ctx.fireChannelRead(msg);
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
        ctx.close();
    }

}
