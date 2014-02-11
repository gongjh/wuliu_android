package cn.megasound.youthbegan.wuliumanager.entity;

import java.util.Date;

/**
 * 已派货类
 * 
 */
public class SentGoods {
	
	public int id;  //货物id
	public Date inputDate; //入库时间
	public String orderNum; //订单编号
	public String phoneNum; //手机号
	public Company company; //物流公司名
	public String shelfNum; //货架编号
	public int type;    //快递的种类：1:普通、2:同城、3:加急、4:遗留
	
	public Date getDate;   //取货时间
	
	public String city;    //所在城市
	public String name;    //姓名
	@Override
	public String toString() {
		return "SentGoods [id=" + id + ", inputDate=" + inputDate
				+ ", orderNum=" + orderNum + ", phoneNum=" + phoneNum
				+ ", company=" + company + ", shelfNum=" + shelfNum + ", type="
				+ type + ", getDate=" + getDate + ", city=" + city + ", name="
				+ name + "]";
	}
	
	
	
}
