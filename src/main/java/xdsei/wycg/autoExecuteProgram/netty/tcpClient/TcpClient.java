package xdsei.wycg.autoExecuteProgram.netty.tcpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import xdsei.wycg.autoExecuteProgram.netty.TcpCustomHeartbeatHandler;

import java.util.Random;

/**
 * 有连接
 * @author ZPww
 * @since 2021/4/10
 */

public class TcpClient {

    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            Random random = new Random(System.currentTimeMillis());
            b.group(group)
                    //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            ChannelFuture f = b.connect("127.0.0.1",7777).sync();

            for (int i = 0; i < 10; i++) {
                String content = "client msg " + i;
                ByteBuf buf = f.channel().alloc().buffer();
                // 1 个 int 型 32bit = 4 个Byte
                buf.writeInt(content.getBytes().length);
                // 1 个 Byte
                buf.writeByte(TcpCustomHeartbeatHandler.CUSTOM_MSG);
                buf.writeBytes(content.getBytes());
                f.channel().writeAndFlush(buf);

                Thread.sleep(random.nextInt(20000));
            }
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
