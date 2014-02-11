package cn.megasound.youthbegan.wuliumanager.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.application.ConstantValue;
import cn.megasound.youthbegan.wuliumanager.entity.Company;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.Toast;

public class LookforActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "LookforActivity";
	private static final int FIRST = 1;
	private static final int END = 2;
	
	private Button companyBT,firstBT,endBT,okBT;
	private ListAdapter cAdapter;
	private int y,m,d;
	private int companyId=0;
	private String companyStr,firstStr,endStr;
	
	private List<String> cList ;
	private List<Company> companyList ;
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.lookfor_home);
	}
	
	@Override
	protected void findViewById() {
		companyBT = (Button)findViewById(R.id.lookfor_home_company_bt);
		firstBT = (Button)findViewById(R.id.lookfor_home_first_bt);
		endBT = (Button)findViewById(R.id.lookfor_home_end_bt);
		okBT = (Button)findViewById(R.id.lookfor_home_ok_bt);
	}
	
	@Override
	protected void init() {
		
		cList = new ArrayList<String>();
		companyList = ConstantValue.companys;
		for(Company c:companyList){
			cList.add(c.name);
		}
		Calendar c = Calendar.getInstance();
		y = c.get(Calendar.YEAR);
		m = c.get(Calendar.MONTH);
		d = c.get(Calendar.DAY_OF_MONTH);
		//设置默认值
		firstBT.setBackgroundResource(R.drawable.lookfor_home_company_tv_up);
		firstStr = y+"-"+(m+1)+"-"+d;
		firstBT.setText(firstStr);
		endBT.setBackgroundResource(R.drawable.lookfor_home_company_tv_up);
		endStr = y+"-"+(m+1)+"-"+d;
		endBT.setText(endStr);
	}

	@Override
	protected void setListener() {
		companyBT.setOnClickListener(this);
		firstBT.setOnClickListener(this);
		endBT.setOnClickListener(this);
		okBT.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.lookfor_home_company_bt:
				cAdapter=new ArrayAdapter<String>
					(this, android.R.layout.simple_list_item_1, cList);
				new AlertDialog.Builder(LookforActivity.this)
					.setTitle("请选择快递公司")
					.setAdapter(cAdapter,
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.i(TAG, "choose:"+which);
								companyBT.setBackgroundResource(R.drawable.lookfor_home_company_tv_up);
								companyStr = cAdapter.getItem(which).toString();
								companyBT.setText(companyStr);
							}
						}).show();
				break;
			case R.id.lookfor_home_first_bt:
				showDatePicker(FIRST);
				break;
			case R.id.lookfor_home_end_bt:
				showDatePicker(END);
				break;
			case R.id.lookfor_home_ok_bt:
				if(TextUtils.isEmpty(companyStr) ){
					Toast.makeText(LookforActivity.this, "请选择物流公司。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(TextUtils.isEmpty(firstStr)){
					Toast.makeText(LookforActivity.this, "请选择起始时间。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(TextUtils.isEmpty(endStr)){
					Toast.makeText(LookforActivity.this, "请选择截止时间。", Toast.LENGTH_SHORT).show();
					return;
				}
				List<Company> cl = ConstantValue.companys;
				for(Company c: cl){
					if(c.name.equals(companyStr)){
						companyId = c.id;
					}
				}
				Intent intent = new Intent(LookforActivity.this,LookforDetailActivity.class);
				intent.putExtra("companyName", companyStr);
				intent.putExtra("companyId", companyId);
				intent.putExtra("first", firstStr);
				intent.putExtra("end", endStr);
				startActivity(intent);
				break;
			default:
				break;
		}
	}
	
	/**
	 * 显示时间选择器
	 * @param type
	 */
	public void showDatePicker(final int type){
		DatePickerDialog  dpd=new DatePickerDialog(LookforActivity.this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				y=year;
				m=monthOfYear;
				d=dayOfMonth;
				updateTimer(type);
			}
		}, y, m, d);
		dpd.show();
	}
	
	/**
	 * 更新时间
	 * @param type
	 */
	public void updateTimer(int type){
		switch(type){
			case FIRST:
				String fstr=y+"-"+(m+1)+"-"+d;
				firstBT.setBackgroundResource(R.drawable.lookfor_home_company_tv_up);
				firstBT.setText(fstr);
				firstStr = fstr;
				break;
			case END:
				String estr=y+"-"+(m+1)+"-"+d;
				endBT.setBackgroundResource(R.drawable.lookfor_home_company_tv_up);
				endBT.setText(estr);
				endStr = estr;
				break;
			default:
				break;
		}
	}

}
