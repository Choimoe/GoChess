package GoUtil;

import java.io.Closeable;

public class GoUtil {
    public static void close(Closeable... targets) {
        for (Closeable target : targets) {
            try {
                if (target != null)
                    target.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
