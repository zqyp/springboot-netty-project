package xdsei.wycg.autoExecuteProgram.netty.udpServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xdsei.wycg.autoExecuteProgram.netty.udpServer.handler.UdpServerHandler;

/**
 * @author ZQYP
 * @since 2021/4/28
 */

@Component
@Slf4j
public class UdpServer {

    @Value("${udp.port}")
    private int port;


    /**
     * 启动 udpServer
     *
     * @throws InterruptedException ex
     */
    public void start() throws InterruptedException {

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workGroup  = new NioEventLoopGroup();
        bootstrap.group(workGroup)
                .channel(NioDatagramChannel.class)
                // 支持广播
                .option(ChannelOption.SO_BROADCAST, true)
                // 添加编码器
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    @Override
                    protected void initChannel(NioDatagramChannel ch) {
                        ch.pipeline().addLast(new UdpServerHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind("127.0.0.1", port).sync();
            if (future.isSuccess()) {
                log.info("Netty Server start! port is [{}]", port);
            }
            future.channel().closeFuture().sync().await();
        } finally {
            workGroup.shutdownGracefully();
        }
    }

}
