package hatpeon.app.com.Fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hatpeon.app.com.Adapter.CartAdapter
import hatpeon.app.com.Adapter.RestaurantAdapter
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Fragment.Search.SearchResturantView
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentCartBinding
import java.io.Serializable


class CartFragment : Fragment() {
    lateinit var binding:FragmentCartBinding
    lateinit var navController: NavController
    var database: CartDatabase? = null
    var cartList: List<Cart> = ArrayList()
    var totalPricefromcart = 0.0
    var restaurant: Restaurant = Restaurant()
    companion object{
        lateinit var totalPriceText: TextView
        lateinit var recyclerView: RecyclerView
        lateinit var cartDialog: LinearLayout
        var cartAdapter: CartAdapter? = null

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      /*  navController=findNavController()
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (true) {
                    var bundle :Bundle ?=this@CartFragment.arguments
                    if(bundle != null) {
                        restaurant = bundle.getSerializable("value") as Restaurant
                    }
                    val args=this@CartFragment.arguments
                    if (args?.get("from").toString().equals("search")){
*//*                        var bundle :Bundle ?=this@CartFragment.arguments
                        if(bundle != null) {
                            restaurant = bundle.getSerializable("value") as Restaurant
                        }*//*
                        val bundleres=Bundle()
                        bundleres.putString("id",bundle?.get("id").toString())
                        bundleres.putSerializable("value", restaurant as Serializable)
                        navController.popBackStack()
                        navController.navigate(R.id.navigation__search_res,bundleres)
                    }else{
*//*                        var bundle :Bundle ?=this@CartFragment.arguments
                        if(bundle != null) {
                         restaurant = bundle.getSerializable("value") as Restaurant
                        }*//*
                        val bundleres=Bundle()
                        bundleres.putString("id",bundle?.get("id").toString())
                        bundleres.putSerializable("value", restaurant as Serializable)
                        navController.popBackStack()
                        Log.e("ID",bundle?.get("id").toString())
                        navController.navigate(R.id.navigation_restaruant,bundleres)
                    }

                } else  NavHostFragment.findNavController(this@CartFragment).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )*/
        // Inflate the layout for this fragment
        var win: Window = requireActivity().window
        win.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding= FragmentCartBinding.inflate(inflater,container,false)
        cartDialog=binding.cartDialog
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24);
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        val args=this@CartFragment.arguments
        totalPriceText=binding.totalPrice
        database = CartDatabase.getInstance(requireContext()!!.applicationContext)
        cartList = database!!.getData().cart

        for (i in 0..cartList.size-1){
            totalPricefromcart+= cartList.get(i).price.toDouble()*cartList.get(i).quantity.toDouble()
        }
        binding.totalPrice.text="Tk "+database!!.getData().sum().toString()
        binding.cartItem.text=cartList.size.toString()+" Item in the cart"
        recyclerView=binding.recyclerView
        binding.goToCheckOut.setOnClickListener {
            //var bundlecart :Bundle ?=this@CartFragment.arguments
//            if(bundlecart != null) {
//                restaurant = bundlecart.getSerializable("value") as Restaurant
//            }
            val bundle=Bundle()
           // findNavController().popBackStack()
            //bundle.putString("from",args?.get("from").toString())
            bundle.putString("id", args?.get("id").toString())
            //bundle.putSerializable("value", restaurant as Serializable)
            findNavController().navigate(R.id.navigation_checkOut,bundle)
        }
        var arraylist = ArrayList(cartList)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(arraylist,requireContext(),binding.recyclerView)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = cartAdapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemAnimator(null)
        binding.recyclerView.setItemViewCacheSize(100);
        return binding.root
    }


}