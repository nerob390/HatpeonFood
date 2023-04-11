package hatpeon.app.com.Adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Fragment.RestaurantsView
import hatpeon.app.com.Fragment.Search.SearchResturantView
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.RestaurantsSearchChildBinding
import java.io.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchRestaurantsAdapter (var context: Context, var contactList: List<Restaurant>, var contactListFiltered: List<Restaurant>, val searchListAdapter: SearchListAdapter): Filterable,RecyclerView.Adapter<SearchRestaurantsAdapter.ViewHolder>(){
    companion object{
        var sl:Int?=null
        private val contactListFiltered2: List<Restaurant>? = null
        lateinit var listener: SearchListAdapter
        var searchString = ""
        lateinit var data:Restaurant
        var database: CartDatabase? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RestaurantsSearchChildBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: SearchRestaurantsAdapter.ViewHolder, position: Int) {
        val list=contactListFiltered.get(position)
        sl=position+1
        contactListFiltered2 !=contactList
        listener =searchListAdapter
        holder.bind(context,list,position)



    }
    class ViewHolder(private val binding: RestaurantsSearchChildBinding): RecyclerView.ViewHolder(binding.root) {
        private lateinit var item : Restaurant

        fun bind(context: Context,ClientListData : Restaurant,position: Int){
            this.item= ClientListData
            binding.restaruentName.text=item.name
            binding.description.text=item.description
            binding.location.text=item.address
            Glide.with(context).
            load(item.image)
                .timeout(60000)
                /*   .placeholder(R.drawable.hatpeon)*/
                .into(binding.image);
           // Picasso.get().load(item.image).into(binding.image)
            data = item

            binding.voucher.text="VOUCHER : "+item.coupons

            /*TimeCalculation*/
            try {
                val startTime = item.opening_time
                val endTime = item.closing_time
                val format = SimpleDateFormat("h:m a")
                val date2 = format.parse(startTime)
                val currentTimeNow = SimpleDateFormat("h:m a", Locale.getDefault()).format(Date())
                val current = format.parse(currentTimeNow.toString())
                val difference = current.time - date2.time
                val differenceSeconds = difference / 1000 % 60
                val differenceMinutes = difference / (60 * 1000) % 60
                val differenceHours = difference / (60 * 60 * 1000) % 24
                val differenceDays = difference / (24 * 60 * 60 * 1000)
                println("$differenceDays days, ")
                println("$differenceHours hours, ")
                println("$differenceMinutes minutes, ")
                println("$differenceSeconds seconds.")
                val currentTime = SimpleDateFormat("h:m a", Locale.getDefault()).format(Date())
                val df = SimpleDateFormat("h:m a", Locale.getDefault())
                val currentTime_ = df.parse(currentTime.toString())
                val sdf: DateFormat = SimpleDateFormat("h:m a")
                val endTime_ = sdf.parse(endTime)
                val startTime_ = sdf.parse(startTime)
                if (currentTime_>endTime_||currentTime_<startTime_){
                    binding.openLayout.visibility= View.VISIBLE
                    binding.openAt.text="$differenceHours hr $differenceMinutes min".replace("-","")
                }else{
                    binding.openLayout.visibility= View.GONE
                }
                binding.next.setOnClickListener {
                    if (currentTime_>endTime_||currentTime_<startTime_){
                        Toast.makeText(context,"This Restaurant close now", Toast.LENGTH_SHORT).show()
                    }else{
                        database = CartDatabase.getInstance(context.applicationContext)
                        database!!.getData().deleteCartData()
                        val bundle= Bundle()
                        bundle.putSerializable("value", item as Serializable)
                        bundle.putString("id", item.id)
                        Log.e("searchId", item.id)
                        val result= SearchResturantView()
                        result.arguments=bundle
                        it.findNavController().navigate(R.id.navigation_restaruant,bundle)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (binding.voucher.text.equals("VOUCHER : ")){
                binding.voucher.visibility= View.GONE
            }else{
                binding.voucher.visibility= View.VISIBLE
            }
/*            binding.next.setOnClickListener {
                val bundle= Bundle()
                bundle.putSerializable("value", item as Serializable)
                bundle.putString("id", item.id)
                Log.e("searchId", item.id)
                val result= SearchResturantView()
                result.arguments=bundle
              //  it.findNavController().popBackStack()
                it.findNavController().navigate(R.id.navigation__search_res,bundle)

            }*/

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
                    val resultList = ArrayList<Restaurant>()
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
                contactListFiltered = results.values as ArrayList<Restaurant>
                notifyDataSetChanged()
            }
        }
    }
    interface SearchListAdapter {
        fun onContactSelected(contact: Restaurant)
    }

    override fun getItemCount(): Int {
        return contactListFiltered.size
    }
}