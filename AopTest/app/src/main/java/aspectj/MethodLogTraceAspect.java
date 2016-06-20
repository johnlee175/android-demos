package aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import android.util.Log;

/**
 * @author John Kenrinus Lee
 * @version 2016-06-19
 */
@Aspect
public class MethodLogTraceAspect {
    private static final String TAG = "LogTrace";

    @Pointcut("execution(void com..*Activity.onCreate(android.os.Bundle))")
    public void pointcut_Activity_onCreate_Execution() {}

    @Before("pointcut_Activity_onCreate_Execution()")
    public void before_Activity_onCreate_Execution(JoinPoint joinPoint) {
        Log.w(TAG, "!!!!!!!!!!!@@@@@@@@@@@@##########$$$$$$$$$%%%%%%%%%%" + joinPoint.getSignature().toLongString());
    }
}