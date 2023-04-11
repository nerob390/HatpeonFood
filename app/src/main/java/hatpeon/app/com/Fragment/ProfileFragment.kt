package hatpeon.app.com.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
lateinit var binding:FragmentProfileBinding
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController=findNavController()
        // Inflate the layout for this fragment
        binding= FragmentProfileBinding.inflate(inflater,container,false)
        val sharedPreference= requireContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        binding.name.text=sharedPreference.getString("name","")
        binding.email.text=sharedPreference.getString("email","")
        binding.phone.text=sharedPreference.getString("phone","")
        Picasso.get().load(sharedPreference.getString("image","")).placeholder(R.drawable.profile).into(binding.image)
            binding.back.setOnClickListener {
/*               findNavController().popBackStack()
               findNavController().navigate(R.id.navigation_home)*/
                findNavController().navigate(R.id.action_navigation_profile_to_navigation_home2)
           //     navController.popBackStack(R.id.action_navigation_profile_to_navigation_home, true)
            }

        binding.edtProfile.setOnClickListener {
            //findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_profileEdit)
        }
        binding.passwordChange.setOnClickListener {
           // findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_change_password)
        }
        binding.myAddress.setOnClickListener {
           // findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_my_address)
        }
        return binding.root
    }
}