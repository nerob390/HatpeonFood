package hatpeon.app.com.Fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.CuisineAdapter
import hatpeon.app.com.MVVVM.CuisineViewModel
import hatpeon.app.com.Model.Cuisine
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentCuisineFramentBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.lang.RuntimeException

class CuisineFrament : Fragment() {
    lateinit var binding:FragmentCuisineFramentBinding
    lateinit var navController: NavController
    lateinit var viewModel: CuisineViewModel
    lateinit var cuisineAdapter: CuisineAdapter
    val retService = Rettrofit.getInstance().create(Services::class.java)
    val cuisine:ArrayList<Cuisine> = ArrayList()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel= ViewModelProvider(requireActivity()).get(CuisineViewModel::class.java)
        viewModel.myLiveData(binding.progress).observe(requireActivity(), Observer {
            try {
                cuisineAdapter= CuisineAdapter(it,requireContext())
                binding.cuisineRecycler.adapter=cuisineAdapter
            }catch (e:NullPointerException){
                e.toString()
            }catch (e: IllegalStateException){
                e.printStackTrace()
            }catch (r: RuntimeException){
                r.printStackTrace()
            }


        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var win: Window = requireActivity().window
        win.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        navController=findNavController()
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (true) {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_home)
                } else  NavHostFragment.findNavController(this@CuisineFrament).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )
        // Inflate the layout for this fragment
        binding= FragmentCuisineFramentBinding.inflate(inflater,container,false)
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

       //  Cuisine()
        val layoutManager = GridLayoutManager(requireContext(),2)
        binding.cuisineRecycler.layoutManager=layoutManager



        return  binding.root

    }

    fun Cuisine(){
        retService.cuisine().enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val response=response.body()
                        val jsonObject = JSONObject(response.toString())
                        Log.e("obj",jsonObject.toString())
                        val dataObj=jsonObject.getJSONObject("data")
                        val dataArray=dataObj.getJSONArray("data")
                        for (i in 0..dataArray.length()-1){
                            val cuiObj=dataArray.getJSONObject(i)
                            cuisine.add(
                                Cuisine(
                                    cuiObj.getString("id"),
                                    cuiObj.getString("name"),
                                    cuiObj.getString("slug"),
                                    cuiObj.getString("image"),
                                    cuiObj.getString("description")
                                )
                            )

                        }
                        try {

                            val layoutManager = GridLayoutManager(requireContext(),2)
                            binding.cuisineRecycler.adapter= CuisineAdapter(cuisine,requireContext())
                            binding.cuisineRecycler.layoutManager=layoutManager

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
    }
}