package me.dmdev.rxpm.sample.main.ui.country

import android.os.*
import android.view.*
import androidx.recyclerview.widget.*
import com.jakewharton.rxbinding3.view.*
import kotlinx.android.synthetic.main.screen_choose_country.*
import me.dmdev.rxpm.*
import me.dmdev.rxpm.sample.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.main.extensions.*
import me.dmdev.rxpm.sample.main.ui.base.*
import me.dmdev.rxpm.sample.main.ui.country.ChooseCountryPm.*
import me.dmdev.rxpm.widget.*


class ChooseCountryScreen : Screen<ChooseCountryPm>() {

    private val countriesAdapter = CountriesAdapter(null) { country ->
        country passTo presentationModel.countryClicks
    }

    override val screenLayout = R.layout.screen_choose_country

    override fun providePresentationModel() = ChooseCountryPm(App.component.phoneUtil)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = countriesAdapter
        }
    }

    override fun onBindPresentationModel(pm: ChooseCountryPm) {
        super.onBindPresentationModel(pm)

        pm.mode bindTo {
            if (it == Mode.SEARCH_OPENED) {
                toolbarTitle.visible(false)
                searchQueryEdit.visible(true)
                searchQueryEdit.showKeyboard()
                searchButton.visible(false)
                clearButton.visible(true)
            } else {
                toolbarTitle.visible(true)
                searchQueryEdit.visible(false)
                searchQueryEdit.hideKeyboard()
                searchButton.visible(true)
                clearButton.visible(false)
            }
        }

        pm.searchQueryInput bindTo searchQueryEdit
        pm.countries bindTo { countriesAdapter.setData(it) }

        searchButton.clicks() bindTo pm.openSearchClicks
        clearButton.clicks() bindTo pm.clearClicks
        navButton.clicks() bindTo pm.backAction
    }
}
