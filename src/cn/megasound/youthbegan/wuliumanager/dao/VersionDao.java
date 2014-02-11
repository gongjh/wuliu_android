package cn.megasound.youthbegan.wuliumanager.dao;

import cn.megasound.youthbegan.wuliumanager.entity.Version;

public interface VersionDao {

	/**
	 * 获取服务器端保存的手机版本号
	 */
	public Version getFromServer();
}
