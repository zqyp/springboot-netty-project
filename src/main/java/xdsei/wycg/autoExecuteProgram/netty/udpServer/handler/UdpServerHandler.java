package xdsei.wycg.autoExecuteProgram.netty.udpServer.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * server 收到 某一个客户端发来的消息，即DatagramPacket，packet里存储着发送端的 ip+port
 * client 要给服务端发送消息需要指定 ip+host
 *
 * @author ZQYP
 * @since 2021/4/28
 */
@Slf4j
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        String msgInfo = packet.content().toString(CharsetUtil.UTF_8);
        log.info("i am udpServer, i receive msg is [{}]", msgInfo);
        String response = "hello sender, i receive your msg and response you...";
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(response, CharsetUtil.UTF_8), packet.sender()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}
