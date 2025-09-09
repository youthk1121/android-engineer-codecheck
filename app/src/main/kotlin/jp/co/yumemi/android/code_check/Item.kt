package jp.co.yumemi.android.code_check

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Item(
    val name: String,
    val detail: RepositoryDetail,
    val fetchDate: Date
) : Parcelable