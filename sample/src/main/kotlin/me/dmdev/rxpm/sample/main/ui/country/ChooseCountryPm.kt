package me.dmdev.rxpm.sample.main.ui.country

import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.main.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.ui.country.ChooseCountryPm.Mode.*
import me.dmdev.rxpm.sample.main.util.*
import me.dmdev.rxpm.widget.*
import java.util.concurrent.*


class ChooseCountryPm(private val phoneUtil: PhoneUtil) : ScreenPresentationModel() {

    enum class Mode { SEARCH_OPENED, SEARCH_CLOSED }

    val countries = state<List<Country>>()
    val mode = state(SEARCH_CLOSED)
    val searchQueryInput = inputControl()

    override val backAction = action<Unit>()

    val clearAction = action<Unit>()
    val openSearchAction = action<Unit>()
    val countryClicks = action<Country>()

    override fun onCreate() {
        super.onCreate()

        openSearchAction.observable
            .map { SEARCH_OPENED }
            .subscribe(mode.consumer)
            .untilDestroy()

        clearAction.observable
            .subscribe {
                if (searchQueryInput.text.value.isEmpty()) {
                    mode.consumer.accept(SEARCH_CLOSED)
                } else {
                    searchQueryInput.text.consumer.accept("")
                }
            }
            .untilDestroy()

        backAction.observable
            .subscribe {
                if (mode.value == SEARCH_OPENED) {
                    mode.consumer.accept(SEARCH_CLOSED)
                } else {
                    super.backAction.consumer.accept(Unit)
                }
            }
            .untilDestroy()

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
            .subscribe(countries.consumer)
            .untilDestroy()

        countryClicks.observable
            .subscribe {
                sendMessage(CountryChosenMessage(it))
            }
            .untilDestroy()
    }
}