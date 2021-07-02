package xdsei.wycg.autoExecuteProgram.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xdsei.wycg.autoExecuteProgram.config.NettyUdpServerConfig;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * ping pong 机制，是客户端向服务端发请求，服务端接收到请求发送响应，客户端接收到响应，这是一次ping pong。
 *
 *（X）如果两个都做服务端，监听不同的端口，互发 ping ,以对方是否收到 ping 来判断是否在线，则即便是双方的 读或写空闲时间相同，
 * 但启动时间也不同，因此真正的触发时间还是有先后顺序，且还有其他问题。。所以只会是 一方不停 触发 读空闲发送 ping，另一方不会
 * 触发读空闲,也不会发送 ping。
 *
 * 客户端与服务端连接中断的情况：
 *  1）客户端触发读超时，则客户端发送 ping 给服务端
 *     若中断，则服务端收不到 ping 消息，触发服务端的读超时，此时认为客户端掉线。
 *  2）若服务端收到 ping, 则发送 pong 给客户端，若客户端收不到 pong，则触发读超时，再次发送ping,
 *     且此时 udpClientPingCount > 1，则认为服务端断开连接，若收到 pong 则重置udpClientPingCount。
 *
 * 考虑网络因素：
 *    客户端在服务端掉线 指定时间（客户端读空闲时间 * 4）后发现服务端掉线。
 *    前三次可以认为是网络不稳定。
 *
 * @author ZQYP
 * @since 2021/4/30
 */
@Slf4j
@Component
public abstract class AbstractUdpCustomHeartbeatHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    protected String appName;

    @Autowired
    private NettyUdpServerConfig nettyUdpServerConfig;

    /**
     * 客户端发送的报文格式：
     *                    [报文长度(4 byte)] [消息类型(1 byte)] [消息内容(n bytes)]
     */
    public static final byte PING_MSG = 1;
    public static final byte PONG_MSG = 2;
    public static final byte CUSTOM_MSG = 3;
    /**
     * 消息类型的索引
     */
    public static final int DATAGRAM_TYPE_INDEX = 4;

    /**
     * 判断服务端是否还连接，客户端 ping 服务端，udpClientPingCount++, 客户端收到 pong 后重置为 0。
     */
    protected AtomicInteger udpClientPingCount = new AtomicInteger(0);

    public AbstractUdpCustomHeartbeatHandler(String appName) {
        this.appName = appName;
    }

    public AbstractUdpCustomHeartbeatHandler(){}


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf content = msg.content();
        if(PING_MSG == content.getByte(DATAGRAM_TYPE_INDEX)) {
            sendPongMsg(ctx, msg);
        } else if(PONG_MSG == content.getByte(DATAGRAM_TYPE_INDEX)) {
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
     * ping 报文类型：[字节长度 4 bytes] [消息类型 1 byte]
     * @param ctx ctx
     */
    protected void sendPingMsg(ChannelHandlerContext ctx) {
        ByteBuf buffer = ctx.alloc().buffer(5);
        buffer.writeInt(5);
        buffer.writeByte(PING_MSG);
        ctx.writeAndFlush(new DatagramPacket(buffer, new InetSocketAddress(nettyUdpServerConfig.getIp()
                , nettyUdpServerConfig.getPort())));
        // ctx.channel().remoteAddress() == null
        log.info(appName + " send ping msg to server");
    }


    /**
     * pong 报文类型：[字节长度 4 bytes] [消息类型 1 byte]
     * @param ctx ctx
     */
    protected void sendPongMsg(ChannelHandlerContext ctx, DatagramPacket msg) {
        ByteBuf buffer = ctx.alloc().buffer(5);
        buffer.writeInt(5);
        buffer.writeByte(PONG_MSG);
        DatagramPacket datagramPacket = new DatagramPacket(buffer, msg.sender());
        ctx.writeAndFlush(datagramPacket);
        log.info("[{}] send pong msg to [{}]...", appName, msg.sender());
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
        log.info("--- READER_IDLE ---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        log.info("--- WRITER_IDLE ---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        log.info("--- ALL_IDLE ---");
    }

}
