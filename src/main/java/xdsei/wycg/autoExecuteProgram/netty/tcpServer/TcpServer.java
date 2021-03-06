package xdsei.wycg.autoExecuteProgram.netty.tcpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xdsei.util.Tools;
import xdsei.wycg.autoExecuteProgram.config.NettyServerConfig;


/**
 *
 * @author ZPww
 * @since 2021/4/10
 */
@Component
@Slf4j
public class TcpServer {

    /**
     * boss 线程组用于处理连接工作
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    /**
     * work 线程组用于数据处理 ，默认为cpu核数的两倍
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup(12);

    @Autowired
    private NettyServerConfig nettyServerConfig;

    public TcpServer() {}

    /**
     * 启动Netty Server
     *
     * @throws InterruptedException ex
     */
    public void start() throws InterruptedException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 设置要被实例化的为 NioServerSocketChannel 类
                    .channel(NioServerSocketChannel.class)

                    //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                    .option(ChannelOption.SO_BACKLOG, 100)

                    // 设置 NioServerSocketChannel 的处理器
                    .handler(new LoggingHandler(LogLevel.INFO))

                    //childHandler 处理 IO
                    .childHandler(new ServerInitializer());
            ChannelFuture future = bootstrap.bind(nettyServerConfig.getPort()).sync();

            // 注册ChannelFutureListener,以便在操作完成时获得通知
            future.addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    log.info("Netty TcpServer start! port is"+ nettyServerConfig.getPort());
                }else {
                    log.error("Netty TcpServer start failed! port is"+ nettyServerConfig.getPort());
                    Tools.logException("Netty TcpServer start failed!" , future1.cause());
                }
            });
            // sync -- 同步阻塞 关闭  服务端channel
            // 直到有一个客户端连接上才会触发 心跳检测
            future.channel().closeFuture().sync();
            System.out.println("i am here1");
        } finally {
            System.out.println("i am here2");
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }


    public void destroy() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
        log.info("Netty server shutdown!");
    }

}
