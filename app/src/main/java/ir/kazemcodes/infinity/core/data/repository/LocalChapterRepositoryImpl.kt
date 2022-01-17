package ir.kazemcodes.infinity.core.data.repository

import ir.kazemcodes.infinity.core.data.local.dao.LibraryChapterDao
import ir.kazemcodes.infinity.core.data.network.models.Source
import ir.kazemcodes.infinity.core.domain.models.Book
import ir.kazemcodes.infinity.core.domain.models.Chapter
import ir.kazemcodes.infinity.core.domain.models.ChapterEntity
import ir.kazemcodes.infinity.core.utils.Resource
import ir.kazemcodes.infinity.core.domain.repository.LocalChapterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class LocalChapterRepositoryImpl @Inject constructor(private val daoLibrary: LibraryChapterDao) :
    LocalChapterRepository {

    override suspend fun insertChapters(chapters: List<Chapter>, book : Book, inLibrary : Boolean, source : Source) {
        return daoLibrary.insertChapters(chapterEntities = chapters.map { it.copy(bookName = book.bookName,source=source.name, inLibrary = inLibrary).toChapterEntity() })
    }


    override suspend fun deleteLastReadChapter(
        bookName: String,
        source: String,
    ) {
        return daoLibrary.deleteLastReadChapter(bookName, source)
    }

    override suspend fun setLastReadChapter(
        bookName: String,
        chapterTitle: String,
        source: String,
    ) {
        return daoLibrary.setLastReadChapter(bookName = bookName, chapterTitle, source)
    }

    override fun getLastReadChapter(bookName: String, source: String): Flow<Resource<Chapter>> =
        flow {
            try {
                Timber.d("Timber: GetLocalChaptersByBookNameUseCase was Called")
                emit(Resource.Loading())
                daoLibrary.getLastReadChapter(bookName, source).collect { chapter ->
                    emit(Resource.Success<Chapter>(data = chapter.toChapter()))
                }
                Timber.d("Timber: GetLocalChaptersByBookNameUseCase was Finished Successfully")
            } catch (e: Exception) {
                emit(Resource.Error<Chapter>(message = e.message.toString()))
            }
        }


    override suspend fun updateChapter(
        readingContent: String,
        haveBeenRead: Boolean,
        bookName: String,
        chapterTitle: String,
        lastRead: Boolean,
        source: String,
    ) {
        return daoLibrary.updateChapter(readingContent = readingContent,
            bookName = bookName,
            chapterTitle = chapterTitle,
            haveBeenRead = haveBeenRead,
            lastRead = lastRead, source)
    }

    override suspend fun updateChapter(chapterEntity: ChapterEntity) {
        return daoLibrary.updateChapter(chapterEntity)
    }

    override suspend fun updateChapters(chapters: List<Chapter>) {
        return daoLibrary.updateChapters(chapters.map { it.toChapterEntity() })
    }

    override suspend fun updateAddToLibraryChapters(
        chapterTitle: String,
        source: String,
        bookName: String,
    ) {
        return daoLibrary.updateAddToLibraryChapters(chapterTitle, source, bookName)
    }

    override fun getChapterByName(bookName: String, source: String): Flow<Resource<List<Chapter>>> =
        flow {
            emit(Resource.Loading())
            try {
                Timber.d("Timber: GetLocalChaptersByBookNameUseCase was Called")
                emit(Resource.Loading())
                daoLibrary.getChapters(bookName, source)
                    .collect { chapters ->
                        emit(Resource.Success<List<Chapter>>(data = chapters.map { chapterEntity ->
                            chapterEntity.toChapter()
                        }))
                    }
                Timber.d("Timber: GetLocalChaptersByBookNameUseCase was Finished Successfully")
            } catch (e: Exception) {
                emit(Resource.Error<List<Chapter>>(message = e.message.toString()))
            }
        }


    override fun getAllChapter(): Flow<Resource<List<Chapter>>> = flow {
        try {
            Timber.d("Timber: GetLocalChaptersByBookNameUseCase was Called")
            emit(Resource.Loading())
            daoLibrary.getAllChapters()
                .collect { chapters ->
                    emit(Resource.Success<List<Chapter>>(data = chapters.map { chapterEntity ->
                        chapterEntity.toChapter()
                    }))
                }
            Timber.d("Timber: GetLocalChaptersByBookNameUseCase was Finished Successfully")
        } catch (e: Exception) {
            emit(Resource.Error<List<Chapter>>(message = e.message.toString()))
        }

    }


    override fun getChapterByChapter(
        chapterTitle: String,
        bookName: String,
        source: String,
    ): Flow<ChapterEntity?> {
        return daoLibrary.getChapterByChapter(chapterTitle, bookName, source)
    }


    override suspend fun deleteChapters(bookName: String, source: String) {
        return daoLibrary.deleteLocalChaptersByName(bookName = bookName, source)
    }

    override suspend fun deleteNotInLibraryChapters() {
        return daoLibrary.deleteLibraryChapters()
    }

    override suspend fun deleteAllChapters() {
        return daoLibrary.deleteAllChapters()
    }
}