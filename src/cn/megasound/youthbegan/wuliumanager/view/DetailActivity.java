package cn.megasound.youthbegan.wuliumanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;

/**
 * 信息详情页面
 * @author Administrator
 *
 */
public class DetailActivity extends BaseActivity {

	private TextView companyTV,telTV,nameTV,typeTV,orderTV,shelfTV;
	private PresentGoods presentGoods;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.input_detail);
	}

	@Override
	protected void findViewById() {
		companyTV = (TextView)findViewById(R.id.input_detail_company_tv);
		telTV = (TextView)findViewById(R.id.input_detail_tel_tv);
		nameTV = (TextView)findViewById(R.id.input_detail_name_tv);
		typeTV = (TextView)findViewById(R.id.input_detail_type_tv);
		orderTV = (TextView)findViewById(R.id.input_detail_order_tv);
		shelfTV = (TextView)findViewById(R.id.input_detail_shelf_tv);
	}
	
	@Override
	protected void init() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		presentGoods = (PresentGoods) data.getSerializable("presentGoods");
		companyTV.setText(presentGoods.company.name);
		telTV.setText(presentGoods.phoneNum);
		nameTV.setText(presentGoods.name);
		int t = presentGoods.type;
		String type="";
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
		}
		typeTV.setText(type);
		orderTV.setText(presentGoods.orderNum);
		shelfTV.setText(presentGoods.shelfNum);
	}

}
