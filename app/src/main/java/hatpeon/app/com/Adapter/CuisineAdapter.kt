package hatpeon.app.com.Adapter


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import hatpeon.app.com.Fragment.CuisineReataurants
import hatpeon.app.com.Fragment.RestaurantsView
import hatpeon.app.com.Model.Cuisine
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.CuisineChildBinding
import hatpeon.app.com.databinding.RestaurantChildBinding
import java.io.Serializable
import java.util.ArrayList


class CuisineAdapter (private val arrayList: ArrayList<Cuisine>, private val context: Context): RecyclerView.Adapter<CuisineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CuisineChildBinding.inflate(inflater, parent, false)
        return CuisineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CuisineViewHolder, position: Int) {
        val cuisine = arrayList[position]
        holder.binding.name.text=cuisine.name
        Glide.with(context).load(cuisine.image).into(holder.binding.image);
        val data: Cuisine = arrayList[position]
        holder.binding.next.setOnClickListener {
            val bundle= Bundle()
            bundle.putString("id",cuisine.id)
            bundle.putString("title",cuisine.name)
            bundle.putString("from","cuisine")
            bundle.putSerializable("value", data as Serializable)
            val result= CuisineReataurants()
            result.arguments=bundle
            //it.findNavController().popBackStack()
            it.findNavController().navigate(R.id.navigation_cuisine_restuarants,bundle)
        //    Navigation.findNavController(it).navigate(R.id.action_fifthFragment_to_sixthFragment)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}

class CuisineViewHolder(val binding: CuisineChildBinding):RecyclerView.ViewHolder(binding.root) {

}