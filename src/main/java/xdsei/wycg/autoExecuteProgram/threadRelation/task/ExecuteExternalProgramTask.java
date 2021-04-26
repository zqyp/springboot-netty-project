package xdsei.wycg.autoExecuteProgram.threadRelation.task;

import lombok.extern.slf4j.Slf4j;
import xdsei.util.Tools;
import xdsei.util.exec.CmdExecutor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行外部程序的类
 *
 * @author ZPww
 * @since 2021/4/13
 */

@Slf4j
public class ExecuteExternalProgramTask implements Runnable {

    private String os;
    private String charset;
    private String executeExternalPath;
    private CmdExecutor cmdExecutor;

    public ExecuteExternalProgramTask(String os, String charset, String executeExternalPath
        ,CmdExecutor cmdExecutor) {
        this.os = os;
        this.charset = charset;
        this.executeExternalPath = executeExternalPath;
        this.cmdExecutor = cmdExecutor;
    }


    @Override
    public void run() {
        executeExternalProgram(os, charset, executeExternalPath, cmdExecutor);
    }



    /**
     *
     * @param os os
     * @param charset cs
     * @param executeExternalPath 外部程序路径
     * @param cmdExecutor cmd执行器
     */
    public void executeExternalProgram(String os, String charset, String executeExternalPath, CmdExecutor cmdExecutor) {

        /*try{
            Thread.sleep(10000); }
        catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        StringBuilder executeExternalProgramCmd = new StringBuilder();
        if("windows".equals(os)) {
            executeExternalProgramCmd.append("cmd").append(" ").append("/c").append(" ");
        }else if("linux".equals(os)){
            executeExternalProgramCmd.append("/bin/sh").append(" ").append("-c").append(" ");
        }
        String cmdLine = executeExternalProgramCmd.append(executeExternalPath).toString();
        cmdExecutor.setArguments(cmdLine);

        // 如果没有设置不能显式关闭进程 watchdog == null 啊
        cmdExecutor.setTimeout(10 * 1000);
        log.info("execute program cmdLine is [{}]", cmdLine);
        // 暂时看看打印控制台是啥
        Map<String, List<String>> stdMp = launchCmd(cmdExecutor, charset);

        // System.out.println(cmdExecutor.hasResult());

        for(String s: stdMp.get("stderr")) {
            System.out.print(s+" ");
        }
        System.out.println();
        for(String s: stdMp.get("stdout")) {
            System.out.println(s+" ");
        }

    }

    /**
     * 执行命令
     * @param launcher l
     * @param charset cs
     * @return sss
     */
    public Map<String, List<String>> launchCmd(CmdExecutor launcher, String charset) {
        // 封装 stdout 和 stderr的输出
        Map<String, List<String>> stdMp = new HashMap<>(16);

        CmdExecutor.OutputHandler stderr = new CmdExecutor.OutputHandler();
        CmdExecutor.OutputHandler stdout = new CmdExecutor.OutputHandler();

        stdout.setCharset(charset);
        stderr.setCharset(charset);
        launcher.setOutputHandler(stdout, stderr);

        try {
            launcher.execute();
        }catch(Exception e) {
            Tools.logException("FATAL ERROR in call executeExternalProgram", e);
            stdout.clear();
            stderr.clear();
            throw e;
        }
        stdMp.put("stderr", stderr.output());
        stdMp.put("stdout", stdout.output());
        return stdMp;

    }



}
