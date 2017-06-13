package com.tamic.novate.download;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tamic on 2017-06-13.
 */

public class MimeType {

    /** html */
    public static final String HTML = "html";
    /** txt */
    public static final String TXT = "txt";
    /** doc */
    public static final String DOC = "doc";
    /** docx */
    public static final String DOCX = "docx";
    /** xls */
    public static final String XLS = "xls";
    /** xlsx */
    public static final String XLSX = "xlsx";
    /** ppt */
    public static final String PPT = "ppt";
    /** pptx */
    public static final String PPTX = "pptx";
    /** pdf */
    public static final String PDF = "pdf";
    /** jpg */
    public static final String JPG = "jpg";
    /** jpeg */
    public static final String JPEG = "jpeg";
    /** png */
    public static final String PNG = "png";
    /** bmp */
    public static final String BMP = "bmp";
    /** gif */
    public static final String GIF = "gif";
    /** mp4 */
    public static final String MP4 = "mp4";
    /** mkv */
    public static final String MKV = "mkv";
    /** mpg */
    public static final String MPG = "mpg";
    /** mpeg */
    public static final String MPEG = "mpeg";
    /** wmv */
    public static final String WMV = "wmv";
    /** avi */
    public static final String AVI = "avi";
    /** rmvb */
    public static final String RMVB = "rmvb";
    /** rm */
    public static final String RM = "rm";
    /** 3gp */
    public static final String ThreeGP = "3gp";
    /** flv */
    public static final String FLV = "flv";
    /** apk */
    public static final String APK = "application/vnd.android.package-archive";
    /** mp3 */
    public static final String MP3 = "mp3";
    /** wma */
    public static final String WMA = "wma";
    /** wav */
    public static final String WAV = "wav";
    /** zip */
    public static final String ZIP = "zip";
    /** rar */
    public static final String RAR = "rar";
    /** gzip */
    public static final String GZIP = "gzip";
    /** gz */
    public static final String GZ = "gz";
    /** bz2 */
    public static final String BZ2 = "bz2";
    /** lzh */
    public static final String CAB = "cab";
    /** 7z */
    public static final String Z7 = "7z";

    private static MimeType sInstance;
    private List<String> mMimeTypeList;

    private MimeType() {
        mMimeTypeList = new ArrayList<>();
        loadList();
    }

    private void loadList() {

        mMimeTypeList.add(TXT);
        mMimeTypeList.add(HTML);
        mMimeTypeList.add(APK);

        mMimeTypeList.add(GIF);
        mMimeTypeList.add(MPG);
        mMimeTypeList.add(JPG);
        mMimeTypeList.add(JPEG);
        mMimeTypeList.add(PNG);
        mMimeTypeList.add(BMP);

        mMimeTypeList.add(WMV);
        mMimeTypeList.add(RM);
        mMimeTypeList.add(FLV);
        mMimeTypeList.add(MP3);
        mMimeTypeList.add(MKV);
        mMimeTypeList.add(AVI);
        mMimeTypeList.add(MP4);
        mMimeTypeList.add(MPEG);
        mMimeTypeList.add(WAV);
        mMimeTypeList.add(WMA);
        mMimeTypeList.add(ThreeGP);

        mMimeTypeList.add(RMVB);

        mMimeTypeList.add(RAR);
        mMimeTypeList.add(ZIP);
        mMimeTypeList.add(Z7);
        mMimeTypeList.add(GZIP);
        mMimeTypeList.add(BZ2);
        mMimeTypeList.add(CAB);
        mMimeTypeList.add(GZ);

        mMimeTypeList.add(PDF);
        mMimeTypeList.add(DOC);
        mMimeTypeList.add(DOCX);
        mMimeTypeList.add(XLS);
        mMimeTypeList.add(XLSX);
        mMimeTypeList.add(PPT);
        mMimeTypeList.add(PPTX);

    }

    public String getSuffix(String mediaType) {
        if (TextUtils.isEmpty(mediaType)) {
            return null;
        }
        for (String type: mMimeTypeList) {
            if (mediaType.contains(mediaType)){
                return "."+ type;
            }
        }
        return null;
    }

    public static MimeType getInstance() {
        if (sInstance == null) {
            sInstance = new MimeType();
        }
        return sInstance;
    }

}
