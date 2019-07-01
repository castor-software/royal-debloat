package se.kth.jbroom.debloat;

import java.util.Map;
import java.util.Set;

public class UsageLogger {

    private static Map<String, Set<String>> usageLogger;

    public UsageLogger(Map<String, Set<String>> usageLogger) {
        this.usageLogger = usageLogger;
    }

    public static void setUsageLogger(Map<String, Set<String>> usageLogger) {
        UsageLogger.usageLogger = usageLogger;
    }

    public static Map<String, Set<String>> getUsageLogger() {
        return usageLogger;
    }

}
