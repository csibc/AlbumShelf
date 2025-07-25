package com.example.albumshelf.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Library : Screen("library")
    object Search : Screen("search")
    object Recommendations : Screen("recommendations")

    object AddOrEdit : Screen("addOrEdit?albumId={albumId}&albumName={albumName}&artistName={artistName}&artworkUrl={artworkUrl}&thumbnailUrl={thumbnailUrl}&year={year}") {
        fun createAddRoute(albumId: String, name: String, artist: String, artworkUrl: String, thumbnailUrl: String, year: String): String {
            val encodedArtworkUrl = java.net.URLEncoder.encode(artworkUrl, "UTF-8")
            val encodedThumbnailUrl = java.net.URLEncoder.encode(thumbnailUrl, "UTF-8")
            return "addOrEdit?albumId=$albumId&albumName=$name&artistName=$artist&artworkUrl=$encodedArtworkUrl&thumbnailUrl=$encodedThumbnailUrl&year=$year"
        }
        fun createEditRoute(albumId: String): String {
            return "addOrEdit?albumId=$albumId"
        }
    }

    object Detail : Screen("detail/{albumId}") {
        fun createRoute(albumId: String) = "detail/$albumId"
    }
}
