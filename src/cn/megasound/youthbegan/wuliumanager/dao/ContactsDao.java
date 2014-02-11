package cn.megasound.youthbegan.wuliumanager.dao;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.entity.Contacts;

/**
 * 联系人表  dao
 * @author Administrator
 *
 */
public interface ContactsDao {
	
	/**
	 * 新增联系人
	 * @param hid
	 * @param message
	 * @param goodsstate
	 * @param remark
	 * @return
	 */
	public int addContacts(int hid,int message,int goodsstate,String remark);
	
	/**
	 * 修改联系人
	 * @param id
	 * @param hid
	 * @param message
	 * @param goodsstate
	 * @param remark
	 * @return
	 */
	public int updateContacts(int id,int hid,int message,int goodsstate,String remark);
	
	/**
	 * 修改短信状态
	 * @param id
	 * @param message
	 * @return
	 */
	public int updateContacts(int id,int message);
	
	/**
	 * 修改备注
	 * @param id
	 * @param remark
	 * @return
	 */
	public int updateContacts(int id,String remark);
	
	/**
	 * 获取联系人
	 * @param messagestate 
	 * @return
	 */
	public List<Contacts> getContacts(int page, int messagestate);
	
	/**
	 * 通过短信平台发送短信
	 * @param phone
	 * @param message
	 * @return
	 */
	public int sentMessage(String phone,String message);
	
	/**
	 * 删除联系人
	 * @param id
	 * @return
	 */
	public int deleteContacts(int id);
	
}
