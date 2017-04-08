package com.zfg.queryexpress.chat.parse;

import android.content.Context;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.zfg.queryexpress.chat.base.DemoHelper;
import com.zfg.queryexpress.chat.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class UserProfileManager {

	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;

	/**
	 * HuanXin sync contact nick and avatar listener
	 */
	private List<DemoHelper.DataSyncListener> syncContactInfosListeners;

	private boolean isSyncingContactInfosWithServer = false;

	private EaseUser currentUser;

	public UserProfileManager() {
	}

	public synchronized boolean init(Context context) {
		if (sdkInited) {
			return true;
		}
		syncContactInfosListeners = new ArrayList<DemoHelper.DataSyncListener>();
		sdkInited = true;
		return true;
	}

	public void addSyncContactInfoListener(DemoHelper.DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.add(listener);
		}
	}

	public void removeSyncContactInfoListener(DemoHelper.DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.remove(listener);
		}
	}

	public void asyncFetchContactInfosFromServer(List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
		if (isSyncingContactInfosWithServer) {
			return;
		}
		isSyncingContactInfosWithServer = true;


	}

	public void notifyContactInfosSyncListener(boolean success) {
		for (DemoHelper.DataSyncListener listener : syncContactInfosListeners) {
			listener.onSyncComplete(success);
		}
	}

	public boolean isSyncingContactInfoWithServer() {
		return isSyncingContactInfosWithServer;
	}

	public synchronized void reset() {
		isSyncingContactInfosWithServer = false;
		currentUser = null;
		PreferenceManager.getInstance().removeCurrentUserInfo();
	}

	public synchronized EaseUser getCurrentUserInfo() {
		if (currentUser == null) {
			String username = EMClient.getInstance().getCurrentUser();
			currentUser = new EaseUser(username);
			String nick = getCurrentUserNick();
			currentUser.setNick((nick != null) ? nick : username);
			currentUser.setAvatar(getCurrentUserAvatar());
		}
		return currentUser;
	}

	public boolean updateCurrentUserNickName(final String nickname) {

		return true;
	}

	public String uploadUserAvatar(byte[] data) {
		String avatarUrl="";
		if (avatarUrl != null) {
			setCurrentUserAvatar(avatarUrl);
		}
		return avatarUrl;
	}

	public void asyncGetCurrentUserInfo() {

	}
	public void asyncGetUserInfo(final String username,final EMValueCallBack<EaseUser> callback){
	}
	private void setCurrentUserNick(String nickname) {
		getCurrentUserInfo().setNick(nickname);
		PreferenceManager.getInstance().setCurrentUserNick(nickname);
	}

	private void setCurrentUserAvatar(String avatar) {
		getCurrentUserInfo().setAvatar(avatar);
		PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
	}

	private String getCurrentUserNick() {
		return PreferenceManager.getInstance().getCurrentUserNick();
	}

	private String getCurrentUserAvatar() {
		return PreferenceManager.getInstance().getCurrentUserAvatar();
	}

}
