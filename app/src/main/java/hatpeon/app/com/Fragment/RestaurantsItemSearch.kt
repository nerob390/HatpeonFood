package hatpeon.app.com.Fragment

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hatpeon.app.com.Adapter.SearchRestaurantsAdapter
import hatpeon.app.com.Adapter.SearchRestaurantsItemAdapter
import hatpeon.app.com.BottomShit.SearchBottomSheetDialogFragment
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.MVVVM.RestaurantsItemSearchViewModel
import hatpeon.app.com.MVVVM.RestaurantsSearchViewModel
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentRestaurantsItemSearchBinding
import java.io.Serializable
import java.lang.IllegalStateException
import java.lang.RuntimeException


class RestaurantsItemSearch : SearchRestaurantsItemAdapter.SearchListAdapter,Fragment() {
    lateinit var binding:FragmentRestaurantsItemSearchBinding
    private var  searchList : ArrayList<Menu> = ArrayList()
    lateinit var searchAdapter : SearchRestaurantsItemAdapter
    lateinit var viewModel: RestaurantsItemSearchViewModel
    var database: CartDatabase? = null
    var totalPrice = 0
    var cartList: List<Cart> = ArrayList()
    companion object{
        lateinit var cartDialog: LinearLayout
        lateinit var cartCount: TextView
        lateinit var totalPriceText: TextView
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentRestaurantsItemSearchBinding.inflate(inflater,container,false)

        searchAdapter = SearchRestaurantsItemAdapter(requireContext(),searchList,searchList,this)
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        binding.searchView.setMaxWidth(Int.MAX_VALUE)
        binding.searchView.setIconifiedByDefault(false)
//        val myCustomFont = Typeface.createFromAsset(requireContext().getAssets(), "fonts/roboto_light.ttf")
        binding.searchView.setQueryHint("Search for restaurants menu")
        cartDialog =binding.cartDialog
        cartCount =binding.cartItem
        totalPriceText =binding.totalPrice
        database = CartDatabase.getInstance(requireContext()!!.applicationContext)
        cartList = database!!.getData().cart
        Log.e("cartSize",cartList.size.toString())
        if (cartList.size>0){
            binding.cartItem.text=cartList.size.toString()
            binding.cartDialog.visibility=View.VISIBLE
            for (i in 0..cartList.size-1){
                totalPrice+= cartList.get(i).price.toInt()*cartList.get(i).quantity.toInt()
                Log.e("TotalPrice",totalPrice.toString())

            }
            binding.totalPrice.text="Tk "+totalPrice.toString()
        }
        binding.cartDialog.setOnClickListener {
            val bundle= Bundle()
            bundle.putString("from","search")
            val result= CartFragment()
            bundle.putString("id", SearchBottomSheetDialogFragment.resid)
            result.arguments=bundle
          //  findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_cart,bundle)
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                searchAdapter.getFilter().filter(p0)
                return false

            }

            override fun onQueryTextChange(query: String?): Boolean {
                // filter recycler view when text is changed
                Log.e("Search",query!!)
                searchAdapter.getFilter().filter(query)

                return false
            }
        })
        viewModel= ViewModelProvider(requireActivity()).get(RestaurantsItemSearchViewModel::class.java)
        viewModel.myLiveData(binding.progress).observe(requireActivity(), Observer {
            try {
                searchAdapter= SearchRestaurantsItemAdapter(requireContext(),it,it,this@RestaurantsItemSearch)
                binding.recyclerView.adapter=searchAdapter
                val layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.layoutManager=layoutManager
            }catch (e:NullPointerException){
                e.toString()
            }catch (e: IllegalStateException){
                e.printStackTrace()
            }catch (r: RuntimeException){
                r.printStackTrace()
            }


        })

/*        val layoutManager= LinearLayoutManager(requireContext())
        searchAdapter= SearchRestaurantsItemAdapter(requireContext(),searchList,searchList,this@RestaurantsItemSearch)
        binding.recyclerView.layoutManager=layoutManager
        binding.recyclerView.adapter=searchAdapter*/
        return binding.root
    }

    override fun onContactSelected(contact: Menu) {

    }

}