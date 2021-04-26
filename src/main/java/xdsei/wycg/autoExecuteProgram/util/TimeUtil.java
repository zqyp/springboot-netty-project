package xdsei.wycg.autoExecuteProgram.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Formatter;
import java.util.Locale;

/**
 * @author ZPww
 * @since 2021/4/18
 */
@Component
public class TimeUtil {

    /**
     * 获取当前时间 UTC（返回的是utc+0） 不随系统时区变化
     * @return 秒
     */
    public long getUTCTime() {
        return Instant.now().getEpochSecond();
    }


    /**
     *
     * @param seconds utc时间戳（utc+0） 转年月日
     * @return yyyyMMddHHmmss
     */
    public String getUtcStrBySeconds(Long seconds) {
        return getStrUTC(Instant.ofEpochSecond(seconds));
    }

    /**
     * 转化utc时间戳为年月日
     * @param instant instant
     * @return 年月日
     */
    private String getStrUTC(Instant instant) {

        // utc+0 时区
        ZonedDateTime utcNow = ZonedDateTime.ofInstant(instant, ZoneId.of("+00:00"));
        StringBuilder utcStr = new StringBuilder();
        Formatter formatter = new Formatter(utcStr, Locale.CHINA);
        try {
            formatter.format("%04d%02d%02d%02d%02d%02d",
                    utcNow.getYear(), utcNow.getMonth().getValue(),
                    utcNow.getDayOfMonth(), utcNow.getHour(),
                    utcNow.getMinute(), utcNow.getSecond());
            return formatter.toString();
        }finally {
            formatter.close();
        }
    }
}
