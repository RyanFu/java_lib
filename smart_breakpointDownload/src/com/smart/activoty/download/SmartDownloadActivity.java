package com.smart.activoty.download;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smart.impl.SmartDownloadProgressListener;
import com.smart.impl.SmartFileDownloader;

public class SmartDownloadActivity extends Activity {
    private ProgressBar downloadbar;
    private EditText pathText;
    private TextView resultView;
    
    
    /**
     * 当Handler被创建会关联到创建它的当前线程的消息队列，该类用于往消息队列发送消息
     * 消息队列中的消息由当前线程内部进行处理
     */
    
    private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int size = msg.getData().getInt("size");
				downloadbar.setProgress(size);
				float result = (float)downloadbar.getProgress()/ (float)downloadbar.getMax();
				int p = (int)(result*100);
				resultView.setText(p+"%");
				if(downloadbar.getProgress()==downloadbar.getMax())
					Toast.makeText(SmartDownloadActivity.this, R.string.success, 1).show();
				break;

			case -1:
				Toast.makeText(SmartDownloadActivity.this, R.string.error, 1).show();
				break;
			}
			
		}    	
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button button = (Button)this.findViewById(R.id.button);
        downloadbar = (ProgressBar)this.findViewById(R.id.downloadbar);
        pathText = (EditText)this.findViewById(R.id.path);
        resultView = (TextView)this.findViewById(R.id.result);
        button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				String path = "http://www.baidu.com";
//				String path = "https://rm.airbiquity.com/attachments/download/10002/OIL_LCN2Kai_Facebook_%231_20130704.xlsx";
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					File dir = Environment.getExternalStorageDirectory();//�ļ�����Ŀ¼
					download(path, dir);
				}else{
					Toast.makeText(SmartDownloadActivity.this, R.string.sdcarderror, 1).show();
				}
			}
		});
    }
    
    /**
     * 主线程(UI线程)
     * 对于显示控件的界面更新只是由UI线程负责，如果是在非UI线程更新控件的属性值，更新后的显示界面不会反映到屏幕上
     * @param path
     * @param savedir
     */
    private void download(final String path, final File dir){
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SmartFileDownloader loader = new SmartFileDownloader(SmartDownloadActivity.this, path, dir, 3);
					int length = loader.getFileSize();////设置进度条的最大刻度为文件的长度
					downloadbar.setMax(length);
					loader.download(new SmartDownloadProgressListener(){
						@Override
						public void onDownloadSize(int size) {//实时获知文件已经下载的数据长度
							Message msg = new Message();
							msg.what = 1;
							msg.getData().putInt("size", size);							
							handler.sendMessage(msg); // 发送消息
						}});
				} catch (Exception e) {
					Message msg = new Message();//��Ϣ��ʾ
					msg.what = -1;
					msg.getData().putString("error", "����ʧ��");//������ش�����ʾ��ʾʧ�ܣ�
					handler.sendMessage(msg);
				}
			}
		}).start();//��ʼ
    	
    }
}