package cn.bidaround.ytcore.social;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import cn.bidaround.point.YoutuiConstants;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.activity.ShareActivity;
import cn.bidaround.ytcore.data.KeyInfo;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.util.AccessTokenKeeper;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;

/**
 * 新浪微博分享操作以及分享回调
 * 
 * @author youtui
 * @since 14/4/25
 */
public class SinaShare {
	/** 新浪微博分享接口 */
	private IWeiboShareAPI iWeiboShareAPI;
	/** 传入的activity */
	private Activity activity;
	/** 新浪微博授权AccessToken */
	private Oauth2AccessToken oauth2AccessToken;
	/** 分享微博授权接口 */
	private WeiboAuth mWeiboAuth;
	/** 新浪微博sso类 */
	private SsoHandler mSsoHandler;
	/** 待分享数据 */
	private ShareData shareData;

	public SinaShare(Activity activity, ShareData shareData) {
		this.activity = activity;
		iWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, KeyInfo.sinaWeibo_AppKey);
		this.shareData = shareData;
	}

	/**
	 * 发送共享到新浪微博
	 */
	public void shareToSina() {
		// 新浪微博在10351以上版本支持多条微博发送
		if (iWeiboShareAPI.getWeiboAppSupportAPI() >= 10351) {
			if(shareData.getShareType()==ShareData.SHARETYPE_IMAGEANDTEXT){
				//如果是图文类型
				sendMultiMessage();
			}else if(shareData.getShareType()==ShareData.SHARETYPE_IMAGE){
				//如果是纯图分享
				sendSingleMessage();
			}else if(shareData.getShareType()==ShareData.SHARETYPE_TEXT){
				//纯文字分享
				sendTextMessage();
			}else if(shareData.getShareType()==ShareData.SHARETYPE_MUSIC){
				//音乐分享
				sendMusicMessage();
			}else if(shareData.getShareType()==ShareData.SHARETYPE_VIDEO){
				sendVideoMessage();
			}
		} else {
			sendSingleMessage();
		}
	}

	/**
	 * 新浪微博sso授权
	 */
	public void sinaAuth(final String realUrl, final String shortUrl) {
		mWeiboAuth = new WeiboAuth(activity, KeyInfo.sinaWeibo_AppKey, KeyInfo.sinaWeibo_RedirectUrl, YoutuiConstants.SINA_WEIBO_SCOPE);
		mSsoHandler = new SsoHandler(activity, mWeiboAuth);
		mSsoHandler.authorize(new WeiboAuthListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				//Toast.makeText(activity, "授权错误", Toast.LENGTH_SHORT).show();
				//Log.e("授权错误", arg0.getMessage());
			}

			@Override
			public void onComplete(Bundle arg0) {
				//Toast.makeText(activity, "授权成功", Toast.LENGTH_SHORT).show();
				//Log.e("授权成功", arg0.toString());
				oauth2AccessToken = Oauth2AccessToken.parseAccessToken(arg0);
				if (oauth2AccessToken.isSessionValid()) {
					AccessTokenKeeper.writeAccessToken(activity, oauth2AccessToken);
				}
				Intent it = new Intent(activity, ShareActivity.class);
				it.putExtra("platform", YtPlatform.PLATFORM_SINAWEIBO);
				it.putExtra("sinaWeiboIsNoKeyShare", true);
				it.putExtra("realUrl", realUrl);
				it.putExtra("shortUrl", shortUrl);
				activity.startActivity(it);
			}

			@Override
			public void onCancel() {
				//Toast.makeText(activity, "授权取消", Toast.LENGTH_SHORT).show();
				//Log.e("授权取消", "取消");
			}
		});
	}

	/**
	 * 采用网页形式对新浪微博进行授权
	 */
	public void sinaWebAuth(final String realUrl, final String shortUrl) {
		mWeiboAuth = new WeiboAuth(activity, KeyInfo.sinaWeibo_AppKey, KeyInfo.sinaWeibo_RedirectUrl, YoutuiConstants.SINA_WEIBO_SCOPE);
		mWeiboAuth.anthorize(new WeiboAuthListener() {

			@Override
			public void onWeiboException(WeiboException arg0) {
				//Toast.makeText(activity, "授权错误", Toast.LENGTH_SHORT).show();
				//Log.e("授权错误", arg0.getMessage());
			}

			@Override
			public void onComplete(Bundle arg0) {
				//Toast.makeText(activity, "授权成功", Toast.LENGTH_SHORT).show();
				//Log.e("授权成功", arg0.toString());
				oauth2AccessToken = Oauth2AccessToken.parseAccessToken(arg0);
				if (oauth2AccessToken.isSessionValid()) {
					AccessTokenKeeper.writeAccessToken(activity, oauth2AccessToken);
				}
				Intent it = new Intent(activity, ShareActivity.class);
				it.putExtra("platform", YtPlatform.PLATFORM_SINAWEIBO);
				it.putExtra("sinaWeiboIsNoKeyShare", true);
				it.putExtra("realUrl", realUrl);
				it.putExtra("shortUrl", shortUrl);
				activity.startActivity(it);
			}

			@Override
			public void onCancel() {
				//Toast.makeText(activity, "授权取消", Toast.LENGTH_SHORT).show();
				//Log.e("授权取消", "取消");
			}
		});
	}

	/**
	 * sso回调需要
	 */

	public void sinaResult(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面 < 10351 时，只支持分享单条消息，该方法发送一条网页消息
	 */
	private boolean sendSingleMessage() {

		// 1. 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		weiboMessage.mediaObject = getImageObj(shareData);
		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		return iWeiboShareAPI.sendRequest(request);
	}
	
	/**
	 * 分享纯文字
	 * @return
	 */
	private boolean sendTextMessage() {
		// 1. 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		weiboMessage.mediaObject = getTextObject(shareData);
		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		return iWeiboShareAPI.sendRequest(request);
	}
	
	/**
	 * 第三方应用发送请求消息到微博，唤起微博分享界面 > 10351 时，支持分享多条消息，该方法发送一条网页消息和一条图片消息
	 */

	private void sendMultiMessage() {
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		weiboMessage.imageObject = getImageObj(shareData);
		weiboMessage.textObject = getTextObject(shareData);
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		iWeiboShareAPI.sendRequest(request);
	}
	
	private boolean sendMusicMessage(){
		// 1. 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		weiboMessage.mediaObject = getMusicObj(shareData);
		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		return iWeiboShareAPI.sendRequest(request);
	}
	
	
	private boolean sendVideoMessage(){
		// 1. 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		weiboMessage.mediaObject = getVideoObj(shareData);
		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		return iWeiboShareAPI.sendRequest(request);
	}
	
	

	/**
	 * 获得要分享的文字信息
	 * 
	 * @return TextObject
	 */
	private TextObject getTextObject(ShareData shareData) {

		TextObject textObject = new TextObject();
		textObject.title = shareData.getTitle();
		textObject.description = shareData.getDescription();
		String text = shareData.getText();
		// 如果文字太长，截取部分，不然微博无法发送
		if (text.length() > 110) {
			text = text.substring(0, 109);
			text += "...";
		}
		// YtLog.i("shareData.getTarget_url()", shareData.getTarget_url());
		if(shareData.getTarget_url()!=null&&!"".equals(shareData.getTarget_url())&&!"null".equals(shareData.getTarget_url())){
			//YtLog.e("getTarget_url not null", shareData.getTarget_url());
			textObject.text = text + shareData.getTarget_url();
			textObject.actionUrl = shareData.getTarget_url();
		}else{
			//YtLog.e("getTarget_url  null", shareData.getTarget_url());
			textObject.text = text;
		}
		return textObject;
	}

	/**
	 * 获得要分享的图片信息
	 * @return ImageObject
	 */
	private ImageObject getImageObj(ShareData shareData) {
		ImageObject image = new ImageObject();
		Bitmap bitmap = null;
		if (shareData == null) {
		} else if (shareData.getImagePath() != null) {
			bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
		} else if (shareData.getImageUrl() != null) {
			try {
				bitmap = BitmapFactory.decodeStream(new URL(shareData.getImageUrl()).openStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(activity.getResources(), YtCore.res.getIdentifier("loadfail", "drawable", YtCore.packName));
		}
		image.setImageObject(bitmap);
		image.actionUrl = shareData.getTarget_url();
		image.description = shareData.getText();
		return image;
	}
	/**
	 * 获得要分享的音乐
	 * @param shareData
	 * @return
	 */
	private MusicObject getMusicObj(ShareData shareData){
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = shareData.getTitle();
        musicObject.description = shareData.getDescription();
        
        // 设置 Bitmap 类型的图片到视频对象里
        Bitmap bitmap = null;
        Bitmap bmpThum = null;
		if (shareData.getImagePath() != null) {
			bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
		}
		// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
		if (bitmap != null) {
			bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
		} else {
			bmpThum = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(activity.getResources(), YtCore.res.getIdentifier("yt_loadfail", "drawable", YtCore.packName)), 150, 150, true);
		}
        
        musicObject.setThumbImage(bmpThum);
        musicObject.actionUrl = shareData.getMusicUrl();
        musicObject.dataUrl = shareData.getTarget_url();
        musicObject.dataHdUrl = shareData.getTarget_url();
        musicObject.duration = 10;
        musicObject.defaultText = shareData.getText();
		
		return musicObject;
	}
	/**
	 * 获得要分享的视频
	 * @param shareData
	 * @return
	 */
	private VideoObject getVideoObj(ShareData shareData){
        // 创建媒体消息
        VideoObject videoObject = new VideoObject();
        videoObject.identify = Utility.generateGUID();
        videoObject.title = shareData.getTitle();
        videoObject.description = shareData.getText();
        
        // 设置 Bitmap 类型的图片到视频对象里
        Bitmap bitmap = null;
        Bitmap bmpThum = null;
		if (shareData.getImagePath() != null) {
			bitmap = BitmapFactory.decodeFile(shareData.getImagePath());
		}
		// bitmap为空时微信分享会没有响应，所以要设置一个默认图片让用户知道
		if (bitmap != null) {
			bmpThum = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
		} else {
			bmpThum = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(activity.getResources(), YtCore.res.getIdentifier("yt_loadfail", "drawable", YtCore.packName)), 150, 150, true);
		}
        
        videoObject.setThumbImage(bmpThum);
        videoObject.actionUrl = shareData.getVideoUrl();
        videoObject.dataUrl = shareData.getTarget_url();
        videoObject.dataHdUrl = shareData.getTarget_url();
        videoObject.duration = 10;
        videoObject.defaultText = shareData.getText();
		
		return videoObject;
	}
	
}
