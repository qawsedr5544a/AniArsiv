import com.lagradost.CloudstreamPlugin3

plugins {
    id("cloudstream")
}

cloudstream {
    authors     = listOf("qawsedr5544a")
    language    = "tr"
    description = "AniArşiv - Türkçe Anime İzleme Platformu."

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
    **/
    status  = 1
    tvTypes = listOf("Anime", "AnimeMovie")
    iconUrl = "https://google.com%"
}
