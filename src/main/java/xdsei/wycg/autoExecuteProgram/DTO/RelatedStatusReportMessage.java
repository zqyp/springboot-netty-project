package xdsei.wycg.autoExecuteProgram.DTO;

import lombok.Data;

/**
 *
 * 相关-状态报文
 * @author ZPww
 * @since 2021/4/14
 */

@Data
public class RelatedStatusReportMessage {

    /**
     * 帧头
     */
    private String frameHeader = "{";

    /**
     * 设备种类区分
     */
    private String machineType;

    /**
     * 时间戳
     */
    private String timeUnix;

    /**
     * CPU利用率
     */
    private String cpuUtilization;

    /**
     * 硬盘使用率
     */
    private String diskUtilization;

    /**
     * 总任务编号
     */
    private String totalTaskNo;

    /**
     * 当前相关任务编号
     */
    private String currentRelatedTaskNo;

    /**
     * 相关目标
     */
    private String relatedGoal;

    /**
     * 参与相关处理的站
     */
    private String involvedRelatedProcessStation;

    /**
     * 状态标识
     */
    private String taskStatus;

    /**
     * 相关结果
     */
    private String relatedResult;

    /**
     * 时差结果数
     */
    private String timeDiffResult;

    /**
     * 出错代码
     */
    private String errCode;

    /**
     * 帧尾
     */
    private static String FRAME_TAIL = "}";

}
