package com.example.movieapp.domain.usecase

import com.example.movieapp.data.network.instance.MovieInstance
import com.example.movieapp.domain.model.details.FormattedMovieDetails
import com.example.movieapp.domain.model.details.MovieDetails
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.roundToInt

class FormatMovieDetailsUseCase {
    operator fun invoke(movieDetails: MovieDetails): FormattedMovieDetails {
        return FormattedMovieDetails(
            title = movieDetails.title,
            overview = movieDetails.overview,
            posterUrl = "${MovieInstance.BASE_URL_IMAGE}${movieDetails.posterPath}",
            voteAverage = DecimalFormat("#.#").format(movieDetails.voteAverage),
            voteCount = formatVoteCount(movieDetails.voteCount),
            releaseYear = movieDetails.releaseDate.split("-").firstOrNull() ?: "",
            genres = movieDetails.genres.joinToString(", ") { it.name },
            runtime = formatRuntime(movieDetails.runtime),
            spokenLanguages = movieDetails.spokenLanguages.map { it.name },
            productionCompanies = movieDetails.productionCompanies.map { it.name }
        )
    }

    private fun formatRuntime(minutes: Int): String {
        if (minutes <= 0) return ""
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return "${hours}h ${remainingMinutes}m"
    }

    private fun formatVoteCount(count: Int): String {
        if (count < 1000) return count.toString()
        val thousands = count / 1000.0
        return "${floor(thousands * 10).roundToInt() / 10.0}k"
    }

    fun checkIfMovieDetailsNotEmpty(movieDetails: FormattedMovieDetails): Boolean {
        return !(movieDetails.title.isBlank() == true && movieDetails.overview.isBlank() == true &&
                movieDetails.voteAverage.isBlank() == true && movieDetails.voteCount.isBlank() == true &&
                movieDetails.releaseYear.isBlank() == true && movieDetails.genres.isBlank() == true &&
                movieDetails.runtime.isBlank() == true && movieDetails.spokenLanguages.isEmpty() == true &&
                movieDetails.productionCompanies.isEmpty() == true)
    }
}