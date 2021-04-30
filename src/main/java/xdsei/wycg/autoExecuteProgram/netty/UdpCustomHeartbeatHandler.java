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


/**
 * @author ZQYP
 * @since 2021/4/30
 */
@Slf4j
public abstract class UdpCustomHeartbeatHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    protected String appName;
    public static final String PING_MSG = "ping";
    public static final String PONG_MSG = "pong";


    public UdpCustomHeartbeatHandler(String appName) {
        this.appName = appName;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        if(PING_MSG.equalsIgnoreCase(msg.content().toString(CharsetUtil.UTF_8))) {
            sendPongMsg(ctx, msg);
        } else if(PONG_MSG.equalsIgnoreCase(msg.content().toString(CharsetUtil.UTF_8))) {
            log.info("[{}] get pong msg from [{}]", appName, msg.sender());
        } else {
            channelReadCustom(ctx, msg);
        }
    }

    /**
     * 对读到的数据进行处理的方法
     * @param channelHandlerContext ctx
     * @param msg m
     */
    protected abstract void channelReadCustom(ChannelHandlerContext channelHandlerContext, DatagramPacket msg);


    protected void sendPingMsg(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(PING_MSG,CharsetUtil.UTF_8)
                , new InetSocketAddress("127.0.0.1", 6666)));
        // ctx.channel().remoteAddress() == null
        log.info(appName + " send ping msg to server");
    }


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
        log.error("---READER_IDLE---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        log.error("---WRITER_IDLE---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        log.error("---ALL_IDLE---");
    }

}
