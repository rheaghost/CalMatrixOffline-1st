package com.calmatrixoffline;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackupHelper {
    private static final String TAG = "BackupHelper";
    private static final String APP_FOLDER = "CalSnap"; // Change this when renaming app

    public static String getAppFolder() {
        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File appFolder = new File(downloads, APP_FOLDER);
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }
        return appFolder.getAbsolutePath();
    }

    public static String getBackupFolder() {
        File folder = new File(getAppFolder(), "backup");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    public static String getCsvFolder() {
        File folder = new File(getAppFolder(), "csv");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    public static String getImagesFolder() {
        File folder = new File(getAppFolder(), "food_images");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    public static String getHistoryFolder() {
        File folder = new File(getAppFolder(), "history");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    public static String backupDatabase(Context context) {
        try {
            File dbFile = context.getDatabasePath("CalSnap.db");
            if (!dbFile.exists()) {
                Log.e(TAG, "Database file not found");
                return null;
            }

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());
            String backupName = "CalSnap_backup_" + timestamp + ".db";
            File backupFile = new File(getBackupFolder(), backupName);

            FileInputStream src = new FileInputStream(dbFile);
            FileOutputStream dst = new FileOutputStream(backupFile);
            FileChannel srcChannel = src.getChannel();
            FileChannel dstChannel = dst.getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            srcChannel.close();
            dstChannel.close();
            src.close();
            dst.close();

            Log.d(TAG, "Backup saved: " + backupFile.getAbsolutePath());
            return backupFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean restoreDatabase(Context context, String backupPath) {
        try {
            File backupFile = new File(backupPath);
            if (!backupFile.exists()) {
                Log.e(TAG, "Backup file not found");
                return false;
            }

            File dbFile = context.getDatabasePath("CalSnap.db");
            if (dbFile.exists()) {
                dbFile.delete();
            }

            FileInputStream src = new FileInputStream(backupFile);
            FileOutputStream dst = new FileOutputStream(dbFile);
            FileChannel srcChannel = src.getChannel();
            FileChannel dstChannel = dst.getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            srcChannel.close();
            dstChannel.close();
            src.close();
            dst.close();

            Log.d(TAG, "Database restored from: " + backupPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportCSV(Context context, String csvData, String filename) {
        try {
            File file = new File(getCsvFolder(), filename);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(csvData.getBytes());
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String[] listBackups() {
        File folder = new File(getBackupFolder());
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return new String[0];
        }
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName();
        }
        return names;
    }

    //food images related

    public static int preloadSampleImages(Context context) {
        int copied = 0;
        try {
            android.content.res.AssetManager am = context.getAssets();
            String[] files = am.list("sample_images");
            if (files == null || files.length == 0) {
                return 0;
            }

            File destDir = new File(getImagesFolder());

            for (String filename : files) {
                File destFile = new File(destDir, filename);
                if (destFile.exists()) continue;

                java.io.InputStream is = am.open("sample_images/" + filename);
                FileOutputStream fos = new FileOutputStream(destFile);
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();
                copied++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copied;
    }

    public static String[] listImageFiles() {
        File folder = new File(getImagesFolder());
        File[] files = folder.listFiles();
        if (files == null) return new String[0];
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName();
        }
        return names;
    }

    //end food images related

    public static String getImageFullPath(String imageFilename) {
        if (imageFilename == null || imageFilename.isEmpty()) return null;
        File file = new File(getImagesFolder(), imageFilename);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }//helper 2609110622
}