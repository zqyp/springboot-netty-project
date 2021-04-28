package xdsei.wycg.autoExecuteProgram.netty.tcpClient.handler;


import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZPww
 * @since 2021/4/10
 */

@Sharable
@Slf4j
public class TcpClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel Client Active .....");
    }

    /**
     *
     * @param ctx ctx
     * @param msg msg
     * @throws Exception ex
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info(msg.toString());
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
