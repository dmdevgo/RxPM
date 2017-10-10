package me.dmdev.rxpm.sample.ui.country

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.screen_choose_country.*
import me.dmdev.rxpm.sample.App
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.extensions.hideKeyboard
import me.dmdev.rxpm.sample.extensions.showKeyboard
import me.dmdev.rxpm.sample.extensions.visible
import me.dmdev.rxpm.sample.ui.base.Screen
import me.dmdev.rxpm.sample.ui.country.ChooseCountryPm.Mode

/**
 * @author Dmitriy Gorbunov
 */
class ChooseCountryScreen : Screen<ChooseCountryPm>() {

    private val countriesAdapter = CountriesAdapter(null) { country ->
        presentationModel.countryClicks.consumer.accept(country)
    }

    override val screenLayout = R.layout.screen_choose_country

    override fun providePresentationModel() = ChooseCountryPm(App.component.phoneUtil)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = countriesAdapter
        }
    }

    override fun onBindPresentationModel(pm: ChooseCountryPm) {
        super.onBindPresentationModel(pm)

        pm.mode.observable.bindTo {
            if (it == Mode.SEARCH_OPENED) {
                toolbarTitle.visible(false)
                searchQuery.visible(true)
                searchQuery.showKeyboard()
                searchButton.visible(false)
                clearButton.visible(true)
            } else {
                toolbarTitle.visible(true)
                searchQuery.visible(false)
                searchQuery.hideKeyboard()
                searchButton.visible(true)
                clearButton.visible(false)
            }
        }

        pm.searchQuery.bindTo(searchQuery)
        pm.countries.observable.bindTo { countriesAdapter.setData(it) }

        searchButton.clicks().bindTo(pm.openSearchAction.consumer)
        clearButton.clicks().bindTo(pm.clearAction.consumer)
        navButton.clicks().bindTo(pm.backAction.consumer)
    }
}
