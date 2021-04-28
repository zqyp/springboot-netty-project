package xdsei.wycg.autoExecuteProgram.netty.udpClient.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;


/**
 * @author ZQYP
 * @since 2021/4/28
 */
@Slf4j
public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String msgInfo = packet.content().toString(CharsetUtil.UTF_8);
        log.info("i am one udpClient, i receive msg is [{}]", msgInfo);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("i am here1...");
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("one client send msg to udpServer..."
                , CharsetUtil.UTF_8), new InetSocketAddress("127.0.0.1",6666)));
    }

}
