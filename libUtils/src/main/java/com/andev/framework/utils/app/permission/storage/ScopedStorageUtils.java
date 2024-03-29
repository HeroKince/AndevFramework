package com.andev.framework.utils.app.permission.storage;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import com.andev.framework.utils.common.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 1.https://github.com/fuweiwei/ScopedStorage
 * 2.https://github.com/SMask/ShareLibrary_Android
 * 3.https://github.com/xluu233/AndroidStorageDemo
 * 4.https://github.com/wanghao200906/ScopeStorage
 * 5.https://github.com/HurryYU/ScopedStorage
 */
public class ScopedStorageUtils {

    public final static String VEER_PHOTOS_OUTSIDE_STORE = Environment.DIRECTORY_PICTURES + File.separator + "veer/";
    public final static String VEER_VIDEO_OUTSIDE_STORE = Environment.DIRECTORY_MOVIES + File.separator + "veer/";
    public final static String VEER_FILE_OUTSIDE_STORE = Environment.DIRECTORY_DOCUMENTS + File.separator + "veer/";

    public static boolean writeFileFromString(final File file, final String content, final boolean append) {
        if (file == null || content == null) return false;
        if (!createOrExistsFile(file)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 保存图片到相册
     *
     * @param bitmap
     * @param fileName
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean saveBitmapPhotos(Context context, Bitmap bitmap, String fileName) {
        boolean result = false;
        String name = fileName;
        //        String mimeType = "image/jpeg";
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        String path = VEER_PHOTOS_OUTSIDE_STORE;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        //        contentValues.put(MediaStore.MediaColumns.MIME_TYPE ,mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                bitmap.compress(compressFormat, 100, outputStream);
                outputStream.close();
                result = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bitmap.recycle();
            }
        }
        return result;
    }

    /**
     * 保存视频到相册
     *
     * @param videoFile
     * @param fileName
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean saveVideoPhotos(Context context, File videoFile, String fileName) {
        boolean result = false;
        if (!FileUtils.isFileExists(videoFile)) return result;
        String name = fileName;
        String path = VEER_VIDEO_OUTSIDE_STORE;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
        Uri uri = null;
        try {
            uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uri != null) {
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(videoFile));
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                byte[] buffer = new byte[1024];
                int bytes = bis.read(buffer);
                while (bytes >= 0) {
                    bos.write(buffer, 0, bytes);
                    bos.flush();
                    bytes = bis.read(buffer);
                }
                bos.close();
                bis.close();
                result = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
        return result;
    }

    /**
     * 保存文件到外部存储  Android 10 以上可用 10以下还是使用之前的存储
     *
     * @param file
     * @param fileName
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean saveFileOutSide(Context context, File file, String fileName) {
        boolean result = false;
        if (!FileUtils.isFileExists(file)) return result;
        String name = fileName;
        String path = VEER_FILE_OUTSIDE_STORE;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
        Uri uri = null;
        try {
            uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uri != null) {
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                byte[] buffer = new byte[1024];
                int bytes = bis.read(buffer);
                while (bytes >= 0) {
                    bos.write(buffer, 0, bytes);
                    bos.flush();
                    bytes = bis.read(buffer);
                }
                bos.close();
                bis.close();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
        return result;
    }

    /**
     * 获取 Authority(7.0Uri适配)
     *
     * @param context context
     * @return String Authority
     */
    public static String getAuthority(Context context) {
        return context.getPackageName() + ".FileProvider";
    }

    /**
     * 获取 FileUri
     *
     * @param context context
     * @param file    file
     * @return Uri
     */
    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, getAuthority(context), file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 获取 ContentUri
     *
     * @param context context
     * @param file    file(特定的文件才能查询到)
     * @return Uri
     */
    public static Uri getContentUri(Context context, File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        Uri uri = null;
        String path = file.getAbsolutePath();
        String mimeType = getMimeType(file.getName());

        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = getContentUri(mimeType);
        String[] projection = new String[]{MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[]{path};
        Cursor cursor = contentResolver.query(contentUri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idCol = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                if (idCol >= 0) {
                    uri = ContentUris.withAppendedId(contentUri, cursor.getLong(idCol));
                }
            }
            cursor.close();
        }
        return uri;
    }

    /**
     * 获取 重复文件Uri
     *
     * @param context context
     * @param file    file
     * @return Uri
     */
    public static Uri getDuplicateFileUri(Context context, File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        Uri uri = null;
        String name = file.getName();
        long size = file.length();
        String mimeType = getMimeType(name);

        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = getContentUri(mimeType);
        String[] projection = new String[]{MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?" + " AND " + MediaStore.MediaColumns.SIZE + "=?";
        String[] selectionArgs = new String[]{name, String.valueOf(size)};
        Cursor cursor = contentResolver.query(contentUri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idCol = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                if (idCol >= 0) {
                    uri = ContentUris.withAppendedId(contentUri, cursor.getLong(idCol));
                }
            }
            cursor.close();
        }
        return uri;
    }

    /**
     * 获取 Path
     * <p>
     * 注意：
     * 只作为Debug使用，实际开发不建议使用，毕竟获取path没有什么意义，用Uri来操作更好
     *
     * @param context context
     * @param uri     uri(特定的Uri才能查询到，比如系统媒体库的Uri)
     * @return String Path
     */
    public static String getPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        String path = null;
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int pathCol = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                if (pathCol >= 0) {
                    path = cursor.getString(pathCol);
                }
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 获取 Name
     *
     * @param context context
     * @param uri     uri
     * @return String Name
     */
    public static String getName(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
        if (documentFile != null) {
            return documentFile.getName();
        }
        return null;

//        String name = null;
//        ContentResolver contentResolver = context.getContentResolver();
//        String[] projection = new String[]{MediaStore.MediaColumns.DISPLAY_NAME};
//        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                int nameCol = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
//                if (nameCol >= 0) {
//                    name = cursor.getString(nameCol);
//                }
//            }
//            cursor.close();
//        }
//        return name;
    }

    /**
     * 获取 文件扩展名
     *
     * @param name name
     * @return String Extension
     */
    public static String getExtension(String name) {
        int index = name.lastIndexOf(".");
        if (index > 0) {
            return name.substring(index + 1);
        }
        return "";
    }

    /**
     * 获取 文件MimeType
     *
     * @param name name
     * @return String MimeType
     */
    public static String getMimeType(String name) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(name));
    }

    /**
     * 获取 文件MimeType
     *
     * @param context context
     * @param uri     uri
     * @return String MimeType
     */
    public static String getMimeType(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

//        DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
//        if (documentFile != null) {
//            return documentFile.getType();
//        }
//        return null;

        return context.getContentResolver().getType(uri);
    }

    /**
     * 获取 Uri(根据mimeType)
     *
     * @param mimeType mimeType
     * @return Uri
     */
    public static Uri getContentUri(String mimeType) {
        Uri contentUri;
        if (mimeType.startsWith("image")) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (mimeType.startsWith("video")) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if (mimeType.startsWith("audio")) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }
        return contentUri;
    }

    /**
     * 获取 文件夹名称(根据mimeType)
     *
     * @param mimeType mimeType
     * @return String dirName
     */
    public static String getDirName(String mimeType) {
        String dirName;
        if (mimeType.startsWith("image")) {
            dirName = Environment.DIRECTORY_PICTURES;
        } else if (mimeType.startsWith("video")) {
            dirName = Environment.DIRECTORY_PICTURES;
        } else if (mimeType.startsWith("audio")) {
            dirName = Environment.DIRECTORY_MUSIC;
        } else {
            dirName = Environment.DIRECTORY_DOCUMENTS;
        }
        return dirName;
    }

    /**
     * 复制文件到外部
     * <p>
     * 访问共享存储空间中的媒体文件：https://developer.android.com/training/data-storage/shared/media#add-item
     *
     * @param context context
     * @param dirName 目录名(例如："/Pictures/WeChat"中的"WeChat")
     * @param file    file
     * @return Uri
     */
    public static Uri copyFileToExternal(Context context, String dirName, File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        // 获取是否有重复的文件，避免重复复制
        Uri uri = getDuplicateFileUri(context, file);
        if (uri != null) {
            return uri;
        }

        String name = file.getName();
        String mimeType = getMimeType(name);

        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = getContentUri(mimeType);

        // 插入参数
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);// 文件名
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);// mimeType
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            String dirPath = getDirName(mimeType);
            if (!TextUtils.isEmpty(dirName)) {
                dirPath += File.separatorChar + dirName;
            }
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, dirPath);// 相对路径
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);// 文件的处理状态(防止写入过程中被其他App查询到，写入完成后记得修改回来)
        }

        // 获取插入的Uri
        uri = contentResolver.insert(contentUri, values);
        if (uri == null) {
            return null;
        }

        // 复制文件
        boolean copySuccess = false;
        try (OutputStream outputStream = contentResolver.openOutputStream(uri);
             InputStream inputStream = new FileInputStream(file)) {
            copySuccess = copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 复制失败则删除
        if (!copySuccess) {
            delete(context, uri);
            return null;
        }

        // 更新文件的处理状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear();
            values.put(MediaStore.MediaColumns.IS_PENDING, 0);// 文件的处理状态(防止写入过程中被其他App查询到，写入完成后记得修改回来)

            contentResolver.update(uri, values, null, null);
        }

        return uri;
    }

    /**
     * 复制
     * {@link android.os.FileUtils#copy(InputStream, OutputStream)}
     *
     * @param inputStream  inputStream
     * @param outputStream outputStream
     * @return boolean
     */
    public static boolean copy(InputStream inputStream, OutputStream outputStream) {
        try {
            byte[] buffer = new byte[8192];
            int byteRead;
            while ((byteRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, byteRead);
            }
            outputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除
     * <p>
     * 注意：
     * 只适用于系统文件选择器返回的Uri，其他Uri会报错
     * <p>
     * 从共享存储空间访问文档和其他文件：https://developer.android.com/training/data-storage/shared/documents-files#delete
     *
     * @param context context
     * @param uri     uri
     * @return boolean
     */
    public static boolean deleteSystem(Context context, Uri uri) {
        boolean delete = false;

        ContentResolver contentResolver = context.getContentResolver();

        try {
            delete = DocumentsContract.deleteDocument(contentResolver, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return delete;
    }

    /**
     * 删除
     * <p>
     * 注意：
     * 只适用于应用自身创建的媒体文件；
     * 文档等其他类型文件无法删除，其他App的文件也无法删除成功，只能删除媒体库里的Uri数据，实际文件并没有删除。
     * 操作其他App的数据需要用户授予权限，catch RecoverableSecurityException 异常，然后请求权限，具体见官方文档。
     * <p>
     * 访问共享存储空间中的媒体文件：https://developer.android.com/training/data-storage/shared/media#remove-item
     *
     * @param context context
     * @param uri     uri
     * @return boolean
     */
    public static boolean delete(Context context, Uri uri) {
        boolean delete = false;

        ContentResolver contentResolver = context.getContentResolver();

        try {
            delete = contentResolver.delete(uri, null, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return delete;
    }

}