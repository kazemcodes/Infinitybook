package org.ireader.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.ireader.core.utils.Constants.ImageKeyTable

@Entity(tableName = ImageKeyTable)
data class BrowseRemoteKey(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    val previousPage: Int?,
    var nextPage: Int?,
    var lastUpdated: Long?,
)