package xdsei.wycg.autoExecuteProgram.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @author ZPww
 * @since 2021/4/11
 */

@Configuration
@ConfigurationProperties(prefix = "related-process-program.netty.server")
@Setter
@Getter
public class NettyServerConfig {

    /**
     * netty server的监听端口号
     */
    private int port;

}
