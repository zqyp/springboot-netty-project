package xdsei.wycg.autoExecuteProgram.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ZPww
 * @since 2021/4/14
 */

@Configuration
@ConfigurationProperties(prefix = "related-process-program.execute")
@Setter
public class ExecuteExternalProgramCmdConfig {

    /**
     * 外部程序路径
     */
    private String externalProgramPaths;

    public List<String> getExternalProgramPaths() {
        if(null == externalProgramPaths)
            return null;
        List<String> externalProgramPathList = new ArrayList<>(10);
        Collections.addAll(externalProgramPathList, externalProgramPaths.split(";"));
        return externalProgramPathList;
    }
}
