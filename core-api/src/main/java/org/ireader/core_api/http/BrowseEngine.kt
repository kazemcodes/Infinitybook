package org.ireader.core_api.http

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.ireader.core_api.log.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import kotlin.collections.set

class BrowseEngine @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun fetch(
        url: String,
        selector: String? = null,
        headers: Map<String, String> = emptyMap(),
        timeout: Long = 5000L,
        userAgent: String = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36",
    ): Result {
        var html: Document = Document("No Data was Found")
        var cookies: String? = null
        var responseHeader: WebResourceResponse? = null
        withContext(Dispatchers.Main) {
            val scope = this
            val client = WebView(context.applicationContext)
            client.setDefaultSettings()
            client.loadUrl(url, headers)
            try {
                client.settings.userAgentString = userAgent
            } catch (e: Throwable) {
                Log.error(exception = e, "failed to set user agent")
            }
            var isLoadUp: Boolean = false

            client.webViewClient = object : WebViewClientCompat() {
                override fun onPageFinished(view: WebView, url: String) {
                    cookies = CookieManager.getInstance().getCookie(url)
                    scope.launch {
                        html = Jsoup.parse(client.getHtml())
                        if (selector != null) {
                            while (html.select(selector).text().isEmpty()) {
                                html = Jsoup.parse(client.getHtml())
                            }
                            isLoadUp = true
                        } else {
                            isLoadUp = true
                        }
                    }
                }

                override fun onReceivedErrorCompat(
                    view: WebView,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String,
                    isMainFrame: Boolean,
                ) {
                    isLoadUp = true
                    Log.error("WebView: Not shown")
                }

                override fun shouldInterceptRequestCompat(
                    view: WebView,
                    url: String,
                ): WebResourceResponse? {
                    val req = super.shouldInterceptRequestCompat(view, url)
                    responseHeader = req
                    return req
                }
            }
            var currentTime = 0
            while (!isLoadUp && currentTime < timeout) {
                delay(200)
                currentTime += 200
            }
            html = Jsoup.parse(client.getHtml())
            client.clearHistory()
            client.clearCache(true)
            client.destroy()
        }

        return Result(
            responseBody = html.html(),
            responseHeader = responseHeader?.responseHeaders,
            baseUri = url,
            cookies = getCookieMap(cookies),
            responseCode = responseHeader?.statusCode,
            mimeType = responseHeader?.mimeType
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.setDefaultSettings() {
    with(settings) {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        // https://stackoverflow.com/questions/9128952/caching-in-android-webview
        // setAppCacheEnabled(true)
        useWideViewPort = true
        loadWithOverviewMode = true
        cacheMode = WebSettings.LOAD_DEFAULT
    }
}

@Suppress("OverridingDeprecatedMember")
private abstract class WebViewClientCompat : WebViewClient() {

    open fun shouldOverrideUrlCompat(view: WebView, url: String): Boolean {
        return false
    }

    open fun shouldInterceptRequestCompat(view: WebView, url: String): WebResourceResponse? {
        return null
    }

    open fun onReceivedErrorCompat(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String,
        isMainFrame: Boolean,
    ) {
    }

    @TargetApi(Build.VERSION_CODES.N)
    final override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest,
    ): Boolean {
        return shouldOverrideUrlCompat(view, request.url.toString())
    }

    final override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return shouldOverrideUrlCompat(view, url)
    }

    final override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest,
    ): WebResourceResponse? {
        return shouldInterceptRequestCompat(view, request.url.toString())
    }

    final override fun shouldInterceptRequest(
        view: WebView,
        url: String,
    ): WebResourceResponse? {
        return shouldInterceptRequestCompat(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    final override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError,
    ) {
        onReceivedErrorCompat(
            view,
            error.errorCode,
            error.description?.toString(),
            request.url.toString(),
            request.isForMainFrame
        )
    }

    final override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String,
    ) {
        onReceivedErrorCompat(view, errorCode, description, failingUrl, failingUrl == view.url)
    }

    final override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceResponse,
    ) {
        onReceivedErrorCompat(
            view,
            error.statusCode,
            error.reasonPhrase,
            request.url
                .toString(),
            request.isForMainFrame
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun WebView.getHtml(): String = suspendCancellableCoroutine { continuation ->
    settings.javaScriptEnabled = true
    if (!settings.javaScriptEnabled)
        throw IllegalStateException("Javascript is disabled")

    evaluateJavascript(
        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"
    ) {
        continuation.resume(
            it!!.replace("\\u003C", "<")
                .replace("\\n", "")
                .replace("\\t", "")
                .replace("\\\"", "\"")
                .replace("<hr />", "")
        ) {
        }
    }
}

fun getCookieMap(cookies: String?): Map<String, String>? {
    val map = mutableMapOf<String, String>()
    if (cookies == null) return null

    val typedArray = cookies.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    for (element in typedArray) {
        val split = element.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (split.size >= 2) {
            map[split[0]] = split[1]
        } else if (split.size == 1) {
            map[split[0]] = ""
        }
    }

    return map
}

/**
 * This object is representing the result of an request
 * @param responseBody - the response responseBody
 * @param responseStatus - the http responses status code and message
 * @param contentType - the http responses content type
 * @param responseHeader - the http responses headers
 * @param cookies - the http response's cookies
 */
@Suppress("LongParameterList")
class Result(
    val responseBody: String,
    val responseCode: Int? = null,
    val mimeType: String? = null,
    val responseHeader: Map<String, String>? = emptyMap(),
    val baseUri: String = "",
    val cookies: Map<String, String>? = emptyMap(),
)