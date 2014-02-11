package cn.megasound.youthbegan.wuliumanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	
	public static String dateToString(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

}
