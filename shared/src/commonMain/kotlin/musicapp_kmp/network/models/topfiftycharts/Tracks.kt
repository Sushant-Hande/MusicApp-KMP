package musicapp_kmp.network.models.topfiftycharts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import musicapp_kmp.network.models.topfiftycharts.Item

@Serializable
data class Tracks(
    @SerialName("href")
    val href: String?,
    @SerialName("items")
    val items: List<Item>?,
    @SerialName("limit")
    val limit: Int?,
    @SerialName("next")
    val next: String?,
    @SerialName("offset")
    val offset: Int?,
    @SerialName("previous")
    val previous: String?,
    @SerialName("total")
    val total: Int?
)