package xdsei.wycg.autoExecuteProgram.netty.tcpServer;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import xdsei.wycg.autoExecuteProgram.netty.tcpServer.handler.ClientMonitorHandler;
import xdsei.wycg.autoExecuteProgram.netty.tcpServer.handler.ExternalProgramHandler;

/**
 * ChannelInitializer 继承了ChannelInboundHandlerAdapter，这是一个入站处理器.
 * @author ZQYP
 * @since 2021/4/10
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder(CharsetUtil.UTF_8);
    private static final StringEncoder ENCODER = new StringEncoder(CharsetUtil.UTF_8);


    private static final ClientMonitorHandler CLIENT_MONITOR_HANDLER = new ClientMonitorHandler();
    private static final ExternalProgramHandler EXTERNAL_PROGRAM_HANDLER = new ExternalProgramHandler();


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
        ByteBuf delimiter = Unpooled.copiedBuffer("}".getBytes());
        // 超过2048个字符数会报异常
        pipeline.addLast("frameTail", new DelimiterBasedFrameDecoder(2048, delimiter));

        pipeline.addLast(DECODER);
        pipeline.addLast(ENCODER);
        // 业务逻辑实现类
        pipeline.addLast(CLIENT_MONITOR_HANDLER);
        pipeline.addLast(EXTERNAL_PROGRAM_HANDLER);

    }
}
