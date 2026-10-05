package com.example

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class AniArsivProvider : MainAPI() {
    override var mainUrl = "https://aniarsiv.com"
    override var name = "AniArşiv"
    override var lang = "tr"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Anime, TvType.OVA, TvType.AnimeMovie)

    override val mainPage = mainPageOf(
        "puan_desc" to "En Yüksek Puanlılar"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val res = app.get(
            "$mainUrl/api/animes?sort=${request.data}&sirala=${request.data}&page=$page&limit=24"
        ).parsedSafe<AnimeListResponse>()
        val items = res?.items?.map { it.toSearch() } ?: emptyList()
        return HomePageResponse(
            listOf(HomePageList(request.name, items, hasNext = page < (res?.totalPages ?: 0)))
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val res = app.get(
            "$mainUrl/api/animes?q=${query.encodeUri()}&sort=puan_desc&sirala=puan_desc&page=1&limit=24"
        ).parsedSafe<AnimeListResponse>()
        return res?.items?.map { it.toSearch() } ?: emptyList()
    }

    override suspend fun load(url: String): LoadResponse? {
        val slug = url.substringAfterLast("/")
        val d = app.get("$mainUrl/api/anime/$slug").parsedSafe<AnimeDetail>() ?: return null

        val episodes = d.bolumler.orEmpty()
            .filter { it.hasCdn == 1 || (it.linkSayisi ?: 0) > 0 }
            .mapIndexed { index, b ->
                Episode(
                    data = b.slug,
                    name = b.ad,
                    episode = Regex("""(\d+)\.\s*Bölüm""").find(b.ad)
                        ?.groupValues?.get(1)?.toIntOrNull() ?: (index + 1)
                )
            }
            .sortedBy { it.episode }

        return newAnimeLoadResponse(d.baslik, "$mainUrl/anime/$slug", TvType.Anime) {
            posterUrl = d.coverImage
            backgroundPosterUrl = d.bannerImage
            plot = (d.ozet?.takeIf { it.isNotBlank() } ?: d.anilist?.description)
                ?.replace("rnrn", "\n\n")?.trim()
            year = d.yil
            tags = d.turler?.split(",")?.map { it.trim() }
            addEpisodes(DubStatus.Subbed, episodes)
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val res = app.get("$mainUrl/api/bolum/$data").parsedSafe<BolumResponse>() ?: return false
        val v = res.cdnVideo ?: return false
        
        callback(
            ExtractorLink(
                source = name,
                name = "Özel Sunucu",
                url = v.videoUrl,
                referer = "$mainUrl/",
                quality = v.height ?: Qualities.Unknown.value,
                isM3u8 = false
            )
        )
        return true
    }

    private fun AnimeItem.toSearch() =
        newAnimeSearchResponse(baslik, "$mainUrl/anime/$slug", TvType.Anime) {
            posterUrl = coverImage ?: resim
        }

    data class AnimeListResponse(
        @JsonProperty("items") val items: List<AnimeItem>?,
        @JsonProperty("total_pages") val totalPages: Int?
    )

    data class AnimeItem(
        @JsonProperty("slug") val slug: String,
        @JsonProperty("baslik") val baslik: String,
        @JsonProperty("cover_image") val coverImage: String?,
        @JsonProperty("resim") val resim: String?
    )

    data class AnimeDetail(
        @JsonProperty("baslik") val baslik: String,
        @JsonProperty("ozet") val ozet: String?,
        @JsonProperty("yil") val yil: Int?,
        @JsonProperty("turler") val turler: String?,
        @JsonProperty("cover_image") val coverImage: String?,
        @JsonProperty("banner_image") val bannerImage: String?,
        @JsonProperty("bolumler") val bolumler: List<BolumItem>?,
        @JsonProperty("anilist") val anilist: AnilistInfo?
    )

    data class BolumItem(
        @JsonProperty("slug") val slug: String,
        @JsonProperty("ad") val ad: String,
        @JsonProperty("link_sayisi") val linkSayisi: Int?,
        @JsonProperty("has_cdn") val hasCdn: Int?
    )

    data class AnilistInfo(
        @JsonProperty("description") val description: String?
    )

    data class BolumResponse(
        @JsonProperty("cdn_video") val cdnVideo: CdnVideo?
    )

    data class CdnVideo(
        @JsonProperty("video_url") val videoUrl: String,
        @JsonProperty("height") val height: Int?
    )
}
