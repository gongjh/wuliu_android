package cn.megasound.youthbegan.wuliumanager.dao;

import java.util.List;

import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;

/**
 * 及时派货表  dao
 */
public interface PresentGoodsDao {
	
	/**
	 * 新增入库
	 * @param orderNum
	 * @param phoneNum
	 * @param companyId
	 * @param shelfNum
	 * @param type
	 * @param city
	 * @param name
	 * return  int  返回id
	 */
	public int addGoods(String orderNum,String phoneNum,
			int companyId,String shelfNum,int type,String city,String name);
	
	/**
	 * 修改信息
	 * @param id
	 * @param orderNum
	 * @param phoneNum
	 * @param companyId
	 * @param shelfNum
	 * @param type
	 * @param city
	 * @param name
	 */
	public int updateGoods(int id,String orderNum,String phoneNum,
			int companyId,String shelfNum,int type,String city,String name);
	
	/**
	 * 查询当前及时派货的列表
	 * @return
	 */
	public List<PresentGoods> getPresentGoods(int page);
	
	/**
	 * 按条件查询
	 * @param phoneNum 
	 * @return
	 */
	public List<PresentGoods> getPresentGoodsByCondition(int page,String phoneNum);
	
	/**
	 * 获取及时派货表  某条件的列表
	 * @param page
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 * @return
	 */
	public List<PresentGoods> getPresentGoodsList(int page,int companyId, String firstDate, String endDate,int type);
	
	/**
	 * 人来取货
	 * 注：将该条数据迁移至已派货表中，然后删除与联系人关联的那条数据，最后删除本条数据
	 * @param orderNum
	 */
	public int moveToSentGoods(String orderNum);
	
	/**
	 * 快递公司来取货
	 * 注：将该条数据迁移至返还货表中，然后删除与联系人关联的那条数据，最后删除本条数据
	 * @param orderNum
	 */
	public int moveToReturnGoods(String orderNum);
	
	/**
	 * 入库时间为当天的该表的数量
	 * @return
	 */
	public int getCount();
	
	/**
	 * 查询某个快递公司，某一段时间内(以入库时间为准)目前仍在及时派货表的货件数
	 * @param companyId
	 * @param firstDate
	 * @param endDate
	 * @return
	 */
	public int getCount(int companyId, String firstDate, String endDate, int type);
	
	/**
	 * 删除某一条数据
	 * @param id
	 * @return
	 */
	public int deletePresentGoods(int id);
	
	/**
	 * 将遣返货物列表中的订单项回滚到及时派货表中
	 * @param rid
	 * @return
	 */
	public int rollback(String rOrder);
	
}
