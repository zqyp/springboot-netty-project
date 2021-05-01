package xdsei.wycg.autoExecuteProgram.netty.udpClient.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import xdsei.wycg.autoExecuteProgram.netty.UdpCustomHeartbeatHandler;



/**
 * 客户端与服务端连接中断的情况：
 *  1）客户端触发读超时，则客户端发送 ping 给服务端
 *     若中断，则服务端收不到 ping 消息，触发服务端的读超时，此时认为客户端掉线。
 *  2）若服务端收到 ping, 则发送 pong 给客户端，若客户端收不到 pong，则触发读超时，再次发送ping,
 *     且此时 udpClientPingCount > 1，则认为服务端断开连接，若收到 pong 则重置udpClientPingCount。
 *
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

    /**
     * 客户端在服务端掉线 指定时间（客户端读空闲时间 * 2）后发现服务端掉线。
     * @param ctx ctx
     */
    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        sendPingMsg(ctx);
        udpClientPingCount.getAndIncrement();
        if(udpClientPingCount.get() >= 2) {
            // 服务端掉线
            log.error("[{}] disConnected...",appName);
        }
    }

}
