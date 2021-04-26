package xdsei.wycg.autoExecuteProgram.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import xdsei.wycg.autoExecuteProgram.schedule.AppScheduleBean;

/**
 * 暂时的模拟了下1s发一个报文，相关处理的上报报文规则好像不是这个
 * @author ZPww
 * @since 2021/4/19
 */

@Configuration
@ConfigurationProperties(prefix = "related-process-program.report-message")
//@EnableScheduling
@Setter
@Getter
public class ReportMsgConfig {

    /**
     * 定时任务表达式
     */
    private String cron;


    @Bean
    public AppScheduleBean getAppScheduleBean() {
        return new AppScheduleBean();
    }
}
