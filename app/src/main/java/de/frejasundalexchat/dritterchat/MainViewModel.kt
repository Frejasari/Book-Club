package de.frejasundalexchat.dritterchat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val menuFragment = MutableLiveData<MenuFragment>()

    init {
        menuFragment.value = MenuFragment.CurrentReadingCycle
    }

    fun menuItemClicked(itemId: Int) {
        val value = menuFragment.value
        val new = when (itemId) {
            R.id.current -> MenuFragment.CurrentReadingCycle
            R.id.stats -> MenuFragment.Stats
            R.id.all -> MenuFragment.BookOverview
            R.id.self -> MenuFragment.Profile
            else -> throw IllegalArgumentException("cant handle menuItem $itemId")
        }
        if (value != new) menuFragment.value = new
    }
}

sealed class MenuFragment {
    object CurrentReadingCycle : MenuFragment()
    object Profile : MenuFragment()
    object BookOverview : MenuFragment()
    object Stats : MenuFragment()
}