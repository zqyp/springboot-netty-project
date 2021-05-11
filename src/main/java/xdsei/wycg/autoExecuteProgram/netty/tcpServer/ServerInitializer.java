package xdsei.wycg.autoExecuteProgram.netty.tcpServer;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import xdsei.wycg.autoExecuteProgram.netty.tcpServer.outBoundHandler.OutBoundExceptionHandler;
import xdsei.wycg.autoExecuteProgram.netty.tcpServer.inBoundhandler.ClientMonitorHandler;
import xdsei.wycg.autoExecuteProgram.netty.tcpServer.inBoundhandler.ExternalProgramHandler;
import xdsei.wycg.autoExecuteProgram.netty.tcpServer.inBoundhandler.TcpServerHandler;

/**
 * @author ZQYP
 * @since 2021/4/10
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder(CharsetUtil.UTF_8);
    private static final StringEncoder ENCODER = new StringEncoder(CharsetUtil.UTF_8); 



    /**
     * 入站处理器初始化管道 ChannelPipeline.
     * @param ch socketChannel
     * @throws Exception ex
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // pipeline 中存储所有serverHandler
        ChannelPipeline pipeline = ch.pipeline();

        // 指定包以“}”结束，只接受“}”之前的，先这样吧，根据控制指定格式改
       /* ByteBuf delimiter = Unpooled.copiedBuffer("}".getBytes());
        // 超过2048个字符数会报异常
        pipeline.addLast("frameTail", new DelimiterBasedFrameDecoder(2048, delimiter));*/

        /*pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);*/

        // ctx 若在出站处理器之前，则ctx.write() 不会传递到此出站处理器，但ctx.channel.write()可以
        // 为了规避潜在错误 出站就放在最前面
        //pipeline.addFirst("outBoundHandler" ,new OutBoundExceptionHandler());

        pipeline.addLast(new IdleStateHandler(10, 0, 0));
        pipeline.addLast( "tcpServerHandler",new TcpServerHandler());

        // 业务逻辑实现类
        /*pipeline.addLast(CLIENT_MONITOR_HANDLER);
        pipeline.addLast(EXTERNAL_PROGRAM_HANDLER);*/

    }
}
