package org.chusete81.poc_cc.common;

import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    public static String getEnvironmentVariable (String variableName) {
        return System.getenv(variableName);
    }

    public static Map<String, String> getPocCCEnvironment () {
        Map<String, String> mEnvVars = System.getenv();
        return mEnvVars.entrySet().stream()
                .filter(e -> e.getKey().startsWith("POC_CC_"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
