package xdsei.wycg.autoExecuteProgram.netty.tcpServer.outBoundHandler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 所有出站的异常处理类
 * @author ZQYP
 * @since 2021/5/9
 */
@Slf4j
public class OutBoundExceptionHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("tcp server sent a msg..."+msg);
        promise.addListener((ChannelFutureListener) future -> {
            if(!future.isSuccess()) {
                future.channel().writeAndFlush("an error");
                future.cause().printStackTrace();
                future.channel().close();
            }
        });
        super.write(ctx, msg, promise);
    }
}
