package xdsei.wycg.autoExecuteProgram.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * ping pong 机制，是客户端向服务端发请求，服务端接收到请求发送响应，客户端接收到响应，这是一次ping pong。
 *
 * （X）如果两个都做服务端，监听不同的端口，互发 ping ,以对方是否收到 ping 来判断是否在线，则即便是双方的 读或写空闲时间相同，
 *  但启动时间也不同，因此真正的触发时间还是有先后顺序，且还有其他问题。。所以只会是 一方不停 触发 读空闲发送 ping，另一方不会
 *  触发读空闲,也不会发送 ping。
 * @author ZQYP
 * @since 2021/4/30
 */
@Slf4j
public abstract class UdpCustomHeartbeatHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    protected String appName;
    public static final String PING_MSG = "ping";
    public static final String PONG_MSG = "pong";
    /**
     * 判断服务端是否还连接，客户端 ping 服务端，udpClientPingCount++, 客户端收到 pong 后重置为 0。
     */
    protected AtomicInteger udpClientPingCount = new AtomicInteger(0);

    public UdpCustomHeartbeatHandler(String appName) {
        this.appName = appName;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        if(PING_MSG.equalsIgnoreCase(msg.content().toString(CharsetUtil.UTF_8))) {
            // 服务端收到客户端的请求
            sendPongMsg(ctx, msg);
        } else if(PONG_MSG.equalsIgnoreCase(msg.content().toString(CharsetUtil.UTF_8))) {
            // 客户端收到服务端的回应
            log.info("[{}] get pong msg from [{}]", appName, msg.sender());
            udpClientPingCount.set(0);
        } else {
            channelReadCustom(ctx, msg);
        }
    }

    /**
     * 对读到的消息进行处理
     * @param ctx ctx
     * @param packet p
     */
    protected abstract void channelReadCustom(ChannelHandlerContext ctx, DatagramPacket packet);

    /**
     * 服务端对 客户端的 ping 消息回 pong
     * @param ctx ctx
     * @param msg m
     */
    protected void sendPongMsg(ChannelHandlerContext ctx, DatagramPacket msg) {
        ByteBuf responseBuf = Unpooled.copiedBuffer(PONG_MSG, CharsetUtil.UTF_8);
        DatagramPacket datagramPacket = new DatagramPacket(responseBuf, msg.sender());
        ctx.writeAndFlush(datagramPacket);
        log.info("[{}] send pong msg to [{}]...", appName, msg.sender());
    }


    /**
     * 客户端 ping 服务端
     * @param ctx ctx
     */
    protected void sendPingMsg(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(PING_MSG,CharsetUtil.UTF_8)
                , new InetSocketAddress("127.0.0.1", 6660)));
        // ctx.channel().remoteAddress() == null
        log.info(appName + " send ping msg to server");
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }


    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        log.error("--- READER_IDLE ---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        log.error("--- WRITER_IDLE ---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        log.error("--- ALL_IDLE ---");
    }

}
