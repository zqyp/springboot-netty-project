package xdsei.wycg.autoExecuteProgram.netty.udpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xdsei.wycg.autoExecuteProgram.netty.UdpCustomHeartbeatHandler;
import xdsei.wycg.autoExecuteProgram.netty.udpClient.handler.UdpClientHandler;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 *
 * @author ZQYP
 * @since 2021/4/28
 */
@Component
public class UdpClient {

    @Value("${udp.server.ip}")
    private String udpServerIp;

    @Value("${udp.server.port}")
    private int udpServerPort;

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Random random = new Random(System.currentTimeMillis());
        bootstrap.group(workGroup).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(3,0,0));
                        ch.pipeline().addLast(new UdpClientHandler());
                    }
                });
        try {
            Channel ch = bootstrap.bind(0).sync().channel();
            for (int i = 0; i < 10; i++) {
                String content = "i am udpClient, i send msg to you..."+ i;
                ByteBuf buffer = ch.alloc().buffer();
                buffer.writeInt(5 + content.getBytes().length);
                buffer.writeByte(UdpCustomHeartbeatHandler.CUSTOM_MSG);
                buffer.writeBytes(content.getBytes());

                ch.writeAndFlush(new DatagramPacket(buffer, new InetSocketAddress(udpServerIp, udpServerPort)));

                Thread.sleep(random.nextInt(20000));
            }
            ch.closeFuture().sync().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
