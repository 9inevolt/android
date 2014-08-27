package gg.destiny.app.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import com.mogoweb.chrome.*;

//import android.webkit.*;
//import android.webkit.WebView;
//import com.mongoweb.chrome.WebView;

public class WebViewer {
	public long pageStartTime = 0;
	public TextView pageLoadTime;
	public RelativeLayout.LayoutParams loparams;
	public RelativeLayout contentContainer;
	public Button backToLoadedURLButton;

	boolean useNative;
	android.webkit.WebView nativeWV;
	//com.mogoweb.chrome.WebView chromiumWV;

	private ViewGroup vagueWebView;

	private String lastLoadedURL = "";

	WebViewer(){
//		use native if we have websockets natively
		this.useNative = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}
	public void Make(Context context){

		nativeWV = createNativeWebView(context);
		vagueWebView = nativeWV;

		vagueWebView.setLayoutParams(loparams);
		contentContainer.addView(vagueWebView);
		vagueWebView.requestFocus();

		backToLoadedURLButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(lastLoadedURL.length() > 0){
					LoadURL(lastLoadedURL);
				}
			}
		});

	}

	public void LoadURL(String url){
		lastLoadedURL = url;
			nativeWV.loadUrl(url);
	}

//	native version
	private android.webkit.WebView createNativeWebView(Context context){
		android.webkit.WebView webView = new android.webkit.WebView(context);
        webView.setWebChromeClient(new android.webkit.WebChromeClient());
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        android.webkit.WebSettings settings = webView.getSettings();
        settings.setAllowUniversalAccessFromFileURLs(true);
//        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                genericOnPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(android.webkit.WebView view, String url)
            {
                super.onPageFinished(view, url);
                genericOnPageFinished(view, url);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
		return webView;
	}

	// generic methods
	private void genericOnPageStarted(View view, String url, Bitmap favicon){
        //et.setText(url);
        pageStartTime = System.currentTimeMillis();
        pageLoadTime.setText("0ms");
        if(!url.startsWith(lastLoadedURL)){
        	//shoe back button
        	backToLoadedURLButton.setVisibility(View.VISIBLE);
        }else{
        	//hide back button
        	backToLoadedURLButton.setVisibility(View.GONE);
        }
	}

	private void genericOnPageFinished(View view, String url){
		if (pageStartTime == 0)
        {
        }
        else
        {
            long loadTime = (System.currentTimeMillis() - pageStartTime);
            pageLoadTime.setText(String.format("%sms to load chat", loadTime));
            System.out.println(String.format("page load time: %sms", loadTime));
        }
//        if(url != lastLoadedURL){
//        	//shoe back button
//        	backToLoadedURLButton.setVisibility(View.VISIBLE);
//        }else{
//        	//hide back button
//        	backToLoadedURLButton.setVisibility(View.GONE);
//        }
	}
	public void setAlignParentTop(boolean b) {
		// TODO Auto-generated method stub
//		contentContainer.`
		RelativeLayout.LayoutParams cloParams;
		cloParams = (RelativeLayout.LayoutParams)contentContainer.getLayoutParams();
		if(b){
			cloParams.removeRule(RelativeLayout.BELOW);
			cloParams.addRule(RelativeLayout.BELOW, R.id.header_container);
//			cloParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		}else{
//			cloParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
			cloParams.removeRule(RelativeLayout.BELOW);
			cloParams.addRule(RelativeLayout.BELOW, R.id.everything_else);
		}
		contentContainer.setLayoutParams(cloParams);
		contentContainer.requestLayout();
		vagueWebView.requestLayout();

	}


}