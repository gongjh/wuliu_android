package cn.megasound.youthbegan.wuliumanager.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 及时派货类
 * 
 */
public class PresentGoods implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2980827126516812867L;
	public int id;  //货物id
	public Date inputDate; //入库时间
	public String orderNum; //订单编号
	public String phoneNum; //手机号
	public Company company; //物流类对象
	public String shelfNum; //货架编号
	public int type;    //快递的种类：1:普通、2:同城、3:加急、4:遗留
	public String city;    //所在城市
	public String name;    //姓名
	
	@Override
	public String toString() {
		return "PresentGoods [id=" + id + ", inputDate=" + inputDate
				+ ", orderNum=" + orderNum + ", phoneNum=" + phoneNum
				+ ", Company=" + company + ", shelfNum=" + shelfNum
				+ ", type=" + type + ", city=" + city + ", name=" + name + "]";
	}

}
