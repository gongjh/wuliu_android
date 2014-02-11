package cn.megasound.youthbegan.wuliumanager.dao;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.entity.ReturnGoods;

/**
 * 返还货表  dao
 * @author Administrator
 *
 */
public interface ReturnGoodsDao {

	/**
	 * 入库时间为当天的该表的数量
	 * @return
	 */
	public int getCount();
	
	/**
	 * 获取当天退回的货件数
	 * @return
	 */
	public int getCount(int companyId, String firstDate, String endDate);
	
	/**
	 * 获取返还货件   某条件的列表
	 * @param page
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 * @return
	 */
	public List<ReturnGoods> getReturnGoodsList(int page,int companyId, String firstDate, String endDate);
	
	/**
	 * 删除返还货件
	 * @param id
	 * @return
	 */
	public int deleteReturnGoods(int id);
	
}
