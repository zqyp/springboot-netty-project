package xdsei.wycg.autoExecuteProgram.netty.udpServer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import xdsei.wycg.autoExecuteProgram.netty.UdpCustomHeartbeatHandler;



/**
 * server 收到 某一个客户端发来的消息，即DatagramPacket，packet里存储着发送端的 ip+port
 * client 要给服务端发送消息需要指定 ip+host
 *  *
 * @author ZQYP
 * @since 2021/4/28
 */
@Slf4j
public class UdpServerHandler extends UdpCustomHeartbeatHandler {

    public UdpServerHandler() {
        super("Server");
    }


    /**
     * 处理读数据
     * @param ctx ctx
     * @param packet p
     */
    @Override
    public void channelReadCustom(ChannelHandlerContext ctx, DatagramPacket packet) {
        ByteBuf byteBuf = packet.content();
        int packetLength = byteBuf.readInt();
        byteBuf.skipBytes(1);
        String content = byteBuf.toString(CharsetUtil.UTF_8);
        log.info("i am udpServer, i received [{}] bytes, and content is [{}]", packetLength, content);
    }


    /**
     * 服务端可以在客户端掉线 指定时间（服务端读空闲时间） 后发现客户端掉线
     * @param ctx ctx
     */
    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        // do something 很可能客户端掉线
        log.error("udpClient disConnected...");
}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }

}
