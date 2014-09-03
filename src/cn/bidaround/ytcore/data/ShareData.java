package cn.bidaround.ytcore.data;

import java.io.Serializable;
import cn.bidaround.ytcore.YtCore;


/**
 * 该类为分享数据类，有些平台有分享限制 友推sdk会过滤掉无法分享的内容，只分享能被平台接受的内容
 * 如果需要分享图片，需要设置imageUrl和imagePath中的一项 如果imageUrl和imagePath都被设置，则优先使用imagePath
 * @author youtui 
 * @since 14/6/19
 */

public class ShareData implements Serializable{
	
	public static ShareData instance;
	/**如果为app分享设置为true，如果为content分享则设置为false
	 * app分享的内容由开发者预先保留在友推服务器上
	 * content分享的内容由开发者给ShareData实例的各个字段赋值
	 **/
	public boolean isAppShare = false;
	/**分享的文字*/
	//private String text = "加载分享内容失败";
	private String text = YtCore.res.getString(YtCore.res.getIdentifier("yt_getsharecontent_fail", "string", YtCore.packName));
	/**分享的图片的本地路径*/
	private String imagePath;
	/**分享的描述*/
	//private String description = "描述";
	private String description = YtCore.res.getString(YtCore.res.getIdentifier("yt_description", "string", YtCore.packName));
	/**分享的标题*/
	//private String title = "分享";
	private String title = YtCore.res.getString(YtCore.res.getIdentifier("yt_share", "string", YtCore.packName));
	/**分享的图片的网络url*/
	private String imageUrl;
	/**分享的网页链接*/
	private String target_url;
	/**是否有活动正在进行*/
	private boolean isInProgress = false;
	/**图文分享，该分享类型为默认分享类型，如果开发者未设置，则使用默认分享类型*/
	public static final int SHARETYPE_IMAGEANDTEXT = 0;
	/**纯图分享,qq空间不支持纯图分享*/
	public static final int SHARETYPE_IMAGE = 1;
	/**纯文字分享,qq和qq空间不支持纯文字分享*/
	public static final int SHARETYPE_TEXT = 2;
	/**用来判断分享的类型*/
	private int shareType = SHARETYPE_IMAGEANDTEXT;
	
	public ShareData(){
		instance = this;
	}
	
	public static ShareData getInstance(){
		return instance;
	}
	
	public void setIsInProgress(boolean isInProgress){
		this.isInProgress = isInProgress;
	}
	/**查看是否有活动正在进行中*/
	public boolean getIsInProgress(){
		return isInProgress;
	}
	public void setIsAppShare(boolean isAppShare) {
		this.isAppShare = isAppShare;
	}

	public String getTarget_url() {
		return target_url;
	}
	/**
	 * 网页链接地址
	 */
	public void setTarget_url(String target_url) {
		this.target_url = target_url;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 设置分享内容的描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getText() {
		return text;
	}

	/**
	 * 设置待分享的文字内容
	 */
	public void setText(String text) {
		this.text = text;
	}

	public String getImagePath() {
		return imagePath;
	}

	/**
	 * 待分享的本地图片路径
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * 待分享的内容标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * 设置分享的网络图片url
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**获取分享的类型*/
	public int getShareType() {
		return shareType;
	}
	/**设置分享的类型*/
	public void setShareType(int shareType) {
		this.shareType = shareType;
	}
	
	
}
