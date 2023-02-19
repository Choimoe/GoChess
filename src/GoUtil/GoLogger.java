package GoUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GoLogger {
    static SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

    /**
     * 通过\033特殊转义字符实现输出格式控制，获得带颜色的字体输出     * @param content, 待格式化的内容
     * @param fontColor, 字体颜色：30黑 31红 32绿 33黄 34蓝 35紫 36深绿 37白
     * */
    private static String getColoredOutputString(String content, int fontColor){
        return String.format("\033[%dm%s\033[0m", fontColor, content);
    }

    public static void log(String from, String content) {
        StackTraceElement sup = new Exception().getStackTrace()[1];
        System.out.println(" [" + getColoredOutputString("LOG", 36) + "]  " + getColoredOutputString(sdf.format(new Date()), 37) + " | " + from + " : " + getColoredOutputString(content, 32) + " in " + getColoredOutputString(sup.getClassName() + "." + sup.getMethodName(), 37));
    }

    public static void debug(String x) {
        StackTraceElement sup = new Exception().getStackTrace()[1];
        System.out.println("[" + getColoredOutputString("DEBUG", 33) + "] " + getColoredOutputString(sdf.format(new Date()), 37) + " | Info : " + getColoredOutputString(x, 32) + " in " + getColoredOutputString(sup.getClassName() + "." + sup.getMethodName(), 37));
    }

    public static void debug(Object object, String x) {
        System.out.println("[" + getColoredOutputString("DEBUG", 33) + "] " + getColoredOutputString(sdf.format(new Date()), 37) + " | Info from " + object.getClass().getSimpleName() + " : " + getColoredOutputString(x, 32));
    }

    public static void error(String x) {
        StackTraceElement sup = new Exception().getStackTrace()[1];
        System.out.println("[" + getColoredOutputString("ERROR", 31) + "] " + getColoredOutputString(sdf.format(new Date()), 37) + " | Error : " + getColoredOutputString(x, 31) + " in " + getColoredOutputString(sup.getClassName() + "." + sup.getMethodName(), 37));
    }
}