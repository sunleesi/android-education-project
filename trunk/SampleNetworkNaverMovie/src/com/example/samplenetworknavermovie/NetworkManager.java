package com.example.samplenetworknavermovie;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.example.samplenetworknavermovie.NetworkManager.OnResultListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class NetworkManager {
	private static NetworkManager instance;

	public static NetworkManager getInstance() {
		if (instance == null) {
			instance = new NetworkManager();
		}
		return instance;
	}

	ThreadPoolExecutor mExecutor;
	ThreadPoolExecutor mImageExecutor;
	
	public static final int MESSAGE_SUCCESS = 1;
	public static final int MESSAGE_FAIL = 2;
	
	Handler mainHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			NetworkRequest request = (NetworkRequest)msg.obj;
			switch(msg.what) {
			case MESSAGE_SUCCESS :
				request.sendSuccess();
				break;
			case MESSAGE_FAIL :
				request.sendFail();
				break;
			}
			removeRequest(request.getContext(), request);
		}
	};

	HashMap<Context, List<NetworkRequest>> mRequestMap = new HashMap<Context, List<NetworkRequest>>();

	private NetworkManager() {
		mExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		mImageExecutor = new ThreadPoolExecutor(5, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public interface OnResultListener<T> {
		public void onSuccess(NetworkRequest request, T result);

		public void onFail(NetworkRequest request, int code);
	}

	public void getNaverMovie(Context context, NaverMovieRequest request,
			OnResultListener<NaverMovies> listener) {
		List<NetworkRequest> list = mRequestMap.get(context);
		if (list == null) {
			list = new ArrayList<NetworkRequest>();
			mRequestMap.put(context, list);
		}
		list.add(request);

		request.setContext(context);
		request.setOnResultListener(listener);
		request.setNetworkManager(this);
//		new Thread(request).start();
		mExecutor.execute(request);
	}

	public void getImage(Context context, ImageRequest request,
			OnResultListener<Bitmap> listener) {
		List<NetworkRequest> list = mRequestMap.get(context);
		if (list == null) {
			list = new ArrayList<NetworkRequest>();
			mRequestMap.put(context, list);
		}
		list.add(request);

		request.setContext(context);
		request.setOnResultListener(listener);
		request.setNetworkManager(this);
		mImageExecutor.execute(request);
	}
	
	public void cancel(Context context) {
		List<NetworkRequest> list = mRequestMap.get(context);
		if (list != null) {
			for (NetworkRequest req : list) {
				req.setCancel(true);
			}
		}
	}
	
	public void removeRequest(Context context, NetworkRequest request) {
		List<NetworkRequest> list = mRequestMap
				.get(context);
		if (list != null) {
			list.remove(request);
			if (list.size() == 0) {
				mRequestMap.remove(context);
			}
		}
	}

	public void sendSuccessMessage(NetworkRequest request) {
		Message msg = mainHandler.obtainMessage(MESSAGE_SUCCESS, request);
		mainHandler.sendMessage(msg);
	}
	
	public void sendFailMessage(NetworkRequest request) {
		Message msg = mainHandler.obtainMessage(MESSAGE_FAIL, request);
		mainHandler.sendMessage(msg);
	}
	
	private static final String KEY = "55f1e342c5bce1cac340ebb6032c7d9a";

	class NetworkTask<T> implements Runnable {
		NetworkRequest<T> mRequest;
		OnResultListener<T> mListener;
		Context mContext;

		public NetworkTask(Context context, NetworkRequest<T> request,
				OnResultListener<T> listener) {
			mContext = context;
			mRequest = request;
			mListener = listener;
		}

		@Override
		public void run() {
			int retryCount = 3;
			while (retryCount > 0 && !mRequest.isCanceled()) {
				try {
					URL url = mRequest.getURL();
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					mRequest.setConfiguration(conn);
					conn.setRequestMethod(mRequest.getRequestMethod());
					mRequest.writeOutput(conn);

					if (mRequest.isCanceled()) continue;
					int responseCode = conn.getResponseCode();
					if (mRequest.isCanceled()) continue;
					
					mRequest.setConnection(conn);

					if (responseCode == HttpURLConnection.HTTP_OK) {
						InputStream is = conn.getInputStream();
						if (mRequest.isCanceled())
							return;
						final T result = mRequest.doParsing(is);
						mainHandler.post(new Runnable() {

							@Override
							public void run() {
								if (mListener != null && !mRequest.isCanceled()) {
									mListener.onSuccess(mRequest, result);
								}
								removeRequest(mContext, mRequest);
							}
						});
					} else {
						mainHandler.post(new Runnable() {
							
							@Override
							public void run() {
								if (mListener != null && !mRequest.isCanceled()) {
									mListener.onFail(mRequest, -1);
								}
								removeRequest(mContext, mRequest);
							}
						});
					}
					return;

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					retryCount = 0;
				} catch (MalformedURLException e) {
					e.printStackTrace();
					retryCount = 0;
				} catch (IOException e) {
					e.printStackTrace();
					retryCount--;
				}
			}
			mainHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if (mListener != null && !mRequest.isCanceled()) {
						mListener.onFail(mRequest, -2);
					}
					removeRequest(mContext, mRequest);
				}
			});

		}
	}


}
