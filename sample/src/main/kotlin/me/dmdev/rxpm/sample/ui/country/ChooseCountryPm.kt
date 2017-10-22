package me.dmdev.rxpm.sample.ui.country

import me.dmdev.rxpm.sample.CountryChosenMessage
import me.dmdev.rxpm.sample.ui.base.ScreenPresentationModel
import me.dmdev.rxpm.sample.ui.country.ChooseCountryPm.Mode.SEARCH_CLOSED
import me.dmdev.rxpm.sample.ui.country.ChooseCountryPm.Mode.SEARCH_OPENED
import me.dmdev.rxpm.sample.util.Country
import me.dmdev.rxpm.sample.util.PhoneUtil
import me.dmdev.rxpm.widget.inputControl
import java.util.concurrent.TimeUnit



class ChooseCountryPm(private val phoneUtil: PhoneUtil) : ScreenPresentationModel() {

    val countries = State<List<Country>>()
    val mode = State(SEARCH_CLOSED)
    val searchQuery = inputControl()

    override val backAction = Action<Unit>()

    val clearAction = Action<Unit>()
    val openSearchAction = Action<Unit>()
    val countryClicks = Action<Country>()

    override fun onCreate() {
        super.onCreate()

        openSearchAction.observable
                .map { SEARCH_OPENED }
                .subscribe(mode.consumer)
                .untilDestroy()

        clearAction.observable
                .subscribe {
                    if (searchQuery.text.value.isEmpty()) {
                        mode.consumer.accept(SEARCH_CLOSED)
                    } else {
                        searchQuery.text.consumer.accept("")
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

        searchQuery.text.observable
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

    enum class Mode { SEARCH_OPENED, SEARCH_CLOSED }

}