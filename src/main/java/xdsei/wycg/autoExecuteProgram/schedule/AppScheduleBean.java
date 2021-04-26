package xdsei.wycg.autoExecuteProgram.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import xdsei.base.ScheduleTaskBase;
import xdsei.wycg.autoExecuteProgram.service.ReportRunningStatusService;

/**
 * 应用程序自己的定时任务定义类。
 * @author zp
 */
@Slf4j
public class AppScheduleBean extends ScheduleTaskBase {

	public AppScheduleBean() {
		super("autoExecuteProgram-relatedProcessProgram Module");
	}

	@Autowired
	private ReportRunningStatusService reportRunningStatusService;

	@Override
	protected void initWork() {

	}

	@Override
	protected void stopWork() {
		// TODO Auto-generated method stub
	}

	@Scheduled(cron="${related-process-program.report-message.cron:0 5 0 * * ?}")
	public void runScheduledTask() {
		reportRunningStatusService.doReportRunningStatus();
	}

}
