package xdsei.wycg.autoExecuteProgram.netty.tcpClient.handler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import xdsei.wycg.autoExecuteProgram.netty.TcpCustomHeartbeatHandler;

/**
 * @author ZPww
 * @since 2021/4/10
 */

@Slf4j
public class TcpClientHandler extends TcpCustomHeartbeatHandler {

    public TcpClientHandler() {
        super("Client");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel Client Active .....");
    }

    @Override
    protected void channelReadCustom(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes() - 5];
        byteBuf.skipBytes(5);
        byteBuf.readBytes(data);
        String content = new String(data);
        log.info("[{}] get content [{}]", name, content);
    }


    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        sendPingMsg(ctx);
    }

    /**
     * 异常数据捕获
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
