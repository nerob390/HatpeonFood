package hatpeon.app.com.Fragment

import android.app.SearchManager
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.CuisineAdapter
import hatpeon.app.com.Adapter.MenuAdapterForSearch
import hatpeon.app.com.Adapter.RestaurantAdapter
import hatpeon.app.com.Adapter.SearchRestaurantsAdapter
import hatpeon.app.com.MVVVM.CuisineViewModel
import hatpeon.app.com.MVVVM.RestaurantsSearchViewModel
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.databinding.FragmentRestauransSearchBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.lang.RuntimeException


class RestauransSearchFragment :SearchRestaurantsAdapter.SearchListAdapter,Fragment()  {
    lateinit var binding:FragmentRestauransSearchBinding
    private var  searchList : ArrayList<Restaurant> = ArrayList()
    lateinit var searchAdapter : SearchRestaurantsAdapter
    val retService = Rettrofit.getInstance().create(Services::class.java)
    lateinit var viewModel: RestaurantsSearchViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentRestauransSearchBinding.inflate(inflater,container,false)

        searchAdapter = SearchRestaurantsAdapter(requireContext(),searchList,searchList,this)
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        binding.searchView.setMaxWidth(Int.MAX_VALUE)
        binding.searchView.setIconifiedByDefault(false)
//        val myCustomFont = Typeface.createFromAsset(requireContext().getAssets(), "fonts/roboto_light.ttf")
        binding.searchView.setQueryHint("Search for restaurants")

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
        viewModel= ViewModelProvider(requireActivity()).get(RestaurantsSearchViewModel::class.java)
        viewModel.myLiveData(binding.progress).observe(requireActivity(), Observer {
            try {
                searchAdapter= SearchRestaurantsAdapter(requireContext(),it,it,this@RestauransSearchFragment)
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
        //Search()

        return binding.root
    }

    override fun onContactSelected(contact: Restaurant) {

    }
/*    fun Search(){
        retService.search().enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("regObj",jsonObject.toString())
                        val dataObj=jsonObject.getJSONObject("data")
                        val dataArray=dataObj.getJSONArray("data")
                        for (i in 0..dataArray.length()-1){
                            val resObj=dataArray.getJSONObject(i)
                            searchList.add(
                                Restaurant(
                                    resObj.getString("id"),
                                    resObj.getString("food_type_id"),
                                    resObj.getString("name"),
                                    resObj.getString("description"),
                                    resObj.getString("address"),
                                    resObj.getString("image"),
                                    resObj.getString("avgRating"),
                                    resObj.getString("avgRatingUser")
                                )
                            )

                        }
                        try {
                            //Adapter
                            val layoutManager= LinearLayoutManager(requireContext())
                            searchAdapter= SearchRestaurantsAdapter(requireContext(),searchList,searchList,this@RestauransSearchFragment)
                            binding.recyclerView.layoutManager=layoutManager
                            binding.recyclerView.adapter=searchAdapter

                        }catch (e:NullPointerException){
                            e.toString()
                        }catch (e: IllegalStateException){
                            e.printStackTrace()
                        }catch (r: RuntimeException){
                            r.printStackTrace()
                        }


                    }


                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("Error",t.localizedMessage!!)
                }
            }

        )
    }*/

}