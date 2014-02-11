package cn.megasound.youthbegan.wuliumanager.dao.test;

import junit.framework.Assert;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import android.test.AndroidTestCase;
import android.util.Log;

public class PresentGoodsTest extends AndroidTestCase {
	private static final String TAG = "PresentTest" ;
	public void testAddGoods(){
		PresentGoodsDao pDao = new PresentGoodsDaoImpl();
		int hid = pDao.addGoods("12312344", "13512345678", 1, "010401", 1, "", "测试者");
		Assert.assertEquals(1, hid);
		Log.i(TAG, "hid="+hid);
	}
}
