package cn.megasound.youthbegan.wuliumanager.dao;

/**
 * 用户表  dao
 * @author Administrator
 *
 */
public interface UsersDao {
	
	public int login(String name,String pwd);
	
	public int logout(int uid);
	
	public int checkCode(String orderNum);
	
}
