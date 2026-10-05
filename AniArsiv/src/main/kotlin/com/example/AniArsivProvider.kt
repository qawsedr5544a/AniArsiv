package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class AniArsivProvider : MainAPI() {
    override var mainUrl = "https://aniarsiv.com"
    override var name = "AniArşiv"
    override val supportedTypes = setOf(TvType.Anime, TvType.AnimeMovie)
    override var lang = "tr"
    override val hasMainPage = true

    override suspend fun search(query: String): List<SearchResponse> {
        val link = "$mainUrl/detayli-arama?q=$query"
        val html = app.get(link).document
        return html.select("div.anime-card, div.card").mapNotNull { 
            it.toSearchResult()
        }
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst(".anime-title, h5, a")?.text() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("img")?.attr("src")
        return newAnimeSearchResponse(title, fixUrl(href)) {
            this.posterUrl = fixUrlNull(posterUrl)
        }
    }

    override suspend fun load(url: String): LoadResponse? {
        val html = app.get(url).document
        val title = html.selectFirst("h1, .anime-details-title")?.text() ?: return null
        val poster = html.selectFirst(".anime-poster img, img")?.attr("src")
        val plot = html.selectFirst(".anime-synopsis, p.description")?.text()
        val episodes = arrayListOf<Episode>()
        
        html.select("div.episode-list a, .episodes a").forEachIndexed { index, element ->
            val epUrl = element.attr("href") ?: return@forEachIndexed
            val epTitle = element.text()
            episodes.add(newEpisode(epUrl) {
                this.name = epTitle
                this.episode = index + 1
            })
        }
        return newAnimeLoadResponse(title, url, TvType.Anime, episodes) {
            this.posterUrl = fixUrlNull(poster)
            this.plot = plot
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val html = app.get(data).document
        val videoSource = html.selectFirst("video source")?.attr("src") 
            ?: html.selectFirst("iframe")?.attr("src")

        if (videoSource != null) {
            callback.invoke(
                ExtractorLink(
                    source = this.name,
                    name = "AniArşiv Oynatıcı",
                    url = fixUrl(videoSource),
                    referer = mainUrl,
                    quality = Qualities.P1080.value
                )
            )
            return true
        }
        return false
    }
}
