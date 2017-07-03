package com.tamic.novate.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * by Taimic copy from
 * https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
 */
public class FileUtil {
    /** TAG for log messages. */
    static final String TAG = "FileUtils";
    private static final int DEFAULT_COMPRESS_QUALITY = 100;
    private static final String PATH_DOCUMENT = "document";
    private static final String PATH_TREE = "tree";
    private static final boolean DEBUG = false; // Set to true to enable logging
    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";
    private static final String DEFAULT_FILENAME = "novatedownfile";
    /**content disposition */
    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(
            "attachment;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*$", Pattern.CASE_INSENSITIVE);
    /** content disposition 2 */
    private static final Pattern CONTENT_DISPOSITION_PATTERN_2 = Pattern.compile(
            "inline;\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*$", Pattern.CASE_INSENSITIVE);

    public static final String HIDDEN_PREFIX = ".";

    private FileUtil() {} //private constructor to enforce Singleton pattern


    private static String getFilePathFromUri(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * get SDPath, if no sdcard, return null
     * 
     * @return
     */
    public static String getSDPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
            Log.e("SDCard", "no sdcard found!");
            return null;
        }
    }

    public static String getBasePath(Context context) {
        if (null == getSDPath()) {
            return context.getCacheDir().getAbsolutePath();
        }

        File file = new File(getSDPath() + File.separator + context.getPackageName());
        if (!file.exists()) {
            file.mkdir();
        }

        return file.getAbsolutePath();
    }



    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /** loadFromAssets
     * @param context
     * @param fileName
     * @return
     */
    public static String loadFromAssets(Context context, String fileName) {
        BufferedReader reader = null;
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            reader = new BufferedReader(new InputStreamReader(in));

            char[] buf = new char[1024];
            int count = 0;
            StringBuffer sb = new StringBuffer(in.available());
            while ((count = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, count);
                sb.append(readData);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(reader);
        }

        return "";
    }

    /**
     * 删除文件
     * 
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void deleteFiles(ArrayList<String> filePaths) {
        for (int i = 0; i < filePaths.size(); i++) {
            deleteFile(filePaths.get(i));
        }
    }

    @NonNull
    public static File getUirFile(Uri fileUri){
        return new File(fileUri.getPath());
    }




    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     *         null if uri was null.
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * @return Whether the URI is a local one.
     */
    public static boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
    public static String getMimeType(File file) {

        String extension = getExtension(file.getName());

        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    /**
     * @return The MIME type for the give Uri.
     */
    public static String getMimeType(Context context, Uri uri) {
        File file = new File(getPath(context, uri));
        return getMimeType(file);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is {@link LocalStorageProvider}.
     * @author paulburke
     *//*
    public static boolean isLocalStorageDocument(Uri uri) {
        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
    }*/

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
           /* if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }*/
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     *         Uri is unsupported or pointed to a remote resource.
     * @see #getPath(Context, Uri)
     * @author paulburke
     */
    public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (path != null && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    public static String getReadableFileSize(int size) {
        final int BYTES_IN_KILOBYTES = 1024;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        float fileSize = 0;
        String suffix = KILOBYTES;

        if (size > BYTES_IN_KILOBYTES) {
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }
        return String.valueOf(dec.format(fileSize) + suffix);
    }

    /**
     * Attempt to retrieve the thumbnail of given File from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param file
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, File file) {
        return getThumbnail(context, getUri(file), getMimeType(file));
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, Uri uri) {
        return getThumbnail(context, uri, getMimeType(context, uri));
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @param mimeType
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, Uri uri, String mimeType) {
        if (DEBUG)
            Log.d(TAG, "Attempting to get thumbnail");

        if (!isMediaUri(uri)) {
            Log.e(TAG, "You can only retrieve thumbnails for images and videos.");
            return null;
        }

        Bitmap bm = null;
        if (uri != null) {
            final ContentResolver resolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    final int id = cursor.getInt(0);
                    if (DEBUG)
                        Log.d(TAG, "Got thumb ID: " + id);

                    if (mimeType.contains("video")) {
                        bm = MediaStore.Video.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Video.Thumbnails.MINI_KIND,
                                null);
                    }
                    else if (mimeType.contains(FileUtil.MIME_TYPE_IMAGE)) {
                        bm = MediaStore.Images.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Images.Thumbnails.MINI_KIND,
                                null);
                    }
                }
            } catch (Exception e) {
                if (DEBUG)
                    Log.e(TAG, "getThumbnail", e);
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return bm;
    }

    /**
     * File and folder comparator. TODO Expose sorting option method
     *
     * @author paulburke
     */
    public static Comparator<File> sComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };

    /**
     * File (not directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return files only (not directories) and skip hidden files
            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Folder (directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return directories only and skip hidden directories
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     * @author paulburke
     */
    public static Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    /**
     * 根据url获取文件名
     */
    public static String getFileNameWithURL(String url) {
        String name = null;
        if (!TextUtils.isEmpty(url)) {
            String decodedUrl = Uri.decode(url);
            if (!TextUtils.isEmpty(decodedUrl)) {
                // 先获取extension
                String extension = null;
                int ext = decodedUrl.lastIndexOf(".");
                if (ext != -1 && decodedUrl.length() - ext < 5) {
                    extension = decodedUrl.substring(ext);
                }
                int query = decodedUrl.indexOf("?");
                if (query > 0) {
                    decodedUrl = decodedUrl.substring(0, query);
                }
                query = decodedUrl.indexOf("#");
                if (query > 0) {
                    decodedUrl = decodedUrl.substring(0, query);
                }
                if (!decodedUrl.endsWith("/")) {
                    int index = decodedUrl.lastIndexOf("/") + 1;
                    if (index > 0) {
                        name = decodedUrl.substring(index);
                    }
                }
                if (!TextUtils.isEmpty(extension) && !TextUtils.isEmpty(name) && !name.contains(".")) {
                    name += extension;
                }
            }
        }
        return name;
    }


    /**
     * 解码获取文件名
     */
    private static String decodeContentdisposition(String contentdisposition) {
        String name = parseContentDispostion(contentdisposition);
        if (TextUtils.isEmpty(name)) {
            return name;
        } else {
            try {
                byte[] bytesname = name.getBytes("utf-8");
                if (isUTF8(bytesname, bytesname.length)) {
                    return new String(bytesname, "utf-8");
                } else if (isGBK(bytesname)) {
                    return new String(bytesname, "GBK");
                } else {
                    return new String(bytesname, "gb2312");
                }
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "UnsupportedEncodingException");
                return null;
            }
        }
    }

    /**
     * 解析 content disposition 获取文件名
     */
    private static String parseContentDispostion(String disposition) {
        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(disposition);
            if (m.find()) {
                return m.group(2);
            }
            m = CONTENT_DISPOSITION_PATTERN_2.matcher(disposition);
            if (m.find()) {
                return m.group(2);
            }
        } catch (Exception ex) {
            Log.d(TAG, "fail to parse content dispostion");
        }
        return null;
    }

    /**
     * 判断是否utf8编码
     */
    private static boolean isUTF8(byte[] b, int aMaxCount) {
        int lLen = b.length, lCharCount = 0;
        for (int i = 0; i < lLen && lCharCount < aMaxCount; ++lCharCount) {
            byte lByte = b[i++];
            if (lByte >= 0) {
                continue;
            }
            if (lByte < (byte) 0xc0 || lByte > (byte) 0xfd) {
                return false;
            }
            int lCount = lByte > (byte) 0xfc ? 5 : lByte > (byte) 0xf8 ? 4 : lByte > (byte) 0xf0 ? 3
                    : lByte > (byte) 0xe0 ? 2 : 1;
            if (i + lCount > lLen) {
                return false;
            }
            for (int j = 0; j < lCount; ++j, ++i) {
                if (b[i] >= (byte) 0xc0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否GBK
     */
    private static boolean isGBK(byte[] bytesname) {
        return true;
    }

    /**
     * 根据content disposition生成文件名
     */
    public static String getFileName(String disposition, String url) {
        String filename = decodeContentdisposition(disposition);
        if (TextUtils.isEmpty(filename)) {
            filename = getFileNameWithURL(url);
        }
        if (TextUtils.isEmpty(filename)) {
            filename = DEFAULT_FILENAME;
        }
        return filename;
    }

    /**
     * 根据content disposition生成文件名
     */
    public static String generateFileKey(String url, String name) {
        return System.currentTimeMillis() + url + name;
    }

    /**
     * 新建下载文件
     *
     * @param savePath
     * @return
     */
    public static File createDownloadFile(String savePath, String fileName) {
        if (TextUtils.isEmpty(savePath)) {
            throw new RuntimeException("you should define downloadFolder path!");
        }
        if (TextUtils.isEmpty(fileName)) {
            throw new RuntimeException("you should define downloadFileName !");
        }
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(savePath, fileName);
        return file;
    }

    public static boolean exists(@NonNull File file){
        /*if (!file.exists()) {
            throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
        }*/
        return file.exists();
    }

}
