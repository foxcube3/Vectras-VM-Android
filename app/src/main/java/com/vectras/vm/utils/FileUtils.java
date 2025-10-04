package com.vectras.vm.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

public class FileUtils {

    private static final String TAG = "FileUtils";

    @NonNull
    public static File getExternalFilesDirectory(@NonNull Context context) {
        return new File(context.getExternalFilesDir(null), "Documents/VectrasVM");
    }

    public static void chmod(@NonNull File file, int mode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Path path = Paths.get(file.getAbsolutePath());
                // This is a simplified representation of chmod, real implementation would be more complex
                // For now, we just ensure the file is readable and writable
                file.setReadable(true, false);
                file.setWritable(true, false);
                file.setExecutable(true, false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting file permissions", e);
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(@NonNull Context context, @NonNull final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
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
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return copyFileToInternalStorage(context, uri, "cache");
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static String copyFileToInternalStorage(Context context, Uri uri, String newDirName) {
        File outputDir = new File(context.getCacheDir(), newDirName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        String fileName = getFileNameFromUri(context, uri);
        File outputFile = new File(outputDir, fileName);

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error copying file to internal storage", e);
            return null;
        }
        return outputFile.getAbsolutePath();
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if(nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    public static void deleteDirectory(File dir) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try (Stream<Path> walk = Files.walk(dir.toPath())) {
                walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        Log.e(TAG, "Error deleting path: " + path, e);
                    }
                });
            }
        } else {
            deleteDirectoryLegacy(dir);
        }
    }

    private static void deleteDirectoryLegacy(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    deleteDirectoryLegacy(new File(dir, child));
                }
            }
        }
        dir.delete();
    }


    public static long getFolderSize(File dir) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try (Stream<Path> walk = Files.walk(dir.toPath())) {
                return walk.filter(Files::isRegularFile).mapToLong(p -> {
                    try {
                        return Files.size(p);
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to get size of " + p, e);
                        return 0L;
                    }
                }).sum();
            }
        } else {
            return getFolderSizeLegacy(dir);
        }
    }

    private static long getFolderSizeLegacy(File dir) {
        long size = 0;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += getFolderSizeLegacy(file);
                    }
                }
            }
        } else {
            size = dir.length();
        }
        return size;
    }


    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
