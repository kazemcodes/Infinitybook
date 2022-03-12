package org.ireader.domain.use_cases.download.delete

import org.ireader.core.utils.UiText
import org.ireader.domain.models.entities.Book
import org.ireader.domain.repository.DownloadRepository
import org.ireader.domain.utils.Resource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class DeleteAllSavedDownload @Inject constructor(private val downloadRepository: DownloadRepository) {
    suspend operator fun invoke() {
        try {
            downloadRepository.deleteAllSavedDownload()
        } catch (e: IOException) {
            Resource.Error<Resource<List<Book>>>(
                uiText = UiText.ExceptionString(e)
            )
        } catch (e: HttpException) {
            Resource.Error<Resource<List<Book>>>(
                uiText = UiText.ExceptionString(e)
            )
        } catch (e: Exception) {
            Resource.Error<Resource<List<Book>>>(
                uiText = UiText.ExceptionString(e)
            )
        }


    }
}