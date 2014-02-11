package cn.megasound.youthbegan.wuliumanager.view;

import cn.megasound.youthbegan.wuliumanager.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LookforInfoActivity extends BaseActivity {

	private TextView companyTV,telTV,nameTV,typeTV,orderTV,shelfTV;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.lookfor_detail_info);
	}

	@Override
	protected void findViewById() {
		companyTV = (TextView)findViewById(R.id.lookfor_detail_info_company_tv);
		telTV = (TextView)findViewById(R.id.lookfor_detail_info_tel_tv);
		nameTV = (TextView)findViewById(R.id.lookfor_detail_info_name_tv);
		typeTV = (TextView)findViewById(R.id.lookfor_detail_info_type_tv);
		orderTV = (TextView)findViewById(R.id.lookfor_detail_info_order_tv);
		shelfTV = (TextView)findViewById(R.id.lookfor_detail_info_shelf_tv);
	}
	
	@Override
	protected void init() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		companyTV.setText(data.getString("company"));
		telTV.setText(data.getString("tel"));
		nameTV.setText(data.getString("name"));
		String type = "";
		int t = data.getInt("type");
		switch(t){
			case 1:
				type = "普通件";
				break;
			case 2:
				type = "同城件";
				break;
			case 3:
				type = "加急件";
				break;
			case 4:
				type = "滞留件";
				break;
			default:
				break;
		}
		typeTV.setText(type);
		orderTV.setText(data.getString("order"));
		shelfTV.setText(data.getString("shelf"));
	}

}
