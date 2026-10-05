package com.example

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class AniArsivPlugin: Plugin() {
    companion object {
        var pluginContext: Context? = null
    }
    override fun load(context: Context) {
        pluginContext = context
        // Yazdığımız asıl eklenti motorunu Cloudstream sistemine kayıt ediyoruz
        registerMainAPI(AniArsivProvider())
    }
}
