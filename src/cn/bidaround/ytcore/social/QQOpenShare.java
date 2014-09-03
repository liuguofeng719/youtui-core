package cn.bidaround.ytcore.social;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;
import cn.bidaround.point.ChannelId;
import cn.bidaround.point.YtLog;
import cn.bidaround.ytcore.ErrorInfo;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 该类实现QQ,QZone分享和回调
 * @author youtui 
 * @since 14/6/19
 */
public class QQOpenShare {
	/**传入的activity*/
	private Activity act;
	/**qq分享类*/
	private Tencent mTencent;
	/**判断是qq分享还是qq空间分享*/
	private String flag;
	/**真实网址*/
	private String realUrl;
	/**短链接*/
	private String shortUrl;
	/**分享监听*/
	private YtShareListener listener;
	/**待分享数据*/
	private ShareData shareData;
	private String appName;
	private Resources res;
	private String packName;

	public QQOpenShare(Activity act, String flag,YtShareListener listener,ShareData shareData) {
		this.act = act;
		this.flag = flag;
		this.listener = listener;
		this.shareData = shareData;
		init(act);
		res = act.getResources();
		packName = act.getPackageName();
	}

	/**
	 * 初始化，如果没有授权则进行登录授权
	 */
	private void init(Activity act) {
		if(shareData!=null&&!shareData.isAppShare){
			realUrl = act.getIntent().getExtras().getString("realUrl");
			shortUrl = act.getIntent().getExtras().getString("shortUrl");
		}
		//获取appName
		ApplicationInfo info = null;
		try {
			info = act.getPackageManager().getApplicationInfo(act.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		appName = (String) act.getPackageManager().getApplicationLabel(info);
		
		if ("QQ".equals(flag)) {
			mTencent = Tencent.createInstance(KeyInfo.qQ_AppId, act);
		} else if ("Qzone".equals(flag)) {
			mTencent = Tencent.createInstance(KeyInfo.qZone_AppId, act);
		}		
	}

	/**
	 * 分享到qq
	 */
	public void shareToQQ() {
		if(shareData!=null){
			Bundle params = new Bundle();
			if(shareData.getShareType()==ShareData.SHARETYPE_IMAGEANDTEXT){
				//图文分享
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);			
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,shareData.getTarget_url());
				// 判断传输的是网络图片还是本地图片
				if (shareData.getImagePath() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareData.getImageUrl());
				} 				
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
				params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);		
			}else if(shareData.getShareType()==ShareData.SHARETYPE_IMAGE){
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
				if (shareData.getImagePath() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareData.getImageUrl());
				} 
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);				
			}else if(shareData.getShareType()==ShareData.SHARETYPE_TEXT){
				//图文分享
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);			
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,shareData.getTarget_url());
				// 判断传输的是网络图片还是本地图片
				if (shareData.getImagePath() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,shareData.getImageUrl());
				} 				
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
				params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0);
			}
			
			
			
			mTencent.shareToQQ(act, params, new MyQQShareUIListener());
		}
	}

	/**
	 * 分享到qq空间
	 */
	public void shareToQzone() {
		if(shareData!=null){
			Bundle params = new Bundle();
			if(shareData.getShareType()==ShareData.SHARETYPE_IMAGEANDTEXT){
				//QQ空间只支持图文分享
				if(shareData.getShareType()==ShareData.SHARETYPE_IMAGEANDTEXT){
					
					params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);

					params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
					params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
					params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.getTarget_url());
					params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);

					ArrayList<String> list = new ArrayList<String>();
					// 判断传输的是网络图片还是本地图片
					if (shareData.getImagePath() != null) {
						list.add(shareData.getImagePath());
					} else if (shareData.getImageUrl() != null) {
						list.add(shareData.getImageUrl());
					}
					params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list);
				}
			}else if(shareData.getShareType()==ShareData.SHARETYPE_IMAGE){
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.getTarget_url()); 
				//YtLog.d("shareToQzone shareData.getTarget_url()", shareData.getTarget_url());
				ArrayList<String> list = new ArrayList<String>();
				// 判断传输的是网络图片还是本地图片
				if (shareData.getImagePath() != null) {
					list.add(shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					list.add(shareData.getImageUrl());
				}
				params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list);
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
			}else if(shareData.getShareType()==ShareData.SHARETYPE_TEXT){
				params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
				params.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.getTitle());
				params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.getText());
				params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.getTarget_url());
				params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
				
				ArrayList<String> list = new ArrayList<String>();
				// 判断传输的是网络图片还是本地图片
				if (shareData.getImagePath() != null) {
					list.add(shareData.getImagePath());
				} else if (shareData.getImageUrl() != null) {
					list.add(shareData.getImageUrl());
				}
				params.putStringArrayList(QQShare.SHARE_TO_QQ_IMAGE_URL, list);
			}
			mTencent.shareToQzone(act, params, new MyQQShareUIListener());
		}
	}

	/**
	 * 分享回调
	 */
	class MyQQShareUIListener implements IUiListener {

		@Override
		public void onCancel() {
			if(listener!=null){
				listener.onCancel();
			}			
			act.finish();
		}

		@Override
		public void onComplete(Object arg0) {
			if ("QQ".equals(flag)) {
				YtShareListener.sharePoint(act, KeyInfo.youTui_AppKey, ChannelId.QQ, realUrl, !shareData.isAppShare, shortUrl);
				if(listener!=null){
					ErrorInfo error = new ErrorInfo();
					JSONObject json = (JSONObject) arg0;
					String errorMessage = json.toString();
					error.setErrorMessage(errorMessage);
					listener.onSuccess(error);
				}			
			} else if ("Qzone".equals(flag)) {
				YtShareListener.sharePoint(act, KeyInfo.youTui_AppKey, ChannelId.QZONE, realUrl, !shareData.isAppShare, shortUrl);
				if(listener!=null){
					ErrorInfo error = new ErrorInfo();
					JSONObject json = (JSONObject) arg0;
					String errorMessage = json.toString();
					error.setErrorMessage(errorMessage);
					listener.onSuccess(error);
				}	
			}
			act.finish();
		}

		@Override
		public void onError(UiError arg0) {
			
			if(listener!=null){
				ErrorInfo error = new ErrorInfo();
				String errorMessage = arg0.errorMessage;
				error.setErrorMessage(errorMessage);
				listener.onError(error);
			}	
			act.finish();
		}

	}

	/**
	 * 授权回调
	 */

	class MyQQAuthUIListener implements IUiListener {

		@Override
		public void onCancel() {
			//Toast.makeText(act, "授权取消", Toast.LENGTH_SHORT).show();
			Toast.makeText(act,res.getString(res.getIdentifier("yt_authcancel", "string", packName)), Toast.LENGTH_SHORT).show();
			act.finish();
		}

		@Override
		public void onComplete(Object object) {
			//Toast.makeText(act, "授权成功", Toast.LENGTH_SHORT).show();
			Toast.makeText(act,res.getString(res.getIdentifier("yt_authsuccess", "string", packName)), Toast.LENGTH_SHORT).show();
			// 将access_token信息写入SharedPreferences中
			SharedPreferences sp = act.getSharedPreferences("tencent_open_access", 0);
			Editor edit = sp.edit();
			JSONObject json = (JSONObject) object;

			try {
				edit.putString("openid", json.getString("openid"));
				edit.putString("access_token", json.getString("access_token"));
				edit.putString("expires_in", System.currentTimeMillis() + Long.parseLong(json.getString("expires_in")) + "");
				edit.commit();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			act.finish();
		}

		@Override
		public void onError(UiError arg0) {
			//Toast.makeText(act, "授权错误", Toast.LENGTH_SHORT).show();
			Toast.makeText(act, res.getString(res.getIdentifier("yt_authfailed", "string", packName)), Toast.LENGTH_SHORT).show();
			act.finish();
		}

	}
}
