package me.dmdev.rxpm.sample.ui.country

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_country.view.*
import me.dmdev.rxpm.sample.R
import me.dmdev.rxpm.sample.extensions.inflate
import me.dmdev.rxpm.sample.util.Country


class CountriesAdapter(
        private var countries: List<Country>?,
        private val itemClickListener: (country: Country) -> Unit
) : RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    fun setData(countries: List<Country>) {
        this.countries = countries
        notifyDataSetChanged()
    }

    override fun getItemCount() = countries?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_country))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(countries!![position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var country: Country

        init {
            itemView.setOnClickListener {
                itemClickListener.invoke(country)
            }
        }

        fun bind(country: Country) {
            this.country = country
            itemView.countryName.text = country.name
            itemView.countryCode.text = "+${country.countryCallingCode}"
        }
    }
}
