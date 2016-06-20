package aspectj;

import java.net.HttpURLConnection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import android.util.Log;

/**
 * @author John Kenrinus Lee
 * @version 2016-06-19
 */
@Aspect
public class HttpURLConnectionTimeTakeTraceAspect {
    private static final String TAG = "TimeTakeTrace";

    @Pointcut("call( public void java.net.HttpURLConnection.connect() throws java.io.IOException )")
    public void pointcut_HttpURLConnection_connect_Call() {}

    @Around("pointcut_HttpURLConnection_connect_Call()")
    public void around_HttpURLConnection_connect_Call(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.w(TAG, "Where intercepted: " + joinPoint.getSourceLocation());
        long start = System.currentTimeMillis();
        joinPoint.proceed();
        Log.w(TAG, "Intercepted URL: " + ((HttpURLConnection)joinPoint.getTarget()).getURL()
                + ", Time take:  " + (System.currentTimeMillis() - start));
    }
}