import com.lagradost.CloudstreamPlugin3

plugins {
    id("com.lagradost.CloudstreamPlugin3")
}

cloudstream {
    // Uygulama içinde eklenti ayarlarında görünecek bilgileri tanımlar
    provider {
        name = "AniArşiv"
        description = "AniArşiv Türkçe Anime Eklentisi"
        language = "tr"
        version = 1
    }
}
