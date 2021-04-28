package xdsei.wycg.autoExecuteProgram.netty.udpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import xdsei.wycg.autoExecuteProgram.netty.udpClient.handler.UdpClientHandler;

/**
 * @author ZQYP
 * @since 2021/4/28
 */
public class UdpClient {

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        bootstrap.group(workGroup).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new UdpClientHandler());
                    }
                });
        try {
            Channel channel = bootstrap.bind(0).sync().channel();
            channel.closeFuture().sync().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
