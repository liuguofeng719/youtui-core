package cn.bidaround.ytcore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.bidaround.point.YtLog;
import cn.bidaround.ytcore.YtCore;

/**
 * 下载图片
 * @author youtui
 * @since 14/6/19
 */
public class DownloadImage {
	private static final String TAG = "at class DownloadImage:";

	/**
	 * 加载系统本地图片
	 */
	@SuppressWarnings("unused")
	public static Bitmap loadImage(final String url, final String filename) {
		try {
			FileInputStream fis = new FileInputStream(url + filename);
			if (fis != null) {
				return BitmapFactory.decodeStream(fis);
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 下载文件功能
	 * 
	 * @throws IOException
	 */
	public static void down_file(String url, String path, String filename) throws IOException {
		//YtLog.i(TAG, "start down shared image");
		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		int fileSize = conn.getContentLength();// 根据响应获取文件大小
		if (fileSize <= 0) {
			//YtLog.e(TAG, "无法获知文件大小");
			//throw new RuntimeException("无法获知文件大小 ");
			throw new RuntimeException(YtCore.res.getString(YtCore.res.getIdentifier("yt_unknownfilesize", "string", YtCore.packName)));
		}

		if (is == null) {
			YtLog.e(TAG, "stream is null");
			throw new RuntimeException("stream is null");
		}
		FileUtils util = new FileUtils();
		util.creatSDDir(path);
		File file = util.creatSDFile(path + filename);// 保存的文件名
		//YtLog.e(TAG, "creatSDFile");
		@SuppressWarnings("resource")
		FileOutputStream fos = new FileOutputStream(file);
		
		//YtLog.e(TAG, "FileOutputStream");
		// 把数据存入路径+文件名
		byte buf[] = new byte[1024];
		do {
			// 循环读取
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
		} while (true);
		is.close();
		//YtLog.i(TAG, "down shared image complete");
	}

}
