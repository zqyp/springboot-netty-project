package xdsei.wycg.autoExecuteProgram.netty.tcpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author ZPww
 * @since 2021/4/10
 */

@Component
public class TcpClient {

    EventLoopGroup group = new NioEventLoopGroup();

    @PostConstruct
    public void start() throws Exception{
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            Channel ch = b.connect("127.0.0.1",7777).sync().channel();
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                // Sends the received line to the server.
                lastWriteFuture = ch.writeAndFlush(line + "\r\n");
                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    ch.closeFuture().sync();
                    break;
                }
            }
            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }

        } finally {
            group.shutdownGracefully();
        }
    }

}
