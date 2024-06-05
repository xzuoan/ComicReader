package com.comic.comicreader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class DataOperate {

    Context context;
    String pathInternalCache;
    String pathInternalFile;
    String pathExternalStorage;
    String pathPreviewImageDirName;
    ArrayList<String> fileList = new ArrayList<>();
    final static String[] IMAGE_TYPES = {"WEBP", "JPEG", "JPG", "PNG", "GIF", "BMP", "HEIF"};
    final static String PREVIEW_IMAGE_DIRECTORY_NAME = "preview";
    final static String IMAGE_FORMAT = ".jpeg";


    public DataOperate(Context context) {
        super();
        this.context = context;
        this.pathInternalCache = context.getCacheDir().getAbsolutePath();
        this.pathInternalFile = context.getFilesDir().getAbsolutePath();
        this.pathExternalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.pathPreviewImageDirName = getPathInternalCache() + File.separator + PREVIEW_IMAGE_DIRECTORY_NAME;
    }

    public String getPathInternalCache() {
        return pathInternalCache;
    }

    public String getPathInternalFiles() {
        return pathInternalFile;
    }

    public String getPathExternalStorage() {
        return pathExternalStorage;
    }

    public String getPathPreviewImageDirName() {
        return pathPreviewImageDirName;
    }

    /**
     * 读取assets路径文件
     */
    public synchronized String readAssets(String filename) throws IOException {

        // 获取输入流
        InputStream streamIn = context.getAssets().open(filename);
        // 获取文件字节数
        int length = streamIn.available();
        // 创建byte数组
        byte[] buffer = new byte[length];
        // 将文件中数据写入到字节数组中
        streamIn.read(buffer);
        streamIn.close();

        return new String(buffer);
    }

    /**
     * 读取内存文件
     * 默认路径: data/data/<应用包名>/file?cache
     */
    public String readInternalStorage(String filename) throws IOException {

        FileInputStream inputStream = new FileInputStream(filename);

//        FileInputStream inputStream = context.openFileInput(filename);
        byte[] buffer = new byte[1024];
        StringBuilder stringBuilder = new StringBuilder();
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            stringBuilder.append(new String(buffer, 0, len));
        }
        inputStream.close();
        return stringBuilder.toString();
    }
    public String readInternalCache(String filename) throws IOException {
        filename = getPathInternalCache() + File.separator + filename;
        return readInternalStorage(filename);
    }

    public String readInternalFile(String filename) throws IOException {
        filename = getPathInternalFiles() + File.separator + filename;
        return readInternalStorage(filename);
    }

    public void writeInternalStorage(String filename, String content) throws IOException {

        // MODE_PRIVATE 默认（私有）操作模式，只能够被本应用本身访问，写入内容会覆盖原文件内容
        // MODE_APPEND 会检验文件是否存在，存在的话往文件中追加内容，否则新建文件
        // MODE_WORLD_READABLE 当前文件可被其他应用读取
        // MODE_WORLD_WRITEABLE 当前文件可以被其他应用写入
        FileOutputStream outputStream = new FileOutputStream(filename);

//        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);

        outputStream.write(content.getBytes());
        outputStream.close();
    }

    public void writeInternalCache(String filename, String content) throws IOException {
        filename = getPathInternalCache() + File.separator + filename;
        writeInternalStorage(filename, content);
    }

    public void writeInternalFile(String filename, String content) throws IOException {
        filename = getPathInternalFiles() + File.separator + filename;
        writeInternalStorage(filename, content);
    }


    public boolean isInternalStorageExists(String directory, String filename) {

        if (directory.equalsIgnoreCase("CACHE")) {
            filename = getPathInternalCache() + File.separator + filename;
            return new File(filename).exists();
        } else if (directory.equalsIgnoreCase("FILE")) {
            filename = getPathInternalFiles() + File.separator + filename;
            return new File(filename).exists();
        }
        return false;
    }

    public void copyFromAssets(String fromFilename, String directory, String toFilename) throws IOException {
        String data = readAssets(fromFilename);
        if (directory.equalsIgnoreCase("CACHE")) {
            toFilename = getPathInternalCache() + File.separator + toFilename;
        } else if (directory.equalsIgnoreCase("FILE")) {
            toFilename = getPathInternalFiles() + File.separator + toFilename;
        }
        writeInternalStorage(toFilename, data);
    }

    public void copyFileFromAssets(String oldFilename, String directory, String newFilename) throws IOException {
        if (directory.equalsIgnoreCase("CACHE")) {
            newFilename = getPathInternalCache() + File.separator + newFilename;
        } else if (directory.equalsIgnoreCase("FILE")) {
            newFilename = getPathInternalFiles() + File.separator + newFilename;
        }
        InputStream streamIn = context.getAssets().open(oldFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(newFilename);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = streamIn.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, len);
        }
        streamIn.close();
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public void copyFileFromAssets(String oldFilename, String newFilename) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            newFilename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + newFilename;
            InputStream streamIn = context.getAssets().open(oldFilename);
            FileOutputStream fileOutputStream = new FileOutputStream(newFilename);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = streamIn.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            streamIn.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }

    public void removeInternalFile(String directory, String filename) {
        if (directory.equalsIgnoreCase("CACHE")) {
            filename = getPathInternalCache() + File.separator + filename;
        } else if (directory.equalsIgnoreCase("FILE")) {
            filename = getPathInternalFiles() + File.separator + filename;
        }
        File file = new File(filename);
        if (file.exists() && file.delete()) {
            Log.e("FILE", "删除文件 " + filename);
        }
    }


    /**
     * 读取扩展内存文件
     * storage/emulated/0
     */
    public String readExternalStorage(String filename) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename;
            FileInputStream inputStream = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                stringBuilder.append(new String(buffer, 0, len));
            }
            inputStream.close();
        }
        return stringBuilder.toString();
    }


    public void writeExternalStorage(String filename, String content) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename;
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(content.getBytes());
            outputStream.close();
        }
        else {
            Toast.makeText(context, "外部存储不可用", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<byte[]> readZip(String filename) throws IOException {
         class zipStreamObject {
            final String zipStreamName;
            final InputStream zipStream;

            public zipStreamObject(String zipStreamName, InputStream zipStream) {
                this.zipStreamName = zipStreamName;
                this.zipStream = zipStream;
            }

            public String getZipStreamName() {
                return zipStreamName;
            }

            public InputStream getZipStream() {
                return zipStream;
            }
        }


        ZipEntry zipEntry;
        String zipEntryName;
        ZipFile zipFile = new ZipFile(filename);
        ArrayList<byte[]> streamArrayList = new ArrayList<>();
        ArrayList<zipStreamObject> streamArrayListMap = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(filename);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.isDirectory()) {
                continue;
            }
            else if (imageFilter((zipEntryName = zipEntry.getName()))) {
                streamArrayListMap.add(new zipStreamObject(zipEntryName, zipFile.getInputStream(zipEntry)));
            }
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
        inputStream.close();

        streamArrayListMap.sort(new Comparator<zipStreamObject>() {
            @Override
            public int compare(zipStreamObject o1, zipStreamObject o2) {
                return o1.getZipStreamName().hashCode() - o2.getZipStreamName().hashCode();
            }
        });
        for (int i = 0; i < streamArrayListMap.size(); i++) {
            Log.e("READ", streamArrayListMap.get(i).getZipStreamName());
            streamArrayList.add(inputSteam2ByteArray(streamArrayListMap.get(i).getZipStream()));
        }
        zipFile.close();
        return streamArrayList;
        }

    public byte[] readZipFirst(String filename) throws IOException {

        ZipEntry zipEntry;
        String zipEntryName;
        ZipFile zipFile = new ZipFile(filename);
        byte[] byteArray;
        ArrayList<String> streamArrayListName = new ArrayList<>();
        FileInputStream inputStream = new FileInputStream(filename);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (zipEntry.isDirectory()) {
                continue;
            }
            else if (imageFilter((zipEntryName = zipEntry.getName()))) {
                streamArrayListName.add(zipEntryName);
            }
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
        inputStream.close();
        streamArrayListName.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.hashCode() - o2.hashCode();
            }
        });

        if (!streamArrayListName.isEmpty()) {
            byteArray = inputSteam2ByteArray(zipFile.getInputStream(zipFile.getEntry(streamArrayListName.get(0))));
        }
        else byteArray = new byte[]{};
        zipFile.close();
        return byteArray;
    }

    public byte[] inputSteam2ByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    private boolean imageFilter(String filename) {
        for (String type: IMAGE_TYPES) {
            if (filename.toUpperCase().endsWith("." + type)) {
                return true;
            }
        }
        return false;
    }

    public void saveImageCancel( String filename, byte[] byteArray) throws IOException, NoSuchAlgorithmException {
        File saveDirPath = new File(getPathPreviewImageDirName());
        if (!saveDirPath.exists()) {
            if (!saveDirPath.mkdirs()) {
                return;
            }
        }
        filename = saveDirPath.getAbsolutePath() + File.separator + Md5(filename) + IMAGE_FORMAT;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        FileOutputStream outputStream = new FileOutputStream(filename);
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, outputStream);
        outputStream.write(byteArray);
        outputStream.close();
        bitmap.recycle();
    }

    public ArrayList<String> FileFilter(ArrayList<String> filenames, String fileType) {
        for (String filename: filenames) {
            File file = new File(filename);
            if (file.isFile() && file.getName().endsWith("." + fileType)) {
                fileList.add(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                if (subFiles != null) {
                    ArrayList<String> subFilenames = new ArrayList<>();
                    for (File subFile: subFiles) {
                        subFilenames.add(subFile.getAbsolutePath());
                    }
                    FileFilter(subFilenames, fileType);
                }
            }
        }
        return fileList;
    }

    public void duplicate(ArrayList<?> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < arrayList.size(); j++) {
                if (i != j && arrayList.get(i).equals(arrayList.get(j))) {
                    (arrayList).remove(j);
                }
            }
        }
    }

    public String Md5(String value) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(value.getBytes());
        return (String) new BigInteger(1, messageDigest.digest()).toString(16);
    }
}