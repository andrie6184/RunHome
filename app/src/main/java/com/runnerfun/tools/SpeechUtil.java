package com.runnerfun.tools;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.runnerfun.RunApplication;

import timber.log.Timber;

/**
 * SpeechUtil
 * Created by andrie on 16/12/27.
 */

public class SpeechUtil implements SpeechSynthesizerListener {

    protected static final int UI_LOG_TO_VIEW = 0;

    private SpeechSynthesizer speechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "RHBaiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";

    private Context context;

    public SpeechUtil() {
        this.context = RunApplication.getAppContex();
        init();
    }

    /**
     * 初始化合成相关组件
     *
     * @date 2015-4-14 下午1:36:53
     */
    private void init() {
        initialEnvironment();
        speechSynthesizer = SpeechSynthesizer.getInstance();
        speechSynthesizer.setContext(context);
        speechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件
        // 的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了正式离线授权，不需要设置该参数，
        // 建议将该行代码删除（离线引擎）
        // 如果合成结果出现临时授权文件将要到期的提示，说明使用了临时授权文件，请删除临时授权即可。
        // speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        speechSynthesizer.setAppId("9133815");
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        speechSynthesizer.setApiKey("oGTUgGwrGXiZ5DyOTbMWyojb", "93a1e3758aff0714793778dcc28ddbe5");
        // 设置Mix模式的合成策略
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);

        // 初始化tts
        speechSynthesizer.initTts(TtsMode.MIX);
        setParams();
    }

    private void initialEnvironment() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        FileUtils.makeDir(mSampleDirPath);
        FileUtils.copyFromAssetsToSdcard(context, false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        FileUtils.copyFromAssetsToSdcard(context, false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        FileUtils.copyFromAssetsToSdcard(context, false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        // FileUtils.copyFromAssetsToSdcard(context, false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        FileUtils.copyFromAssetsToSdcard(context, false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        FileUtils.copyFromAssetsToSdcard(context, false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        FileUtils.copyFromAssetsToSdcard(context, false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    /**
     * 开始文本合成并朗读
     *
     * @param content
     * @date 2015-4-14 下午1:47:05
     */
    public void speak(final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setParams();
                int ret = speechSynthesizer.speak(content);
                if (ret != 0) {
                    Timber.e("inf", "开始合成器失败：" + ret);
                }
            }
        }).start();
    }

    /**
     * 取消本次合成并停止朗读
     *
     * @date 2015-4-14 下午2:20:33
     */
    public void stop() {
        speechSynthesizer.stop();
    }

    /**
     * 暂停文本朗读，如果没有调用speak(String)方法或者合成器初始化失败，该方法将无任何效果
     *
     * @date 2015-4-14 下午2:21:07
     */
    public void pause() {
        speechSynthesizer.pause();
    }

    /**
     * 继续文本朗读，如果没有调用speak(String)方法或者合成器初始化失败，该方法将无任何效果
     *
     * @date 2015-4-14 下午2:21:29
     */
    public void resume() {
        speechSynthesizer.resume();
    }

    /**
     * 为语音合成器设置相关参数
     *
     * @date 2015-4-14 下午1:45:11
     */
    private void setParams() {
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");//发音人，目前支持女声(0)和男声(1)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "6");//音量，取值范围[0, 9]，数值越大，音量越大
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");//朗读语速，取值范围[0, 9]，数值越大，语速越快
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");//音调，取值范围[0, 9]，数值越大，音量越高
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE,
                SpeechSynthesizer.AUDIO_ENCODE_AMR);//音频格式，支持bv/amr/opus/mp3，取值详见随后常量声明
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE,
                SpeechSynthesizer.AUDIO_BITRATE_AMR_15K85);//音频比特率，各音频格式支持的比特率详见随后常量声明
    }

    @Override
    public void onSynthesizeStart(String s) {

    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

    }

    @Override
    public void onSynthesizeFinish(String s) {

    }

    @Override
    public void onSpeechStart(String s) {

    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {

    }

    @Override
    public void onSpeechFinish(String s) {

    }

    @Override
    public void onError(String s, SpeechError speechError) {

    }

    private void toPrint(String message) {
        if (message != null && !message.equals("")) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

}
