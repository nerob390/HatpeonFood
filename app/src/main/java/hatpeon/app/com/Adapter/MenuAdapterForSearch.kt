package hatpeon.app.com.Adapter


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hatpeon.app.com.BottomShit.CustomBottomSheetDialogFragment
import hatpeon.app.com.BottomShit.CustomBottomSheetSearchDialogFragment
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.R
import hatpeon.app.com.databinding.MenuChildBinding
import java.io.Serializable


class MenuAdapterForSearch (private val arrayList: ArrayList<Menu>, private val context: Context): RecyclerView.Adapter<MenuSearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MenuChildBinding.inflate(inflater, parent, false)
        return MenuSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuSearchViewHolder, position: Int) {
        val menu = arrayList[position]
        holder.binding.menuName.text=menu.name
        holder.binding.price.text=menu.unit_price+" Tk"
        holder.binding.offerPrice.text=menu.discount_price+" Tk"

        Picasso.get().load(menu.image).into(holder.binding.imageItem)
        if (TextUtils.isEmpty(menu.discount_price)){
            holder.binding.offerPrice.visibility=View.GONE
        }
        val data: Menu = arrayList[position]
        holder.binding.next.setOnClickListener {
/*            val activity = context as Activity
            dialog = CustomBottomSheetDialogFragment()
            dialog.show((activity as AppCompatActivity).supportFragmentManager, "tag")*/
            val bundle= Bundle()
            bundle.putSerializable("value", data as Serializable)
            val result= CustomBottomSheetSearchDialogFragment()
            result.arguments=bundle
           // it.findNavController().popBackStack()
            it.findNavController().navigate(R.id.navigation_search_bottom,bundle)
            val activity = context as Activity
         //   BottomSheetDialogFragment().show(activity as AppCompatActivity.supportFragmentManager, "appBottomSheet")
          //  BottomSheetDialogFragment().show((activity as AppCompatActivity).supportFragmentManager,"null")
/*            val action = DestinationFragmentDirections.actionCurrentFragmentToDestinationFragment()
            val navHostFragment =
                requireActivity().supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
            navHostFragment.navController.navigate(action)*/
          //  Navigation.findNavController(it).navigate(R.id.navigation_bottom);
           //Navigation.findNavController(it).navigate(R.id.action_global_final_nested_nav_graph)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}

class MenuSearchViewHolder(val binding: MenuChildBinding):RecyclerView.ViewHolder(binding.root) {

}