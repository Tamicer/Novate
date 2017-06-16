package com.tamic.novate.util;

import android.os.Environment;
import android.util.Log;


import com.tamic.novate.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * LogWraper
 * @author Tamic(https://github.com/NeglectedByBoss)
 */
public final class LogWraper {
    private static boolean sDebug = BuildConfig.DEBUG;
    public static final String LOG_TAG = "down";
    public static final boolean DEBUG_DEBUG = true;
    public static final boolean DEBUG_ERROR = true;
    public static final boolean DEBUG_PERFORMENCE = true;
    public static final boolean DEBUG_INFO = true;
    public static final boolean DEBUG_VERBOSE = true;
    public static final boolean DEBUG_WARN = true;
    public static final boolean DEBUG_EXCEPT = true;
    private static FileOutputStream mOutfilestream;
    private static FileOutputStream mLogcaOutfilestream;
    private static boolean mIsLogToFile = false;
    private static String mFolderName = Environment.getExternalStorageDirectory() + File.separator + "Tamic"
            + File.separator + "down" + File.separator + "log" + File.separator;
    private static String mLogFileName = mFolderName + "fastDownlaoder_log.txt";
    private static String mLogFileNameLogcat = mFolderName + "fastDownlaoder_lasttime_log.txt";

    /**
     * LogLevel
     */
    private enum LogLevel {
        /**
         * DEBUG Level
         */
        DEBUG,
        /**
         * ERROR Level
         */
        ERROR,
        /**
         * INFO Level
         */
        INFO,
        /**
         * VERBOSE Level
         */
        VERBOSE,
        /**
         * WARN Level
         */
        WARN,
    }

    /**
     * Constructor
     */
    private LogWraper() {
    }

    /**
     * 设置debug开关
     *
     * @param aDebug true打开，false关闭
     */
    public static void setDebug(boolean aDebug) {
        sDebug = aDebug;
    }

    /**
     * @return is BuildConfig.DEBUG
     */
    public static boolean isDebug() {
        return sDebug;
    }


    public static void d(String aTag, String aMessage) {
        if (sDebug && DEBUG_DEBUG) {
            doLog(LogLevel.DEBUG, aTag, aMessage, null);
        }
    }


    public static void d(String aMessage) {
        if (sDebug && DEBUG_DEBUG) {
            doLog(LogLevel.DEBUG, LOG_TAG, aMessage, null);
        }
    }


    public static void d(String aMessage, Throwable aThrow) {
        if (sDebug && DEBUG_DEBUG) {
            doLog(LogLevel.DEBUG, LOG_TAG, aMessage, aThrow);
        }
    }


    public static void d(String aTag, String aMessage, Throwable aThrow) {
        if (sDebug && DEBUG_DEBUG) {
            doLog(LogLevel.DEBUG, aTag, aMessage, aThrow);
        }
    }


    public static void p(String aMessage) {
        if (sDebug && DEBUG_PERFORMENCE) {
            doLog(LogLevel.DEBUG, LOG_TAG, aMessage, null);
        }
    }


    public static void p(String aTag, String aMessage) {
        if (sDebug && DEBUG_PERFORMENCE) {
            doLog(LogLevel.DEBUG, aTag, aMessage, null);
        }
    }


    public static void e(String aTag, String aMessage) {
        if (DEBUG_ERROR) {
            doLog(LogLevel.ERROR, aTag, aMessage, null);
        }
    }

    public static void e(String aMessage) {
        //Debug版Release版都会打印
        if (DEBUG_ERROR) {
            doLog(LogLevel.ERROR, LOG_TAG, aMessage, null);
        }
    }


    public static void e(String aMessage, Throwable aThrow) {
        if (DEBUG_ERROR) {
            doLog(LogLevel.ERROR, LOG_TAG, aMessage, aThrow);
        }
    }


    public static void i(String aTag, String aMessage) {
        if (sDebug && DEBUG_INFO) {
            doLog(LogLevel.INFO, aTag, aMessage, null);
        }
    }


    @SuppressWarnings("unused")
    public static void i(String aMessage) {
        if (sDebug && DEBUG_INFO) {
            doLog(LogLevel.INFO, LOG_TAG, aMessage, null);
        }
    }

    @SuppressWarnings("unused")
    public static void i(String aMessage, Throwable aThrow) {
        if (sDebug && DEBUG_INFO) {
            doLog(LogLevel.INFO, LOG_TAG, aMessage, aThrow);
        }
    }


    @SuppressWarnings("unused")
    public static void v(String aTAG, String aMessage) {
        if (sDebug && DEBUG_VERBOSE) {
            doLog(LogLevel.VERBOSE, aTAG, aMessage, null);
        }
    }


    @SuppressWarnings("unused")
    public static void v(String aMessage) {
        if (sDebug && DEBUG_VERBOSE) {
            doLog(LogLevel.VERBOSE, LOG_TAG, aMessage, null);
        }
    }


    @SuppressWarnings("unused")
    public static void v(String aMessage, Throwable aThrow) {
        if (sDebug && DEBUG_VERBOSE) {
            doLog(LogLevel.VERBOSE, LOG_TAG, aMessage, aThrow);
        }
    }


    public static void w(String aMessage) {
        if (sDebug && DEBUG_WARN) {
            doLog(LogLevel.WARN, LOG_TAG, aMessage, null);
        }
    }

    public static void w(String aTag, String aMessage) {
        if (sDebug && DEBUG_WARN) {
            doLog(LogLevel.WARN, aTag, aMessage, null);
        }
    }


    public static void w(String aTag, String aMessage, Throwable aThrow) {
        if (sDebug && DEBUG_WARN) {
            doLog(LogLevel.WARN, aTag, aMessage, aThrow);
        }
    }


    public static void w(String aMessage, Throwable aThrow) {
        if (sDebug && DEBUG_WARN) {
            doLog(LogLevel.WARN, LOG_TAG, aMessage, aThrow);
        }
    }


    public static void printStackTrace(Exception aException) {
        if (sDebug && DEBUG_EXCEPT) {
            aException.printStackTrace();
        }
    }


    private static void doLog(LogLevel aLevel, String aTag, String aMessage, Throwable aThrow) {

        if (aMessage == null) {
            aMessage = "";
        }

        switch (aLevel) {
            case DEBUG:
                if (aThrow == null) {
                    Log.d(aTag, aMessage);
                } else {
                    Log.d(aTag, aMessage, aThrow);
                }
                break;
            case ERROR:
                if (aThrow == null) {
                    Log.e(aTag, aMessage);
                } else {
                    Log.e(aTag, aMessage, aThrow);
                }
                break;
            case INFO:
                if (aThrow == null) {
                    Log.i(aTag, aMessage);
                } else {
                    Log.i(aTag, aMessage, aThrow);
                }
                break;
            case VERBOSE:
                if (aThrow == null) {
                    Log.v(aTag, aMessage);
                } else {
                    Log.v(aTag, aMessage, aThrow);
                }
                break;
            case WARN:
                if (aThrow == null) {
                    Log.w(aTag, aMessage);
                } else {
                    Log.w(aTag, aMessage, aThrow);
                }
                break;
            default:
                break;
        }

        if (mIsLogToFile) {
            flushToFile(aTag, aMessage);
        }

    }


    public static void dumpLogcat() {

        BufferedReader localBufferedReader = null;
        try {

            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return;
            }

            File folder = new File(mFolderName);

            if (!folder.exists()) {
                folder.mkdirs();
                //  myFile.createNewFile();
            }

            if (null == mLogcaOutfilestream) {
                mLogcaOutfilestream = new FileOutputStream(mLogFileNameLogcat);
            }

            Process localProcess = Runtime.getRuntime().exec("logcat -v time -d");
            InputStream localInputStream = localProcess.getInputStream();
            InputStreamReader localInputStreamReader = new InputStreamReader(localInputStream);
            localBufferedReader = new BufferedReader(localInputStreamReader);

            for (String str1 = localBufferedReader.readLine(); str1 != null; str1 = localBufferedReader
                    .readLine()) {
                mLogcaOutfilestream.write(str1.getBytes("UTF-8"));
                mLogcaOutfilestream.write("\n".getBytes());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (localBufferedReader != null) {
                    localBufferedReader.close();
                }
                if (mLogcaOutfilestream != null) {
                    mLogcaOutfilestream.close();
                    mLogcaOutfilestream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private static void flushToFile(String aTag, String aMessage) {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        try {

            File folder = new File(mFolderName);

            if (!folder.exists()) {
                folder.mkdirs();
                //  myFile.createNewFile();
            }

            if (null == mOutfilestream) {
                mOutfilestream = new FileOutputStream(mLogFileName);
            }
            String output = aTag + " : " + aMessage;
            mOutfilestream.write(output.getBytes("UTF-8"));
            mOutfilestream.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setWriteToFile(boolean bWritetoFile) {
        mIsLogToFile = bWritetoFile;
    }


    public static void logException(String aTag, Exception aException) {
        try {
            if (null == aException) {
                return;
            }

            if (sDebug) {
                aException.printStackTrace();
            }

            LogWraper.d(aTag, "========================= Exception Happened !!================================");

            LogWraper.d(aTag, aException.getMessage());
            StackTraceElement[] stack = aException.getStackTrace();
            //			if (null == stack) { //FindBugs
            //				return;
            //			}
            for (int i = 0; i < stack.length; i++) {
                LogWraper.d(aTag, stack[i].toString());

            }

            LogWraper.d(aTag, "========================= Exception Ended !!================================");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public static void printInvokeTrace(String aTag) {
        StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
        for (int i = 1; i < stackTrace.length; i++) {
            LogWraper.d(aTag + ":  " + stackTrace[i].toString());
        }
    }

    public static void printInvokeTrace(String aTag, int aMax) {
        StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
        int n = Math.min(aMax, stackTrace.length);
        for (int i = 1; i < n; i++) {
            LogWraper.d(aTag + ":  " + stackTrace[i].toString());
        }
    }

}
