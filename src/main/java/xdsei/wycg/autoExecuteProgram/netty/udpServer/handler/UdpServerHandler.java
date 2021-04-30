package xdsei.wycg.autoExecuteProgram.netty.udpServer.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import xdsei.wycg.autoExecuteProgram.netty.UdpCustomHeartbeatHandler;


/**
 * server 收到 某一个客户端发来的消息，即DatagramPacket，packet里存储着发送端的 ip+port
 * client 要给服务端发送消息需要指定 ip+host
 *
 * @author ZQYP
 * @since 2021/4/28
 */
@Slf4j
public class UdpServerHandler extends UdpCustomHeartbeatHandler {


    public UdpServerHandler() {
        super("Server");
    }


    @Override
    protected void channelReadCustom(ChannelHandlerContext channelHandlerContext, DatagramPacket msg) {
        String msgInfo = msg.content().toString(CharsetUtil.UTF_8);
        log.info("i am udpServer, i receive msg is [{}]", msgInfo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}
