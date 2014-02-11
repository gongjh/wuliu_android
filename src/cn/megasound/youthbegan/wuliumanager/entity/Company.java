package cn.megasound.youthbegan.wuliumanager.entity;

import java.io.Serializable;

/**
 * 快递公司
 * @author Administrator
 *
 */
public class Company implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7159484695336181730L;
	public int id;//公司id
	public String name;//公司名称
	
	@Override
	public String toString() {
		return "Company [id=" + id + ", name=" + name + "]";
	}
	
}
