

package org.ireader.core_api.source

import org.ireader.core_api.source.model.FilterList
import org.ireader.core_api.source.model.Listing
import org.ireader.core_api.source.model.MangasPageInfo

interface CatalogSource : Source {

    override val lang: String

    suspend fun getMangaList(sort: Listing?, page: Int): MangasPageInfo

    suspend fun getMangaList(filters: FilterList, page: Int): MangasPageInfo

    fun getListings(): List<Listing>

    fun getFilters(): FilterList
}