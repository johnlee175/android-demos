package aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import android.util.Log;

/**
 * Created by lingyi.mly on 2016/5/27.
 */
@Aspect
public class MethodLogTraceAspect {
    private static final String TAG = "Trace";

    @Pointcut("execution(void com..*Activity.onCreate(android.os.Bundle))")
    public void pointcut_Activity_onCreate_Execution() {}

    @Before("pointcut_Activity_onCreate_Execution()")
    public void before_Activity_onCreate_Execution(JoinPoint joinPoint) {
        Log.w(TAG, "!!!!!!!!!!!@@@@@@@@@@@@##########$$$$$$$$$%%%%%%%%%%" + joinPoint.getSignature().toLongString());
    }
}