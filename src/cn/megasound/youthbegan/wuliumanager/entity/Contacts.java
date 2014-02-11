package cn.megasound.youthbegan.wuliumanager.entity;

import java.io.Serializable;

/**
 * 联系人
 * @author Administrator
 *
 */
public class Contacts implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5160718929785530745L;
	public int id;//自己的id
	public PresentGoods goods;//货物
	public int messagestate;//发送信息失败:0     成功:1
	public int goodsstate;//货物状态：未取   0
	public String remark;//备注
	
	@Override
	public String toString() {
		return "Contacts [id=" + id + ", goods=" + goods + ", message="
				+ messagestate + ", goodsstate=" + goodsstate + ", remark=" + remark
				+ "]";
	}
	
}
