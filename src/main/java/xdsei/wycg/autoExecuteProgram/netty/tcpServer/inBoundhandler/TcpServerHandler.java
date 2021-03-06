package xdsei.wycg.autoExecuteProgram.netty.tcpServer.inBoundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import xdsei.wycg.autoExecuteProgram.netty.AbstractTcpCustomHeartbeatHandler;

/**
 * @author ZQYP
 * @since 2021/4/30
 */
@Slf4j
public class TcpServerHandler extends AbstractTcpCustomHeartbeatHandler {


    public TcpServerHandler() {
        super("Server");
    }

    @Override
    protected void channelReadCustom(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        ByteBuf responseBuf = Unpooled.copiedBuffer(byteBuf);
        byte[] data = new byte[byteBuf.readableBytes() - 5];
        int totalBytes = byteBuf.getInt(0);
        byteBuf.skipBytes(5);
        byteBuf.readBytes(data);
        String content = new String(data);
        log.info("[{}] receive [{}] bytes, and get content is [{}]", name, totalBytes, content);
        ctx.channel().writeAndFlush(responseBuf);
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        log.error("[{}] reader timeout, close it---", ctx.channel().remoteAddress().toString());
        // 关闭客户端连接
        //ctx.close();
    }
}
