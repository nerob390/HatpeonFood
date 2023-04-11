package hatpeon.app.com.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.frammanagment.network.Rettrofit
import com.app.frammanagment.network.Services
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonObject
import hatpeon.app.com.Adapter.RestaurantAdapter
import hatpeon.app.com.Model.PagerItem
import hatpeon.app.com.Model.Restaurant
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentSearchBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment() {
    lateinit var binding:FragmentSearchBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    val list = ArrayList<PagerItem>()
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var win: Window = requireActivity().window
        win.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        navController=findNavController()
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (true) {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_home)
                } else  NavHostFragment.findNavController(this@SearchFragment).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )
        list.clear()
        // Inflate the layout for this fragment
        binding=FragmentSearchBinding.inflate(inflater,container,false)
        list.add(PagerItem("1","Restaurants", RestauransSearchFragment()))
        list.add(PagerItem("2","Restaurants Menu", RestaurantsItemSearch()))
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        viewPagerAdapter = ViewPagerAdapter(requireParentFragment(), list)
        Handler(Looper.getMainLooper()).post(Runnable {
            binding.pager.adapter = viewPagerAdapter
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = list[position].name
                //  Log.e("tabPosition",list[position].name)
            }.attach()

        })

        return binding.root
    }

    class ViewPagerAdapter(fragment: Fragment,val fragmentlist:ArrayList<PagerItem>) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = fragmentlist.size
        override fun createFragment(position: Int): Fragment {
            val fragment = fragmentlist[position].fragment
            fragment.arguments = Bundle().apply {
                putInt(fragmentlist[position].name,position)
            }
            return fragment
        }

    }

}