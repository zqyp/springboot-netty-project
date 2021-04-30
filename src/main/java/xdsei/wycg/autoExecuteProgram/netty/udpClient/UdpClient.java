package xdsei.wycg.autoExecuteProgram.netty.udpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;
import xdsei.wycg.autoExecuteProgram.netty.UdpCustomHeartbeatHandler;
import xdsei.wycg.autoExecuteProgram.netty.udpClient.handler.UdpClientHandler;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * 无连接
 * @author ZQYP
 * @since 2021/4/28
 */
@Component
public class UdpClient {

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Random random = new Random(System.currentTimeMillis());
        bootstrap.group(workGroup).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(5,0,0));
                        ch.pipeline().addLast(new UdpClientHandler());
                    }
                });
        try {
            Channel ch = bootstrap.bind(0).sync().channel();
            for (int i = 0; i < 10; i++) {
                ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("Hello Server..."
                        , CharsetUtil.UTF_8)
                        , new InetSocketAddress("127.0.0.1", 6666)));

                Thread.sleep(random.nextInt(20000));
            }
            ch.closeFuture().sync().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
