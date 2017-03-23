//package com.runnerfun.mock;
//
//import android.content.Context;
//import android.os.Environment;
//import android.util.Log;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * LogToFile
// * Created by andrie on 17/3/14.
// */
//
//public class LogToFile {
//
//    private static String TAG = "LogToFile";
//    private static String logPath = null;//log日志存放路径
//    private static String fileName = null;
//    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
//
//    //因为log日志是使用日期命名的，使用静态成员变量主要是为了在整个程序运行期间只存在一个.log文件中;
//    private static Date date = new Date();
//
//    /**
//     * 初始化，须在使用之前设置，最好在Application创建时调用
//     *
//     * @param context
//     */
//    public static void init(Context context) {
//        logPath = getFilePath(context) + "/RunLogs";
//        //获得文件储存路径,在后面加"/Logs"建立子文件夹
//        fileName = logPath + "/log_" + dateFormat.format(new Date()) + ".log";
//        //log日志名，使用时间命名，保证不重复
//    }
//
//    /**
//     * 获得文件存储路径
//     *
//     * @return
//     */
//    private static String getFilePath(Context context) {
//        if (Environment.MEDIA_MOUNTED.equals(Environment.MEDIA_MOUNTED)
//                || !Environment.isExternalStorageRemovable()) {
//            //如果外部储存可用
//            return context.getExternalFilesDir(null).getPath();
//        } else {
//            return context.getFilesDir().getPath();
//            //直接存在/data/data里，非root手机是看不到的
//        }
//    }
//
//    private static final char VERBOSE = 'v';
//    private static final char DEBUG = 'd';
//    private static final char INFO = 'i';
//    private static final char WARN = 'w';
//    private static final char ERROR = 'e';
//
//    public static void v(String tag, String msg) {
//        writeToFile(VERBOSE, tag, msg);
//    }
//
//    public static void d(String tag, String msg) {
//        writeToFile(DEBUG, tag, msg);
//    }
//
//    public static void i(String tag, String msg) {
//        writeToFile(INFO, tag, msg);
//    }
//
//    public static void w(String tag, String msg) {
//        writeToFile(WARN, tag, msg);
//    }
//
//    public static void e(String tag, String msg) {
//        writeToFile(ERROR, tag, msg);
//    }
//
//    /**
//     * 将log信息写入文件中
//     *
//     * @param type
//     * @param tag
//     * @param msg
//     */
//    private static void writeToFile(char type, String tag, String msg) {
//        if (null == logPath || fileName == null) {
//            Log.e(TAG, "logPath == null ，未初始化LogToFile");
//            return;
//        }
//
//        String log = dateFormat.format(new Date()) + " " + type + " " + tag + " " + msg + "\n";
//        //log日志内容，可以自行定制
//
//        //如果父路径不存在
//        File file = new File(logPath);
//        if (!file.exists()) {
//            file.mkdirs();
//            //创建父路径
//        }
//
//        FileOutputStream fos = null;
//        //FileOutputStream会自动调用底层的close()方法，不用关闭
//        BufferedWriter bw = null;
//        try {
//            fos = new FileOutputStream(fileName, true);
//            //这里的第二个参数代表追加还是覆盖，true为追加，flase为覆盖
//            bw = new BufferedWriter(new OutputStreamWriter(fos));
//            bw.write(log);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (bw != null) {
//                    bw.close();
//                    //关闭缓冲流
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//}
