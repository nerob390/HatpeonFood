package hatpeon.app.com.Adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hatpeon.app.com.BottomShit.CustomBottomSheetDialogFragment
import hatpeon.app.com.BottomShit.SearchBottomSheetDialogFragment
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.RestaurantsItemSearchChildBinding
import hatpeon.app.com.databinding.RestaurantsSearchChildBinding
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class SearchRestaurantsItemAdapter (var context: Context, var contactList: List<Menu>, var contactListFiltered: List<Menu>, val searchListAdapter: SearchListAdapter): Filterable,RecyclerView.Adapter<SearchRestaurantsItemAdapter.ViewHolder>(){
    companion object{
        var sl:Int?=null
        private val contactListFiltered2: List<Restaurant>? = null
        lateinit var listener: SearchListAdapter
        var searchString = ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RestaurantsItemSearchChildBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: SearchRestaurantsItemAdapter.ViewHolder, position: Int) {
        val list=contactListFiltered.get(position)
        sl=position+1
        contactListFiltered2 !=contactList
        listener =searchListAdapter
        holder.bind(context,list,position)




    }
    class ViewHolder(private val binding: RestaurantsItemSearchChildBinding): RecyclerView.ViewHolder(binding.root) {
        private lateinit var item : Menu

        fun bind(context: Context,meenuListData : Menu,position: Int){
            this.item= meenuListData
            binding.restaruentName.text=item.name
            binding.price.text="Tk "+item.unit_price
            binding.offerPrice.text="Tk "+item.discount_price
            Glide.with(context).
            load(item.image)
                .timeout(60000)
                /*   .placeholder(R.drawable.hatpeon)*/
                .into(binding.image);
            val data: Menu = meenuListData
            binding.next.setOnClickListener {
                val bundle= Bundle()
                bundle.putSerializable("value", data as Serializable)
                bundle.putString("id", item.id)
                val result= SearchBottomSheetDialogFragment()
                result.arguments=bundle
                it.findNavController().navigate(R.id.navigation_bottom_search,bundle)
            }


            listener.onContactSelected(item)
            val country: String = item.name.toLowerCase(Locale.getDefault())
            if (country.contains(searchString)) {
                //Log.e("test", country.toString() + " contains: " + searchString)
                val startPos: Int = country.indexOf(searchString)
                val endPos: Int = startPos + searchString.length
                val spanText = Spannable.Factory.getInstance()
                    .newSpannable(binding.restaruentName.getText()) // <- EDITED: Use the original string, as `country` has been converted to lowercase.
                spanText.setSpan(
                    ForegroundColorSpan(Color.RED),
                    startPos,
                    endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.restaruentName.setText(spanText, TextView.BufferType.SPANNABLE)
            }

        }
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {
                    contactListFiltered = contactList!!
                } else {
                    val resultList = ArrayList<Menu>()
                    Log.e("charSearch",charSearch)
                    for (row in contactList!!) {
                        searchString=charSearch
                        if (contactList.size==0){
                            Log.e("result","NotFound")
                        }
                        if (row.name!!.toLowerCase().toUpperCase().contains(charSearch.toLowerCase().toUpperCase())) {

                            Log.e("MatchData",row.name)
                            resultList.add(row)
                        }else if (row.name!!.toLowerCase().toUpperCase().contains(charSearch.toLowerCase().toUpperCase())){
                            resultList.add(row)
                        }
                    }
                    contactListFiltered = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                contactListFiltered = results.values as ArrayList<Menu>
                notifyDataSetChanged()
            }
        }
    }
    interface SearchListAdapter {
        fun onContactSelected(contact: Menu)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }
}