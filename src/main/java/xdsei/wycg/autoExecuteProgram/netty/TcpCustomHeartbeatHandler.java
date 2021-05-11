package xdsei.wycg.autoExecuteProgram.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 1) 客户端设置读写超时检测时间 allIdleTime = 5
 * 2) 客户端若在5秒之内没有write 或 read 消息，则触发检测事件 userEventTriggered
 * 3) 对应的超时条件下发送 ping 消息给服务端
 * 4) 服务端设置读超时检测时间 readIdleTime = 10, 收到客户端的 ping后发送 pong消息给客户端
 * 5）以上为一个心跳检测
 *
 * @author ZQYP
 * @since 2021/4/29
 */
@Slf4j
public abstract class TcpCustomHeartbeatHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 客户端发送的报文格式：
     *                    [报文长度(4 byte)] [消息类型(1 byte)] [消息内容(n bytes)]
     */
    public static final byte PING_MSG = 1;
    public static final byte PONG_MSG = 2;
    public static final byte CUSTOM_MSG = 3;
    protected String name;
    private int heartbeatCount = 0;
    /**
     * 报文类型的索引
     */
    public static final int DATAGRAM_TYPE_INDEX = 4;

    public TcpCustomHeartbeatHandler(String name) {
        this.name = name;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        if(byteBuf.getByte(DATAGRAM_TYPE_INDEX) == PING_MSG) {
            sendPongMsg(ctx);
        }
        else if(byteBuf.getByte(DATAGRAM_TYPE_INDEX) == PONG_MSG) {
            log.info("[{}] get pong msg from [{}]", name, ctx.channel().remoteAddress());
        }
        else {
            channelReadCustom(ctx, byteBuf);
        }
    }


    /**
     * 对读到的数据进行处理的方法
     * @param channelHandlerContext ctx
     * @param byteBuf b
     */
    protected abstract void channelReadCustom(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.error( "[{}] is active---", ctx.channel().remoteAddress());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error( "[{}] is inactive---", ctx.channel().remoteAddress());
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


    /**
     * ping 报文类型：[字节长度] [消息类型]
     * @param ctx ctx
     */
    protected void sendPingMsg(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(PING_MSG);
        ctx.channel().writeAndFlush(buf);
        heartbeatCount++;
        log.info(name + " sent ping msg to " + ctx.channel().remoteAddress() + ", count: " + heartbeatCount);
    }


    /**
     * pong 报文类型：[字节长度] [消息类型]
     * @param ctx ctx
     */
    private void sendPongMsg(ChannelHandlerContext ctx) {
        ByteBuf buf = ctx.alloc().buffer(5);
        buf.writeInt(5);
        buf.writeByte(PONG_MSG);
        ctx.channel().writeAndFlush(buf);
        heartbeatCount++;
        log.info(name + " sent pong msg to " + ctx.channel().remoteAddress() + ", count: " + heartbeatCount);
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
