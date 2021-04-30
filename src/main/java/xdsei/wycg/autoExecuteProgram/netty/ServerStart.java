package xdsei.wycg.autoExecuteProgram.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xdsei.util.Tools;
import xdsei.wycg.autoExecuteProgram.netty.tcpServer.TcpServer;
import xdsei.wycg.autoExecuteProgram.netty.udpServer.UdpServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 启动 netty Server的类
 *
 * @author ZPww
 * @since 2021/4/10
 */
@Slf4j
@Component
public class ServerStart {

    @Autowired
    private TcpServer tcpServer;

    @Autowired
    private UdpServer udpServer;

    /**
     *  PostConstruct在构造方法初始化、autowired之后执行
     */
    //@PostConstruct
    public void tcpServerStart() {
        try {
            tcpServer.start();
        } catch (Exception e) {
            Tools.logException("netty Server start failed! {}", e);
        }

    }

    public void tcpServerDestroy() {
        try {
            tcpServer.destroy();
        } catch (Exception e) {
            Tools.logException("netty Server shutdown failed! {}", e);
        }
    }


    @PostConstruct
    public void udpServerStart() {
        try {
            udpServer.start();
        } catch (Exception e) {
            Tools.logException("netty Server start failed! {}", e);
        }
    }

}
