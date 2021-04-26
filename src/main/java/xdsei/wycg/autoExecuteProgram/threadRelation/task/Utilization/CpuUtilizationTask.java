package xdsei.wycg.autoExecuteProgram.threadRelation.task.Utilization;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.util.Util;

/**
 * @author ZPww
 * @since 2021/4/21
 */
@Slf4j
public class CpuUtilizationTask implements Runnable {

    private volatile String cpuUtilization;
    private static SystemInfo systemInfo = new SystemInfo();

    @Override
    public void run() {
        for(;;) {
            //log.error("{} get cpu...", Thread.currentThread().getName());
            this.cpuUtilization = cpuUtilization();
            Util.sleep(1000);
        }
    }


    public String getCpuUtilization() {
        while (null == cpuUtilization) {
            Util.sleep(1);
        }
        return this.cpuUtilization;
    }


    /**
     * cpu
     * @return 使用率 xx.x%
     */
    private static String cpuUtilization() {

        CentralProcessor processor = systemInfo.getHardware().getProcessor();

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();

        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        // System.out.println("cpu当前使用率:" + new DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu)));
        // 0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。
        return NumberUtil.decimalFormat("#0.0%", 1.0 - NumberUtil.div(idle * 1.0, totalCpu));
    }

}
