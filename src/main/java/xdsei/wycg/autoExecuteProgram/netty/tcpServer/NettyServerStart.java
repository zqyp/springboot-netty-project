package xdsei.wycg.autoExecuteProgram.netty.tcpServer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xdsei.util.Tools;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 启动 nettyTcpServer的类
 *
 * @author ZPww
 * @since 2021/4/10
 */
@Slf4j
@Component
public class NettyServerStart {

    @Autowired
    private NettyTcpServer nettyTcpServer;


    /**
     *  PostConstruct在构造方法初始化、autowired之后执行
     */
    @PostConstruct
    public void nettyServerStart() {
        try {
            nettyTcpServer.start();
        } catch (Exception e) {
            Tools.logException("netty Server start failed! {}", e);
        }

    }

    @PreDestroy
    public void nettyServerDestroy() {
        try {
            nettyTcpServer.destroy();
        } catch (Exception e) {
            Tools.logException("netty Server shutdown failed! {}", e);
        }
    }
}
