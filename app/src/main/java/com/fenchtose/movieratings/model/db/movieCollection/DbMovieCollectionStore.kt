package com.fenchtose.movieratings.model.db.movieCollection

import android.support.annotation.WorkerThread
import com.fenchtose.movieratings.model.Movie
import com.fenchtose.movieratings.model.MovieCollection
import com.fenchtose.movieratings.model.MovieCollectionEntry
import com.fenchtose.movieratings.model.db.dao.MovieCollectionDao
import io.reactivex.Observable

class DbMovieCollectionStore(private val dao: MovieCollectionDao) : MovieCollectionStore {
    override fun createCollection(name: String): Observable<MovieCollection> {
        return Observable.defer {
            Observable.just(name)
                    .map {
                        name -> MovieCollection.create(name)
                    }
                    .doOnNext {
                        dao.insert(it)
                    }
        }
    }

    override fun addMovieToCollection(collection: MovieCollection, movie: Movie): Observable<MovieCollectionEntry> {
        return Observable.defer {
            Observable.just(MovieCollectionEntry.create(collection, movie))
                    .doOnNext {
                        dao.insert(it)
                    }
        }
    }

    override fun isMovieAddedToCollection(collection: MovieCollection, movie: Movie): Observable<Boolean> {
        return Observable.defer {
            Observable.just(dao.isMovieAddedToCollection(collection.id, movie.imdbId))
        }
    }

    @WorkerThread
    override fun apply(movie: Movie) {
        movie.collections = dao.getCollectionsForMovie(movie.imdbId).sortedBy { it.name }
        movie.appliedPreferences.collections = true
    }
}