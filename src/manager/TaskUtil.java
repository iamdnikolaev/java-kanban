package manager;

import task.Task;

import java.time.LocalDateTime;

public class TaskUtil {
    public static boolean isCrossing(Task t1, Task t2) {
        boolean result = false;
        LocalDateTime t1StartTime = t1.getStartTime();
        LocalDateTime t2StartTime = t2.getStartTime();
        LocalDateTime t1EndTime = t1.getEndTime();
        LocalDateTime t2EndTime = t2.getEndTime();

        if (t1StartTime != null && t2StartTime != null) {
            result = t1StartTime.equals(t2StartTime);

            if (!result && t1EndTime != null && t2EndTime != null) {
                result = t1EndTime.equals(t2EndTime);

                if (!result) {
                    if (t1StartTime.isBefore(t2StartTime)) {
                        result = t1EndTime.isAfter(t2StartTime);
                    } else {
                        result = t1StartTime.isBefore(t2EndTime);
                    }
                }
            }
        }
        return result;
    }
}
