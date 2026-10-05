package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import org.jsoup.Jsoup

class AniArsivProvider : MainAPI() {
    override var mainUrl = "https://aniarsiv.com"
    override var name = "AniArşiv"
    override val supportedTypes = setOf(TvType.Anime, TvType.AnimeMovie)
    override var lang = "tr"
    override val hasMainPage = true

    // DİĞER TÜRKÇE EKLENTİLERDEKİ ARAMA KALIBI
    override suspend fun search(query: String): List<SearchResponse> {
        val searchUrl = "$mainUrl/detayli-arama?q=$query"
        val response = app.get(searchUrl).text
        val document = Jsoup.parse(response)
        
        // Diğer eklentiler gibi listedeki her elemanı kutu kutu seçiyoruz
        return document.select("div.anime-card, div.card, .anime-box").mapNotNull { element ->
            val title = element.selectFirst(".anime-title, h5, a")?.text() ?: return@mapNotNull null
            val href = element.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val posterUrl = element.selectFirst("img")?.attr("src")

            newAnimeSearchResponse(title, fixUrl(href)) {
                this.posterUrl = fixUrlNull(posterUrl)
            }
        }
    }

    // DİĞER TÜRKÇE EKLENTİLERDEKİ DETAY SAYFASI KALIBI
    override suspend fun load(url: String): LoadResponse? {
        val response = app.get(url).text
        val document = Jsoup.parse(response)
        
        val title = document.selectFirst("h1, .anime-details-title")?.text() ?: return null
        val poster = document.selectFirst(".anime-poster img, img")?.attr("src")
        val plot = document.selectFirst(".anime-synopsis, p.description, .text-muted")?.text()
        
        val episodes = arrayListOf<Episode>()
        
        // Sitedeki tüm bölüm linklerini topluyoruz
        document.select("div.episode-list a, .episodes a, .episode-nav a").forEachIndexed { index, element ->
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

    // DİĞER TÜRKÇE EKLENTİLERDEKİ VİDEO YAKALAMA KALIBI
    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val response = app.get(data).text
        val document = Jsoup.parse(response)
        
        // Diğer eklentilerin kullandığı video source tarama mekanizması
        val videoSource = document.selectFirst("video source")?.attr("src") 
            ?: document.selectFirst("iframe")?.attr("src")

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
