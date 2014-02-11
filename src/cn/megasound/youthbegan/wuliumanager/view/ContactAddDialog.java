package cn.megasound.youthbegan.wuliumanager.view;


import cn.megasound.youthbegan.wuliumanager.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ContactAddDialog extends Dialog implements 
	android.view.View.OnClickListener{
	private static int theme = android.R.style.Theme_Translucent_NoTitleBar;
	private Context context;
	private String content;
	private Button okBT,cancelBT;
	private EditText contentET ;
	private OnOkListener okListener;

	public ContactAddDialog(Context context,String content) {
		super(context,theme);
		this.context = context;
		this.content = content;
	}
	
	public void setOkListener(OnOkListener okListener) {
		this.okListener = okListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.contact_add_dialog);
		this.setCancelable(true);
		init();
	}
	
	/**
	 * 
	 */
	public void init(){
		contentET = (EditText)findViewById(R.id.contact_add_et) ;
		contentET.setText(content);
		okBT = (Button)findViewById(R.id.contact_ok_bt);
		cancelBT = (Button)findViewById(R.id.contact_cancel_bt);
		okBT.setOnClickListener(this);
		cancelBT.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.contact_cancel_bt:
				this.dismiss();
				break;
			case R.id.contact_ok_bt:
				if(okListener != null){
					content = ""+contentET.getText();
					okListener.setOnClickListener(content);
				}
				this.dismiss();
				break;
 		default:
			break;
		}
	}
	
	/**
	 * 回调函数接口
	 */
	public interface OnOkListener{
		void setOnClickListener(String con);
	}
	
}
