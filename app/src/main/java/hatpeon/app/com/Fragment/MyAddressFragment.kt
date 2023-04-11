package hatpeon.app.com.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentMyAddressBinding


class MyAddressFragment : Fragment() {
lateinit var binding:FragmentMyAddressBinding
lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController=findNavController()
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (true) {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_profile)
                } else  NavHostFragment.findNavController(this@MyAddressFragment).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )
        // Inflate the layout for this fragment
        binding= FragmentMyAddressBinding.inflate(inflater,container,false)

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }


}