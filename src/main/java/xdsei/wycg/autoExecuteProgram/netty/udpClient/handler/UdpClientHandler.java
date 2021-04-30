package xdsei.wycg.autoExecuteProgram.netty.udpClient.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import xdsei.wycg.autoExecuteProgram.netty.UdpCustomHeartbeatHandler;

import java.net.InetSocketAddress;


/**
 * @author ZQYP
 * @since 2021/4/28
 */
@Slf4j
public class UdpClientHandler extends UdpCustomHeartbeatHandler {

    public UdpClientHandler() {
        super("Client");
    }

    @Override
    protected void channelReadCustom(ChannelHandlerContext channelHandlerContext, DatagramPacket msg) {
        String msgInfo = msg.content().toString(CharsetUtil.UTF_8);
        log.info("i am one udpClient, i receive msg is [{}]", msgInfo);
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        sendPingMsg(ctx);
    }

    /* @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("i am here1...");
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("one client send msg to udpServer..."
                , CharsetUtil.UTF_8), new InetSocketAddress("127.0.0.1",6666)));
    }*/

}
