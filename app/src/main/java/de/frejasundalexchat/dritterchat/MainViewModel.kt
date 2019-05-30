package de.frejasundalexchat.dritterchat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val menuFragment = MutableLiveData<MenuFragment>()
    val showAddBookFragment = MutableLiveData<Unit>()

    init {
        menuFragment.value = MenuFragment.CurrentReadingCycle
    }

    fun menuItemClicked(itemId: Int): Boolean {
        return when (itemId) {
            R.id.add -> {
                showAddBookFragment()
                false
            }
            else -> {
                switchFragment(itemId)
                true
            }
        }
    }

    private fun showAddBookFragment() {
        showAddBookFragment.value = Unit
    }

    private fun switchFragment(itemId: Int) {
        val new = when (itemId) {
            R.id.current -> MenuFragment.CurrentReadingCycle
            R.id.stats -> MenuFragment.Stats
            R.id.all -> MenuFragment.BookOverview
            R.id.self -> MenuFragment.Profile
            else -> throw IllegalArgumentException("cant handle menuItem $itemId")
        }
        if (menuFragment.value != new) menuFragment.value = new
    }
}

sealed class MenuFragment {
    object CurrentReadingCycle : MenuFragment()
    object Profile : MenuFragment()
    object AddBook : MenuFragment()
    object BookOverview : MenuFragment()
    object Stats : MenuFragment()
}