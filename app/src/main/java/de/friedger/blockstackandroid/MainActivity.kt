package de.friedger.blockstackandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WebView.setWebContentsDebuggingEnabled(true)

        webview.setWebViewClient(MyWebViewClient())
        webview.setVerticalScrollBarEnabled(false)
        webview.setHorizontalScrollBarEnabled(false)
        with(webview.settings) {
            this.setJavaScriptEnabled(true)
            this.setJavaScriptCanOpenWindowsAutomatically(true)
            this.setSupportMultipleWindows(true)
            this.setSupportZoom(false)
            this.databaseEnabled = true
            this.domStorageEnabled = true
        }

        val url: String = intent.data?.run {
            handleBlockstackScheme(intent.data.toString())
        } ?: "https://browser.blockstack.org/"
        address.setText(url)
        webview.loadUrl(url)

        address.let {
            it.setOnKeyListener { view: View, code: Int, event: KeyEvent ->
                if (code == KeyEvent.KEYCODE_ENTER) {
                    webview.loadUrl(it.text.toString())
                    true
                } else {
                    false
                }
            }
            it.setOnEditorActionListener { tv: TextView, actionId: Int, event: KeyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    webview.loadUrl(it.text.toString())
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private fun handleBlockstackScheme(url: String): String {
        val authRequest = url.replace("blockstack:", "")
        val browserBaseURL = "https://browser.blockstack.org"
        val portalAuthenticationPath = "/auth?authRequest="
        val authURLString = "$browserBaseURL$portalAuthenticationPath$authRequest"
        return authURLString
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            address.setText(url)
            super.onPageFinished(view, url)
        }
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url != null) {
                address.setText(url)
                if (url.startsWith("blockstack:")) {
                    val authURLString = handleBlockstackScheme(url)
                    view?.loadUrl(authURLString)
                    return true
                }
            }
            return false
        }
    }
}
