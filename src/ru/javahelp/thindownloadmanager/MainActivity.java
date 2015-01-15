package ru.javahelp.thindownloadmanager;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;
import android.view.View.*;
import android.os.*;

public class MainActivity extends Activity {

	private ThinDownloadManager downloadManager;
	private static final int DOWNLOAD_THREAD_POOL_SIZE = 5; // Максимальное количество потоков загрузки
															

	Button download, cancel;
	ProgressBar downloadProgress;
	TextView tvProgress;
	private static final String FILE = "https://dl.dropboxusercontent.com/u/25887355/test_photo1.JPG"; // Путь к файлу
																									

	int downloadId;
	DownloadRequest downloadRequest;
	int mb = 1048576, kb = 1024;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		download = (Button) findViewById(R.id.start);
		cancel = (Button) findViewById(R.id.cancel);
		tvProgress = (TextView) findViewById(R.id.downloadStatus);
		downloadProgress = (ProgressBar) findViewById(R.id.downloadProgress);

		downloadProgress.setMax(100);
		downloadProgress.setProgress(0);

		tvProgress.setText("Нажмите \"Старт\" для начала загрузки");

		downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);

		Uri downloadUri = Uri.parse(FILE);
		Uri destinationUri = Uri
				.parse(Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
						+ "/android-image.png");

		downloadRequest = new DownloadRequest(downloadUri)
				.setDestinationURI(destinationUri)
				.setPriority(DownloadRequest.Priority.LOW)
				.setDownloadListener(downloadListener);

		download.setOnClickListener(onClick);
		cancel.setOnClickListener(onClick);
	}

	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.start:
				downloadId = downloadManager.add(downloadRequest);
				break;
			case R.id.cancel:
				downloadRequest.cancel();
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		downloadManager.release();
	}

	DownloadStatusListener downloadListener = new DownloadStatusListener() {

		@Override
		public void onDownloadComplete(int id) {
			tvProgress.setText("Загрузка завершена");
		}

		@Override
		public void onDownloadFailed(int id, int errorCode, String errorMessage) {
			tvProgress.setText("Загрузка отменена");
			downloadProgress.setProgress(0);
		}

		@Override
		public void onProgress(int id, long totalBytes, long arg3, int progress) {
			tvProgress.setText("Прогресс: " + progress + "%" + " "
					+ getBytesDownloaded(progress, totalBytes));
			downloadProgress.setProgress(progress);

		}

	};

	private String getBytesDownloaded(int progress, long totalBytes) {

		long bytesCompleted = (progress * totalBytes) / 100;

		if (totalBytes >= mb) {
			return (format((float) bytesCompleted / mb) + "/"
					+ format((float) totalBytes / mb) + "Мб");
		}
		if (totalBytes >= kb) {
			return ((float) bytesCompleted / kb + "/" + (float) totalBytes / kb + "Кб");
		} else {
			return ("" + bytesCompleted + "/" + totalBytes);
		}
	}

	public String format(Number n) {
		return String.format("%.2f", n);
	}
}