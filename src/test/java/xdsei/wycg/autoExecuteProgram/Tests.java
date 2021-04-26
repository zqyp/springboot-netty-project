package xdsei.wycg.autoExecuteProgram;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import oshi.util.Util;
import xdsei.wycg.autoExecuteProgram.DTO.RelatedStatusReportMessage;
import xdsei.wycg.autoExecuteProgram.service.UtilizationService;
import xdsei.wycg.autoExecuteProgram.threadRelation.task.ExecuteExternalProgramTask;
import xdsei.wycg.autoExecuteProgram.util.SpringContextUtil;

/**
 * @author ZPww
 * @since 2021/4/11
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Tests {

    @Test
    public void testNettyServerStart() {
    }

    @Test
    public void test01() {
        ExecuteExternalProgramTask bean = SpringContextUtil.getBean(ExecuteExternalProgramTask.class);

    }

    @Test
    public void test02() {
        for (;;) {
            System.out.println("cpu:"+UtilizationService.CPU_UTILIZATION_TASK.getCpuUtilization());
            System.out.println("disk"+UtilizationService.DISK_UTILIZATION_TASK.getDiskUtilization());
            Util.sleep(1000);
        }
    }


    public void test03() {
        RelatedStatusReportMessage relatedStatusReportMessage = new RelatedStatusReportMessage();
        relatedStatusReportMessage.setMachineType("53");
    }


}
