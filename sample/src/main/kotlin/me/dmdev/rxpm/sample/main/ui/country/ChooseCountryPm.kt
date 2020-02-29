package me.dmdev.rxpm.sample.main.ui.country

import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.main.AppNavigationMessage.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.ui.country.ChooseCountryPm.Mode.*
import me.dmdev.rxpm.sample.main.util.*
import me.dmdev.rxpm.widget.*
import java.util.concurrent.*


class ChooseCountryPm(private val phoneUtil: PhoneUtil) : ScreenPresentationModel() {

    enum class Mode { SEARCH_OPENED, SEARCH_CLOSED }

    val searchQueryInput = inputControl()
    val mode = state(SEARCH_CLOSED)

    val countries = state<List<Country>> {
        searchQueryInput.text.observable
            .debounce(100, TimeUnit.MILLISECONDS)
            .map { query ->
                val regex = "${query.toLowerCase()}.*".toRegex()
                phoneUtil.countries()
                    .filter { it.name.toLowerCase().matches(regex) }
                    .sortedWith(Comparator { c1, c2 ->
                        compareValues(c1.name.toLowerCase(), c2.name.toLowerCase())
                    })
            }
    }

    override val backAction = action<Unit> {
        this.doOnNext {
            if (mode.value == SEARCH_OPENED) {
                mode.accept(SEARCH_CLOSED)
            } else {
                super.backAction.accept(Unit)
            }
        }
    }

    val clearClicks = action<Unit> {
        this.doOnNext {
            if (searchQueryInput.text.value.isEmpty()) {
                mode.accept(SEARCH_CLOSED)
            } else {
                searchQueryInput.text.accept("")
            }
        }
    }

    val openSearchClicks = action<Unit> {
        this.map { SEARCH_OPENED }
            .doOnNext(mode.consumer)
    }

    val countryClicks = action<Country> {
        this.doOnNext {
            sendMessage(CountryChosen(it))
        }
    }
}