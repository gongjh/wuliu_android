/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.megasound.youthbegan.wuliumanager.view;

import cn.megasound.youthbegan.wuliumanager.R;
import cn.megasound.youthbegan.wuliumanager.dao.PresentGoodsDao;
import cn.megasound.youthbegan.wuliumanager.dao.UsersDao;
import cn.megasound.youthbegan.wuliumanager.dao.impl.PresentGoodsDaoImpl;
import cn.megasound.youthbegan.wuliumanager.dao.impl.UsersDaoImpl;
import cn.megasound.youthbegan.wuliumanager.entity.PresentGoods;
import cn.megasound.youthbegan.wuliumanager.zxing.CaptureActivityHandler;
import cn.megasound.youthbegan.wuliumanager.zxing.FinishListener;
import cn.megasound.youthbegan.wuliumanager.zxing.InactivityTimer;
import cn.megasound.youthbegan.wuliumanager.zxing.Intents;
import cn.megasound.youthbegan.wuliumanager.zxing.PreferencesActivity;
import cn.megasound.youthbegan.wuliumanager.zxing.ViewfinderView;
import cn.megasound.youthbegan.wuliumanager.zxing.camera.CameraManager;
import cn.megasound.youthbegan.wuliumanager.zxing.result.ResultHandler;
import cn.megasound.youthbegan.wuliumanager.zxing.result.ResultHandlerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * The barcode reader activity itself. This is loosely based on the CameraPreview
 * example included in the Android SDK.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

  private static final String TAG = CaptureActivity.class.getSimpleName();

  private static final int SHARE_ID = Menu.FIRST;
  private static final int HISTORY_ID = Menu.FIRST + 1;
  private static final int SETTINGS_ID = Menu.FIRST + 2;
  private static final int HELP_ID = Menu.FIRST + 3;
  private static final int ABOUT_ID = Menu.FIRST + 4;

  private static final long INTENT_RESULT_DURATION = 1500L;
  private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;
  private static final float BEEP_VOLUME = 0.10f;
  private static final long VIBRATE_DURATION = 200L;

  private static final String PACKAGE_NAME = "com.google.zxing.client.android";
  private static final String PRODUCT_SEARCH_URL_PREFIX = "http://www.google";
  private static final String PRODUCT_SEARCH_URL_SUFFIX = "/m/products/scan";
  private static final String ZXING_URL = "http://zxing.appspot.com/scan";
  private static final String RETURN_CODE_PLACEHOLDER = "{CODE}";
  private static final String RETURN_URL_PARAM = "ret";

  private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES;
  static {
    DISPLAYABLE_METADATA_TYPES = new HashSet<ResultMetadataType>(5);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ISSUE_NUMBER);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.SUGGESTED_PRICE);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ERROR_CORRECTION_LEVEL);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.POSSIBLE_COUNTRY);
  }

  private enum Source {
    NATIVE_APP_INTENT,
    PRODUCT_SEARCH_LINK,
    ZXING_LINK,
    NONE
  }

  private CaptureActivityHandler handler;

  private ViewfinderView viewfinderView;
  private TextView timeTextView;
  private View resultView;
  private MediaPlayer mediaPlayer;
  private Result lastResult;
  private boolean hasSurface;
  private boolean playBeep;
  private boolean vibrate;
  private boolean copyToClipboard;
  private Source source;
  private String sourceUrl;
  private String returnUrlTemplate;
  private Vector<BarcodeFormat> decodeFormats;
  private String characterSet;
  private String versionName;
  private InactivityTimer inactivityTimer;
  private UsersDao userDao;
  private String resultStr;
  private int isUseful=0;
  private int type=0;//1:入库首页;2:入库修改;3:入库新增;4:取件;5:退件
  private Intent intent;
  private PresentGoodsDao presentDao;
  private PresentGoods presentGoods;
  private boolean needReset=false;
  private boolean canGetFromWeb = false;

  /**
   * When the beep has finished playing, rewind to queue up another one.
   */
  private final OnCompletionListener beepListener = new OnCompletionListener() {
    public void onCompletion(MediaPlayer mediaPlayer) {
      mediaPlayer.seekTo(0);
    }
  };

  private final DialogInterface.OnClickListener aboutListener =
      new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialogInterface, int i) {
//      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.zxing_url)));
//      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//      startActivity(intent);
    }
  };

  public ViewfinderView getViewfinderView() {
    return viewfinderView;
  }

  public Handler getHandler() {
    return handler;
  }

  @Override
  public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	
	    Window window = getWindow();
	    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(R.layout.capture);
	
	    CameraManager.init(getApplication());
	    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
	    resultView = findViewById(R.id.result_view);
	    //statusView = (TextView) findViewById(R.id.status_view);
	    handler = null;
	    lastResult = null;
	    hasSurface = false;
	    //historyManager = new HistoryManager(this);
	    //historyManager.trimHistory();
	    inactivityTimer = new InactivityTimer(this);
	    userDao = new UsersDaoImpl();
	    presentDao = new PresentGoodsDaoImpl();
	    Intent intent = getIntent();
	    type = intent.getIntExtra("type", 0);
	    Log.i("test", "扫描："+type);
  }

  @Override
  protected void onResume() {
    super.onResume();
    resetStatusView();

    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (hasSurface) {
      // The activity was paused but not stopped, so the surface still exists. Therefore
      // surfaceCreated() won't be called, so init the camera here.
      initCamera(surfaceHolder);
    } else {
      // Install the callback and wait for surfaceCreated() to init the camera.
      surfaceHolder.addCallback(this);
      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    Intent intent = getIntent();
    String action = intent == null ? null : intent.getAction();
    String dataString = intent == null ? null : intent.getDataString();
    if (intent != null && action != null) {
      if (action.equals(Intents.Scan.ACTION)) {
        // Scan the formats the intent requested, and return the result to the calling activity.
        source = Source.NATIVE_APP_INTENT;
        //decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
      } else if (dataString != null && dataString.contains(PRODUCT_SEARCH_URL_PREFIX) &&
          dataString.contains(PRODUCT_SEARCH_URL_SUFFIX)) {
        // Scan only products and send the result to mobile Product Search.
        source = Source.PRODUCT_SEARCH_LINK;
        sourceUrl = dataString;
        //decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;
      } else if (dataString != null && dataString.startsWith(ZXING_URL)) {
        // Scan formats requested in query string (all formats if none specified).
        // If a return URL is specified, send the results there. Otherwise, handle it ourselves.
        source = Source.ZXING_LINK;
        sourceUrl = dataString;
        Uri inputUri = Uri.parse(sourceUrl);
        returnUrlTemplate = inputUri.getQueryParameter(RETURN_URL_PARAM);
        //decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
      } else {
        // Scan all formats and handle the results ourselves (launched from Home).
        source = Source.NONE;
        decodeFormats = null;
      }
      characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
    } else {
      source = Source.NONE;
      decodeFormats = null;
      characterSet = null;
    }

//    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//    playBeep = prefs.getBoolean(PreferencesActivity.KEY_PLAY_BEEP, true);
//    if (playBeep) {
//      // See if sound settings overrides this
//      AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
//      if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//        playBeep = false;
//      }
//    }
//    vibrate = prefs.getBoolean(PreferencesActivity.KEY_VIBRATE, false);
//    copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true);
//    initBeepSound();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (handler != null) {
      handler.quitSynchronously();
      handler = null;
    }
    CameraManager.get().closeDriver();
  }

  @Override
  protected void onDestroy() {
    inactivityTimer.shutdown();
    super.onDestroy();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
	Log.i("test", "onKeyDown:点击了");
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (source == Source.NATIVE_APP_INTENT) {
        setResult(RESULT_CANCELED);
        finish();
        return true;
      } else if ((source == Source.NONE || source == Source.ZXING_LINK) && lastResult != null) {
        resetStatusView();
        if (handler != null) {
          handler.sendEmptyMessage(R.id.restart_preview);
        }
        return true;
      }
    } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
      // Handle these events so they don't launch the Camera app
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    super.onCreateOptionsMenu(menu);
//    menu.add(0, SHARE_ID, 0, R.string.menu_share)
//        .setIcon(android.R.drawable.ic_menu_share);
//    menu.add(0, HISTORY_ID, 0, R.string.menu_history)
//        .setIcon(android.R.drawable.ic_menu_recent_history);
//    menu.add(0, SETTINGS_ID, 0, R.string.menu_settings)
//        .setIcon(android.R.drawable.ic_menu_preferences);
//    menu.add(0, HELP_ID, 0, R.string.menu_help)
//        .setIcon(android.R.drawable.ic_menu_help);
//    menu.add(0, ABOUT_ID, 0, R.string.menu_about)
//        .setIcon(android.R.drawable.ic_menu_info_details);
//    return true;
//  }

  // Don't display the share menu item if the result overlay is showing.
//  @Override
//  public boolean onPrepareOptionsMenu(Menu menu) {
//    super.onPrepareOptionsMenu(menu);
//    menu.findItem(SHARE_ID).setVisible(lastResult == null);
//    return true;
//  }
//
//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//    switch (item.getItemId()) {
//      case SHARE_ID: {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//        intent.setClassName(this, ShareActivity.class.getName());
//        startActivity(intent);
//        break;
//      }
//      case HISTORY_ID: {
//        AlertDialog historyAlert = historyManager.buildAlert();
//        historyAlert.show();
//        break;
//      }
//      case SETTINGS_ID: {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//        intent.setClassName(this, PreferencesActivity.class.getName());
//        startActivity(intent);
//        break;
//      }
//      case HELP_ID: {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//        intent.setClassName(this, HelpActivity.class.getName());
//        startActivity(intent);
//        break;
//      }
//      case ABOUT_ID:
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.title_about) + versionName);
//        builder.setMessage(getString(R.string.msg_about) + "\n\n" + getString(R.string.zxing_url));
//        builder.setIcon(R.drawable.launcher_icon);
//        builder.setPositiveButton(R.string.button_open_browser, aboutListener);
//        builder.setNegativeButton(R.string.button_cancel, null);
//        builder.show();
//        break;
//    }
//    return super.onOptionsItemSelected(item);
//  }

  @Override
  public void onConfigurationChanged(Configuration config) {
    // Do nothing, this is to prevent the activity from being restarted when the keyboard opens.
    super.onConfigurationChanged(config);
  }

  public void surfaceCreated(SurfaceHolder holder) {
    if (!hasSurface) {
      hasSurface = true;
      initCamera(holder);
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    hasSurface = false;
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  /**
   * A valid barcode has been found, so give an indication of success and show the results.
   *
   * @param rawResult The contents of the barcode.
   * @param barcode   A greyscale bitmap of the camera data which was decoded.
   */
  public void handleDecode(Result rawResult, Bitmap barcode) {
    inactivityTimer.onActivity();
    lastResult = rawResult;
    //historyManager.addHistoryItem(rawResult);
    if (barcode == null) {
      // This is from history -- no saved barcode
      handleDecodeInternally(rawResult, null);
    } else {
      playBeepSoundAndVibrate();
      drawResultPoints(barcode, rawResult);
      switch (source) {
        case NATIVE_APP_INTENT:
        case PRODUCT_SEARCH_LINK:
          handleDecodeExternally(rawResult, barcode);
          break;
        case ZXING_LINK:
          if (returnUrlTemplate == null){
            handleDecodeInternally(rawResult, barcode);
          } else {
            handleDecodeExternally(rawResult, barcode);
          }
          break;
        case NONE:
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
          if (prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
            Toast.makeText(this, R.string.msg_bulk_mode_scanned, Toast.LENGTH_SHORT).show();
            // Wait a moment or else it will scan the same barcode continuously about 3 times
            if (handler != null) {
              handler.sendEmptyMessageDelayed(R.id.restart_preview, BULK_MODE_SCAN_DELAY_MS);
            }
            resetStatusView();
          } else {
            handleDecodeInternally(rawResult, barcode);
          }
          break;
      }
    }
  }

  /**
   * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
   *
   * @param barcode   A bitmap of the captured image.
   * @param rawResult The decoded results which contains the points to draw.
   */
  private void drawResultPoints(Bitmap barcode, Result rawResult) {
    ResultPoint[] points = rawResult.getResultPoints();
    if (points != null && points.length > 0) {
      Canvas canvas = new Canvas(barcode);
      Paint paint = new Paint();
      paint.setColor(getResources().getColor(R.color.result_image_border));
      paint.setStrokeWidth(3.0f);
      paint.setStyle(Paint.Style.STROKE);
      Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
      canvas.drawRect(border, paint);

      paint.setColor(getResources().getColor(R.color.result_points));
      if (points.length == 2) {
        paint.setStrokeWidth(4.0f);
        drawLine(canvas, paint, points[0], points[1]);
      } else if (points.length == 4 &&
                 (rawResult.getBarcodeFormat().equals(BarcodeFormat.UPC_A)) ||
                 (rawResult.getBarcodeFormat().equals(BarcodeFormat.EAN_13))) {
        // Hacky special case -- draw two lines, for the barcode and metadata
        drawLine(canvas, paint, points[0], points[1]);
        drawLine(canvas, paint, points[2], points[3]);
      } else {
        paint.setStrokeWidth(10.0f);
        for (ResultPoint point : points) {
          canvas.drawPoint(point.getX(), point.getY(), paint);
        }
      }
    }
  }

  private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
    canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
  }

  // Put up our own UI for how to handle the decoded contents.
  private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
    //statusView.setVisibility(View.GONE);
    viewfinderView.setVisibility(View.GONE);
    resultView.setVisibility(View.VISIBLE);

    ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
    if (barcode == null) {
//      barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
//          R.drawable.launcher_icon));
    } else {
      barcodeImageView.setImageBitmap(barcode);
    }

//    TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
//    formatTextView.setText(rawResult.getBarcodeFormat().toString());
//
    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
//    TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
//    typeTextView.setText(resultHandler.getType().toString());
//
//    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
//    String formattedTime = formatter.format(new Date(rawResult.getTimestamp()));
    timeTextView = (TextView) findViewById(R.id.time_text_view);
//    timeTextView.setText(formattedTime);


    TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
    View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
    metaTextView.setVisibility(View.GONE);
    metaTextViewLabel.setVisibility(View.GONE);
    Map<ResultMetadataType,Object> metadata =
        (Map<ResultMetadataType,Object>) rawResult.getResultMetadata();
    if (metadata != null) {
      StringBuilder metadataText = new StringBuilder(20);
      for (Map.Entry<ResultMetadataType,Object> entry : metadata.entrySet()) {
        if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
          metadataText.append(entry.getValue()).append('\n');
        }
      }
      if (metadataText.length() > 0) {
        metadataText.setLength(metadataText.length() - 1);
        metaTextView.setText(metadataText);
        metaTextView.setVisibility(View.VISIBLE);
        metaTextViewLabel.setVisibility(View.VISIBLE);
      }
    }

    TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
    CharSequence displayContents = resultHandler.getDisplayContents();
    contentsTextView.setText(displayContents);
    resultStr = (String) displayContents;
    if(!TextUtils.isEmpty(resultStr)){
    	Log.i("test", "contentStr="+displayContents);
    	checkCode(resultStr);
    	
    	
    }
    // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
    int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
    contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

    if (copyToClipboard) {
      ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
      clipboard.setText(displayContents);
    }
  }
  
  public void checkCode(final String code){
	  new AsyncTask<Void, Void, Integer>(){

		@Override
		protected Integer doInBackground(Void... params) {
			return userDao.checkCode(code);
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch(type){
	    		case 1://入库首页
	    			if(result==-1){
	    				isUseful = -1;
	    			}
	    			if(result==0){
	    				//快递单号不存在任何数据库表中//响音效
	    				playMedia(2);
	    				isUseful = 0;
	    			}
	    			if(result==1){
	    				//快递单号存在于及时派货表中//响音效
	    				playMedia(1);
	    				isUseful = 1;
	    			}
	    			if(result==2){
	    				//响音效
	    				playMedia(2);
	    				isUseful = 2;
	    			}
	    			if(result==3){
	    				//响音效
	    				playMedia(3);
	    				isUseful = 3;
	    			}
	    			Message msg = backHandle.obtainMessage();
	    			msg.what = 0x001;
	    			backHandle.sendMessage(msg);
	    			break;
	    		case 2://入库修改
	    			if(result==-1){
	    				isUseful = -1;
	    			}
	    			if(result==0){
	    				//快递单号不存在任何数据库表中有效//响音效
	    				playMedia(1);
	    				isUseful = 0;
	    			}
	    			if(result==1){
	    				//快递单号存在于及时派货表中//响音效
	    				playMedia(2);
	    				isUseful = 1;
	    			}
	    			if(result==2){
	    				//响音效
	    				playMedia(2);
	    				isUseful = 2;
	    			}
	    			if(result==3){
	    				//响音效
	    				playMedia(3);
	    				isUseful = 3;
	    			}
	    			msg = backHandle.obtainMessage();
	    			msg.what = 0x001;
	    			backHandle.sendMessage(msg);
	    			break;
	    		case 3://入库新增
	    			if(result==-1){
	    				isUseful = -1;
	    			}
	    			if(result==0){
	    				//快递单号不存在任何数据库表中有效//响音效
	    				playMedia(1);
	    				isUseful = 0;
	    			}
	    			if(result==1){
	    				//快递单号存在于及时派货表中//响音效
	    				playMedia(2);
	    				isUseful = 1;
	    			}
	    			if(result==2){
	    				//响音效
	    				playMedia(2);
	    				isUseful = 2;
	    			}
	    			if(result==3){
	    				//响音效
	    				playMedia(3);
	    				isUseful = 3;
	    				msg = backHandle.obtainMessage();
	    				msg.what=0x003;
	    				backHandle.sendMessage(msg);
	    				break;
	    			}
	    			msg = backHandle.obtainMessage();
	    			msg.what = 0x001;
	    			backHandle.sendMessage(msg);
	    			break;
	    		case 4://取件
	    		case 5://退件
	    			if(result==-1){
	    				Toast.makeText(CaptureActivity.this, "对不起，连不上服务器。", Toast.LENGTH_SHORT).show();
	    				needReset = true;
	    				timeTextView.setText("连不上服务器，点击返回键继续扫描。");
	    			}
	    			if(result==0){
	    				//快递单号不存在任何数据库表中//响音效
	    				playMedia(3);
	    				Toast.makeText(CaptureActivity.this, "该订单号不存在,点击返回键继续扫描。", Toast.LENGTH_SHORT).show();
	    				needReset = true;
	    				timeTextView.setText("该订单号不存在，点击返回键继续扫描。");
	    			}
	    			if(result==1){
	    				//快递单号存在于及时派货表中//响音效//获取详情
	    				playMedia(1);
	    				Message message = backHandle.obtainMessage();
	    				message.what = 0x002;
	    				backHandle.sendMessage(message);
	    				Log.i("test", "货单号存在于及时派货表中。");
	    				break;
	    			}
	    			if(result==2){
	    				//响音效
	    				playMedia(2);
	    				Toast.makeText(CaptureActivity.this, "该订单号已派送,点击返回键继续扫描。", Toast.LENGTH_SHORT).show();
	    				needReset = true;
	    				timeTextView.setText("该订单号已派送，点击返回键继续扫描。");
	    			}
	    			if(result==3){
	    				//响音效
	    				playMedia(3);
	    				Toast.makeText(CaptureActivity.this, "该订单号已遣返,点击返回键继续扫描。", Toast.LENGTH_SHORT).show();
	    				needReset = true;
	    				timeTextView.setText("该订单号已遣返，点击返回键继续扫描。");
	    			}
	    			break;
	    	}
			
		}
		  
	  }.execute();
  }
  
  Handler backHandle= new Handler(){

	@Override
	public void handleMessage(Message msg) {
		if(msg.what==0x001){
			onBackPressed();
		}
		if(msg.what==0x002){
			Log.i("test", "backhandler:002");
			findByCondition(resultStr);
		}
		if(msg.what==0x003){//新增入库的撤回操作
			new AlertDialog.Builder(CaptureActivity.this)
				.setMessage("该订单号已进行过退件操作，是否要撤回？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Message msg = backHandle.obtainMessage();
		    			msg.what = 0x001;
		    			backHandle.sendMessage(msg);
						dialog.dismiss();
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						rollbackReturnGoods(resultStr);
						dialog.dismiss();
					}
				})
				.show();
		}
	}
	  
  };
  
  private MediaPlayer mp;
  public void playMedia(int mtype){
	  if(mp!=null){
			mp.release();
			mp=null;
		}
	  if(mp==null){
		  try {
			  mp = new MediaPlayer();
			  mp.setOnCompletionListener(blistener);
			  AssetFileDescriptor file = null;
			  switch(mtype){
			  	case 0:
			  		
			  		break;
			  	case 1:
			  		//已经存在的音效,
			  		file = getResources().openRawResourceFd(R.raw.rocket);
			  		break;
			  	case 2:
			  	case 3:
			  		//已派送或已遣返的音效
			  		file = getResources().openRawResourceFd(R.raw.super_maria);
			  		break;
			  }
		
			  mp.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
			  file.close();
			  //mp.setVolume(BEEP_VOLUME, BEEP_VOLUME);
			  mp.prepare();
		} catch (Exception e) {
			mp = null;
			e.printStackTrace();
		} 
		 mp.start();
		  
	  }
  }
  
  OnCompletionListener blistener = new OnCompletionListener() {
	
		@Override
		public void onCompletion(MediaPlayer mp) {
			if(mp!=null){
				mp.seekTo(0);
				mp.stop();
				if(needReset){
					Log.i(TAG, "音效播放完成，需要重置。");
				}
			}
		}
  };

  	/**
  	 * 将已遣回的货件回滚到及时拍货表
  	 * @param rid
  	 */
  	public void rollbackReturnGoods(final String rid){
  		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... params) {
				Log.i(TAG, "rid="+rid);
				return presentDao.rollback(resultStr);
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(result==-1 || result==0){
					Toast.makeText(CaptureActivity.this, "撤回操作失败。", Toast.LENGTH_SHORT).show();
				}
				if(result==1){
					Toast.makeText(CaptureActivity.this, "撤回操作成功。", Toast.LENGTH_SHORT).show();
					canGetFromWeb = true;
				}
				Message msg = backHandle.obtainMessage();
    			msg.what = 0x001;
    			backHandle.sendMessage(msg);
			}
  			
  		}.execute();
  	}
  
  	/**
	 * 通过扫描查找并跳转
	 * @param page
	 * @param phoneNum
	 */
	public void findByCondition(final String orderNum){
		new AsyncTask<Void, Void, List<PresentGoods>>(){

			@Override
			protected List<PresentGoods> doInBackground(Void... params) {
				return presentDao.getPresentGoodsByCondition(1, orderNum);
			}

			@Override
			protected void onPostExecute(List<PresentGoods> result) {
				if(result==null){
					Toast.makeText(CaptureActivity.this, "对不起，服务器忙。", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(result.size()==0){
					Toast.makeText(getApplicationContext(), "暂时没有该条件的数据。", Toast.LENGTH_SHORT).show();
					return;
				}
				presentGoods = new PresentGoods();
				presentGoods = result.get(0);
				Log.i("test", "获取详情orderNum："+presentGoods.orderNum);
				Message message = backHandle.obtainMessage();
				message.what = 0x001;
				backHandle.sendMessage(message);
			}
			
		}.execute();
	}
	
	
  
  @Override
  public void onBackPressed() {
	  Log.i("test", "点击返回键");
	  switch(type){
	  	case 1:
	  	case 2:
	  		intent = new Intent();
			intent.putExtra("resultStr", resultStr);
			intent.putExtra("isUseful", isUseful);
			setResult(1, intent);
			this.finish();
			break;
	  	case 3:
			intent = new Intent();
			intent.putExtra("resultStr", resultStr);
			intent.putExtra("isUseful", isUseful);
			intent.putExtra("canGetFromWeb", canGetFromWeb);
			setResult(1, intent);
			this.finish();
			break;
	  	case 4:
	  		if(presentGoods!=null){
		  		intent = new Intent(CaptureActivity.this, SentSignatureActivity.class);
		  		Bundle data = new Bundle();
		  		data.putInt("signType", 0);
		  		data.putSerializable("presentGoods", presentGoods);
		  		intent.putExtras(data);
		  		startActivity(intent);
	  		}
	  		this.finish();
	  		break;
	  	case 5:
	  		if(presentGoods!=null){
		  		intent = new Intent(CaptureActivity.this, ReturnSignatureActivity.class);
		  		Bundle data = new Bundle();
		  		data.putInt("signType", 0);
		  		data.putSerializable("presentGoods", presentGoods);
		  		intent.putExtras(data);
		  		startActivity(intent);
	  		}
	  		this.finish();
	  		break;
	  }
  } 

  // Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
  private void handleDecodeExternally(Result rawResult, Bitmap barcode) {
    viewfinderView.drawResultBitmap(barcode);

    // Since this message will only be shown for a second, just tell the user what kind of
    // barcode was found (e.g. contact info) rather than the full contents, which they won't
    // have time to read.
    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
    //statusView.setText(getString(resultHandler.getDisplayTitle()));

    if (copyToClipboard) {
      ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
      clipboard.setText(resultHandler.getDisplayContents());
    }

    if (source == Source.NATIVE_APP_INTENT) {
      // Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
      // the deprecated intent is retired.
      Intent intent = new Intent(getIntent().getAction());
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
      intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
      Message message = Message.obtain(handler, R.id.return_scan_result);
      message.obj = intent;
      handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
    } else if (source == Source.PRODUCT_SEARCH_LINK) {
      // Reformulate the URL which triggered us into a query, so that the request goes to the same
      // TLD as the scan URL.
      Message message = Message.obtain(handler, R.id.launch_product_query);
      int end = sourceUrl.lastIndexOf("/scan");
      message.obj = sourceUrl.substring(0, end) + "?q=" +
          resultHandler.getDisplayContents().toString() + "&source=zxing";
      handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
    } else if (source == Source.ZXING_LINK) {
      // Replace each occurrence of RETURN_CODE_PLACEHOLDER in the returnUrlTemplate
      // with the scanned code. This allows both queries and REST-style URLs to work.
      Message message = Message.obtain(handler, R.id.launch_product_query);
      message.obj = returnUrlTemplate.replace(RETURN_CODE_PLACEHOLDER,
          resultHandler.getDisplayContents().toString());
      handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
    }
  }

  /**
   * Creates the beep MediaPlayer in advance so that the sound can be triggered with the least
   * latency possible.
   */
  private void initBeepSound() {
    if (playBeep && mediaPlayer == null) {
      // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
      // so we now play on the music stream.
      setVolumeControlStream(AudioManager.STREAM_MUSIC);
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setOnCompletionListener(beepListener);

      AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
      try {
        mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
            file.getLength());
        file.close();
        mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
        mediaPlayer.prepare();
      } catch (IOException e) {
        mediaPlayer = null;
      }
    }
  }

  private void playBeepSoundAndVibrate() {
    if (playBeep && mediaPlayer != null) {
      //mediaPlayer.start();//将原本的音效关闭
    }
    if (vibrate) {
      Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
      vibrator.vibrate(VIBRATE_DURATION);
    }
  }

  private void initCamera(SurfaceHolder surfaceHolder) {
    try {
      CameraManager.get().openDriver(surfaceHolder);
    } catch (IOException ioe) {
      Log.w(TAG, ioe);
      displayFrameworkBugMessageAndExit();
      return;
    } catch (RuntimeException e) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w(TAG, "Unexpected error initializating camera", e);
      displayFrameworkBugMessageAndExit();
      return;
    }
    if (handler == null) {
      handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
    }
  }

  private void displayFrameworkBugMessageAndExit() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.app_name));
    builder.setMessage(getString(R.string.msg_camera_framework_bug));
    builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
    builder.setOnCancelListener(new FinishListener(this));
    builder.show();
  }

  private void resetStatusView() {
    resultView.setVisibility(View.GONE);
    //statusView.setText(R.string.msg_default_status);
    //statusView.setVisibility(View.VISIBLE);
    viewfinderView.setVisibility(View.VISIBLE);
    lastResult = null;
  }

  public void drawViewfinder() {
    viewfinderView.drawViewfinder();
  }
}
