package cn.megasound.youthbegan.wuliumanager.application;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.entity.Company;
import cn.megasound.youthbegan.wuliumanager.entity.Users;

public class ConstantValue {
	
	public static Users user;
	
	public static String station;
	
	public static String station_detail = "六号楼南侧";
	
	public static List<Company> companys;
	
	public static int lastCompany = 0;
	
	/**
	 * 寄件
	 */
	public static int PRESENT_TOTAL = 0;
	
	/**
	 * 派件
	 */
	public static int PRESENT_CONDITION_TOTAL = 0;
	
	/**
	 * 查询
	 */
	public static int LOOKFOR_SENT_TOTAL = 0;
	
	public static int LOOKFOR_REMAIN_TOTAL = 0;
	
	public static int LOOKFOR_BACK_TOTAL = 0;
	
	/**
	 * 联系
	 */
	public static int CONTACT_TOTAL = 0;
	
	/**
	 * 每次请求的签名
	 */
	public static final String SIGN = "81484a1b9ec006ca45163273df40da99";
	
	/**
	 * 服务器ip     http://115.28.131.7:222/index.php
	 * 本地的ip     http://192.168.1.115/onethink/index.php
	 */
	public static final String HOST_NAME = "http://115.28.131.7:222/index.php";
	
	/**
	 * 登录
	 */
	public static final String Login = "?m=home&c=user&a=login";
	
	/**
	 * 登出 
	 */
	public static final String Logout = "?m=home&c=user&a=logout";
	
	/**
	 * 验证快递单号
	 */
	public static final String Check_Code = "?m=home&c=sendonline&a=checkCode";
	
	public static final String Send_Message = "http://60.170.244.50:8087/api/send";
	/**
	 * 添加货件
	 */
	public static final String Add_PresentGoods = "?m=home&c=sendonline&a=index&method=addSendonline";
	
	/**
	 * 修改货件
	 */
	public static final String Update_PresentGoods = "?m=home&c=sendonline&a=index&method=updateSendonline";
	
	/**
	 * 获取货件列表
	 */
	public static final String Get_PresentGoods_List = "?m=home&c=sendonline&a=index&method=getSendonlineList";
	
	/**
	 * 查询详情获取remain列表
	 */                                                           
	public static final String Get_PresentGoods_Condition_List = "?m=home&c=sendonline&a=index&method=getPresentGoodsList";
	
	/**
	 * 移至已派货表
	 */
	public static final String Move_To_SentGoods = "?m=home&c=sendonline&a=index&method=moveToSendover";
	
	/**
	 * 移至返回表
	 */
	public static final String Move_To_ReturnGoods = "?m=home&c=sendonline&a=index&method=moveToReturnGoods";
	
	/**
	 * 获取当天的及时派货数
	 */
	public static final String Get_Today_Total = "?m=home&c=sendonline&a=index&method=getTodayCount";
	
	/**
	 * 获取某个公司某段时间的及时派货数
	 */
	public static final String Get_Condition_Total = "?m=home&c=sendonline&a=index&method=getCount";
	
	/**
	 * 将超时未取的货件修改状态
	 */
	public static final String Update_Over_Time = "?m=home&c=sendonline&a=index&method=updateGoodsStatus";
	
	/**
	 * 将圆通 类型  超时未取的货件修改状态
	 */
	public static final String Update_Yuantong_Type = "?m=home&c=sendonline&a=index&method=updateYuantong";
	
	/**
	 * 将其他  类型  超时未取的货件修改状态
	 */
	public static final String Update_Others_Type = "?m=home&c=sendonline&a=index&method=updateOthers";
	
	public static final String Roll_Back = "?m=home&c=ReturnGoods&a=BacktoPresentGoods";
	
	public static final String Get_By_Order = "";
	
	/**
	 * 删除及时派货件
	 */
	public static final String Delete_PresentGoods = "?m=home&c=sendonline&a=index&method=deletePresentGoods";
	
	public static final String Delete_SentGoods = "?m=home&c=sendover&a=index&method=deleteSentGoods";
	
	public static final String Delete_ReturnGoods = "?m=home&c=returnGoods&a=index&method=deleteReturnGoods";
	
	public static final String Delete_Contacts = "?m=home&c=sms&a=index&method=deleteContacts";
	
	/**
	 * 获取已派货表今日的数量
	 */
	public static final String Get_SentGoods_Today_Count = "?m=home&c=sendover&a=index&method=getTodayCount";
	
	/**
	 * 获取已派货表   某条件的数量
	 */
	public static final String Get_SentGoods_Count_Condition = "?m=home&c=sendover&a=index&method=getCount";
	
	/**
	 * 查询详情获取sent列表
	 */
	public static final String Get_SentGoods_Condition_List = "?m=home&c=sendover&a=index&method=getSentGoodsList";
	
	/**
	 * 获取返还表 今日的数量
	 */
	public static final String Get_ReturnGoods_Today_Count = "?m=home&c=returnGoods&a=index&method=getTodayCount";
	
	/**
	 * 获取返还表  某条件的数量
	 */
	public static final String Get_ReturnGoods_Count_Condition = "?m=home&c=returnGoods&a=index&method=getCount";
	
	/**
	 * 查询详情获取遣返列表
	 */
	public static final String Get_ReturnGoods_Condition_List = "?m=home&c=returnGoods&a=index&method=getReturnGoodsList";
	
	/**
	 * 添加联系人
	 */
	public static final String Add_Contacts = "?m=home&c=sms&a=index&method=addContacts";
	
	/**
	 * 修改联系人的短信发送状态
	 */
	public static final String Update_Contacts_Sms = "?m=home&c=sms&a=index&method=updateSmsStatus";
	
	/**
	 * 修改联系人的备注
	 */
	public static final String Update_Contacts_Remark = "?m=home&c=sms&a=index&method=updateRemark";
	
	/**
	 * 获取联系人列表
	 */
	public static final String Get_Contacts_List = "?m=home&c=sms&a=index&method=getSmsList";
	
	/**
	 * 获取快递公司列表
	 */
	public static final String Get_Company_List = "?m=home&c=common&a=index&method=getCompany";
	
	/**
	 * 获取版本号
	 */
	public static final String Get_Version = "?m=home&c=common&a=getVersion";
	
	/**
	 * 自定义ACTION常数，作为广播的Intent Filter识别常数 
	 */
	public static final String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
	
	public static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
	
}
