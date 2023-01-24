package com.lagradost.cloudstream3.syncproviders.providers

import androidx.fragment.app.FragmentActivity
import com.lagradost.cloudstream3.AcraApplication
import com.lagradost.cloudstream3.R
import com.lagradost.cloudstream3.syncproviders.AuthAPI
import com.lagradost.cloudstream3.syncproviders.SyncAPI
import com.lagradost.cloudstream3.syncproviders.SyncIdName
import com.lagradost.cloudstream3.ui.WatchType
import com.lagradost.cloudstream3.ui.result.txt
import com.lagradost.cloudstream3.utils.Coroutines.ioWork
import com.lagradost.cloudstream3.utils.DataStoreHelper.getAllWatchStateIds
import com.lagradost.cloudstream3.utils.DataStoreHelper.getBookmarkedData
import com.lagradost.cloudstream3.utils.DataStoreHelper.getResultWatchState

class LocalList : SyncAPI {
    override val name = "Local"
    override val icon: Int = R.drawable.ic_baseline_storage_24
    override val requiresLogin = false
    override val createAccountUrl: Nothing? = null
    override val idPrefix = "local"
    override var requireLibraryRefresh = true

    override fun loginInfo(): AuthAPI.LoginInfo {
        return AuthAPI.LoginInfo(
            null,
            null,
            0
        )
    }

    override fun logOut() {

    }

    override val key: String = ""
    override val redirectUrl = ""
    override suspend fun handleRedirect(url: String): Boolean {
        return true
    }

    override fun authenticate(activity: FragmentActivity?) {
    }

    override val mainUrl = ""
    override val syncIdName = SyncIdName.LocalList
    override suspend fun score(id: String, status: SyncAPI.SyncStatus): Boolean {
        return true
    }

    override suspend fun getStatus(id: String): SyncAPI.SyncStatus? {
        return null
    }

    override suspend fun getResult(id: String): SyncAPI.SyncResult? {
        return null
    }

    override suspend fun search(name: String): List<SyncAPI.SyncSearchResult>? {
        return null
    }

    override suspend fun getPersonalLibrary(): SyncAPI.LibraryMetadata? {
        val watchStatusIds = ioWork {
            getAllWatchStateIds()?.map { id ->
                Pair(id, getResultWatchState(id))
            }
        }?.distinctBy { it.first } ?: return null

        val list = ioWork {
            watchStatusIds.mapNotNull {
                getBookmarkedData(it.first)?.toLibraryItem(it.second)
            }
        }

        return SyncAPI.LibraryMetadata(
            WatchType.values().mapNotNull {
                // None is not something to display
                if (it == WatchType.NONE) return@mapNotNull null

                // Dirty hack for context!
                txt(it.stringRes).asStringNull(AcraApplication.context)
            },
            list
        )
    }

    override fun getIdFromUrl(url: String): String {
        return url
    }
}