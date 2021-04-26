package xdsei.wycg.autoExecuteProgram.threadRelation.task.Utilization;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import oshi.util.Util;

import java.io.File;

/**
 * @author ZPww
 * @since 2021/4/21
 */
@Slf4j
public class DiskUtilizationTask implements Runnable {

    private volatile String diskUtilization;
    private String diskPath;

    public DiskUtilizationTask(String diskPath) {
        this.diskPath = diskPath;
    }

    @Override
    public void run() {
        for(;;) {
            //log.error("{} get disk...", Thread.currentThread().getName());
            this.diskUtilization = diskUtilization(diskPath);
            Util.sleep(1000);
        }
    }


    public String getDiskUtilization() {
        while (null == diskUtilization) {
            Util.sleep(1);
        }
        return this.diskUtilization;
    }


    /**
     * disk
     * @param diskPath C: ":"必须 否则会报除零异常
     * @return 使用率 xx.x%
     */
    private static String diskUtilization(String diskPath) {
        File file = new File(diskPath);
        double usedSpace = -1;
        try {
            usedSpace = NumberUtil.div((file.getTotalSpace() - file.getUsableSpace()), file.getTotalSpace());
        } catch (ArithmeticException ae) {
            log.error("disk path error! [{}]", diskPath);
        }
        // 0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。
        return NumberUtil.decimalFormat("#0.0%", usedSpace);
    }
}
