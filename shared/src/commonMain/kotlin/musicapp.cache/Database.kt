package musicapp.cache

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = MusicDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.musicDatabaseQueries

    internal fun getFavoritePlayList(): List<FavoritePlayList> {
        return dbQuery.selectFavoritePlayList(::mapFavoritePlayListSelecting).executeAsList()
    }

    internal fun saveFavoritePlayList(favoritePlayList: FavoritePlayList) {
        return dbQuery.insertFavoritePlayList(favoritePlayList.href, favoritePlayList.isFavorite)
    }

    fun deleteFavoritePlayList(href: String) {
        dbQuery.deletePlayListFromFavorite(href)
    }

    private fun mapFavoritePlayListSelecting(href: String, isFavorite: Boolean): FavoritePlayList {
        return FavoritePlayList(href = href, isFavorite = isFavorite)
    }
}