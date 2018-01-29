package de.friedger.blockstackandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

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
        webview.loadUrl(url)
    }
}

private fun handleBlockstackScheme(url: String): String {
    val authRequest = url.replace("blockstack:", "")
    val browserBaseURL = "http://blockstack-browser.s3-website-us-west-1.amazonaws.com"
    val portalAuthenticationPath = "/auth?authRequest="
    val authURLString = "$browserBaseURL$portalAuthenticationPath$authRequest"
    return authURLString
}


class MyWebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if (url != null) {
            if (url.startsWith("blockstack:")) {
                val authURLString = handleBlockstackScheme(url)
                view?.loadUrl(authURLString)
                return true
            }

        }
        return false
    }
}
