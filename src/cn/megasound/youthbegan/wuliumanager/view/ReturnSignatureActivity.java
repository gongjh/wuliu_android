package cn.megasound.youthbegan.wuliumanager.view;


import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 取货的详细页面
 * @author Administrator
 *
 */
public class ReturnSignatureActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "SignatureActivity";
	
	private TextView companyTV, phoneTV, nameTV, typeTV, orderTV, shelfTV;
	private Button companygetBT ;
	
	private PresentGoods presentGoods;
	private PresentGoodsDao presentDao;
	
	private boolean isPersonGet = false, isCompanyGet = false;
	private int signType = 0;//0：直接点击“退件” 1：点击列表项“退件”
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.output_detail_return);
	}
	
	@Override
	protected void findViewById() {
		companyTV = (TextView)findViewById(R.id.output_detail_return_company_tv);
		phoneTV = (TextView)findViewById(R.id.output_detail_return_tel_tv);
		nameTV = (TextView)findViewById(R.id.output_detail_return_name_tv);
		typeTV = (TextView)findViewById(R.id.output_detail_return_type_tv);
		orderTV = (TextView)findViewById(R.id.output_detail_return_order_tv);
		shelfTV = (TextView)findViewById(R.id.output_detail_return_shelf_tv);
		companygetBT = (Button)findViewById(R.id.output_detail_companyget_bt);
	}
	
	@Override
	protected void init() {
		presentDao = new PresentGoodsDaoImpl();
		presentGoods = new PresentGoods();
		//获取intent意图，得到信息
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		signType = data.getInt("signType");
		presentGoods = (PresentGoods) data.getSerializable("presentGoods");
		Log.i(TAG, "signType="+signType+";pid="+presentGoods.id+";name="+presentGoods.name
				+";order="+presentGoods.orderNum+";shelf="+presentGoods.shelfNum);
		fillForms(presentGoods.company.name,presentGoods.phoneNum,presentGoods.name,
				presentGoods.type,presentGoods.orderNum,presentGoods.shelfNum);
	}

	@Override
	protected void setListener() {
		companygetBT.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	/**
	 * 填充表单
	 */
	public void fillForms(String comName,String phoneNum,String name,int type,String order,String shelf){
		companyTV.setText(comName);
		phoneTV.setText(phoneNum);
		nameTV.setText(name);
		String typeStr = "";
		switch(type){
			case 1:
				typeStr="普通";
				break;
			case 2:
				typeStr="同城";
				break;
			case 3:
				typeStr="加急";
				break;
			case 4:
				typeStr="滞留";
				break;
			default:
				break;
		}
		typeTV.setText(typeStr);
		orderTV.setText(order);
		shelfTV.setText(shelf);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.output_detail_companyget_bt://快递公司回收货件
				if(isPersonGet || isCompanyGet){
					Toast.makeText(ReturnSignatureActivity.this, "已被遣返了。", Toast.LENGTH_SHORT).show();
					return;
				}
				String orderNum = ""+orderTV.getText();
				companyGet(orderNum);
				break;
			default:
				break;
		}
	}

	/**
	 * 快递公司来取件
	 * @param orderNum
	 */
	public void companyGet(final String orderNum){
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				return presentDao.moveToReturnGoods(orderNum);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1){
					Toast.makeText(ReturnSignatureActivity.this, "服务器忙，请稍后。", Toast.LENGTH_SHORT).show();
					return;
				}
				if(result==0){
					Toast.makeText(ReturnSignatureActivity.this, "遣返货件失败。", Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(ReturnSignatureActivity.this, "遣返货件成功。", Toast.LENGTH_SHORT).show();
				isCompanyGet = true;
				if(signType==0){
					Intent intent = new Intent(ReturnSignatureActivity.this, CaptureActivity.class);
					intent.putExtra("type", 5);
					startActivity(intent);
					onBackPressed();
				}
			}
			
		}.execute();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		//super.onBackPressed();
	}

}
