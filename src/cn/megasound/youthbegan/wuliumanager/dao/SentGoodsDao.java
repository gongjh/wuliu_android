package cn.megasound.youthbegan.wuliumanager.dao;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.entity.SentGoods;


/**
 * 已派货表 dao
 * @author Administrator
 *
 */
public interface SentGoodsDao {
	
	/**
	 * 入库时间为当天的该表的数量
	 * @return
	 */
	public int getCount();
	
	
	/**
	 * 查询某个快递公司，某一段时间内已派送成功的货件数
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 */
	public int getCount(int companyId, String firstDate, String endDate);
	
	/**
	 * 获取已派货表  某条件的列表
	 * @param page
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 * @return  
	 */
	public List<SentGoods> getSentGoodsList(int page,int companyId, String firstDate, String endDate);
	
	/**
	 * 删除已派货件
	 * @param id
	 * @return
	 */
	public int deleteSentGoods(int id);
	
}
