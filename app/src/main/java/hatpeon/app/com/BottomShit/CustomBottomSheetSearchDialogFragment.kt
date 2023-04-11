package hatpeon.app.com.BottomShit

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Fragment.RestaurantsView
import hatpeon.app.com.Fragment.Search.SearchResturantView
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentCustomBottomSheetDialogBinding


class CustomBottomSheetSearchDialogFragment():
    BottomSheetDialogFragment() {
    lateinit var binding:FragmentCustomBottomSheetDialogBinding
    var database: CartDatabase? = null
    var menu: Menu = Menu()
    var cartList: List<Cart> = ArrayList()
    var totalPrice = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCustomBottomSheetDialogBinding.inflate(inflater,container,false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)

        var bundle :Bundle ?=this.arguments
        if(bundle != null) {
            menu = bundle.getSerializable("value") as Menu
            binding.restaurantName.text=menu.name
            binding.price.text="Tk "+menu.unit_price
            Picasso.get().load(menu.image).into(binding.imageview)
            binding.location.text=SearchResturantView.restaurant.address

        }

        database = CartDatabase.getInstance(requireContext()!!.applicationContext)

        binding.addToCart.setOnClickListener {
            dismiss()
            val value: Long
            val cart = Cart( menu.id.toInt(),menu.name,SearchResturantView.resid,menu.discount_price,"",menu.unit_price,binding.quantity.text.toString(),"","")
            value = database!!.getData().insertCart(cart)
            Log.e("Save", "Success$value")
            cartList = database!!.getData().cart
            SearchResturantView.cartCount.text=cartList.size.toString()
            SearchResturantView.cartDialog.visibility=View.VISIBLE

            for (i in 0..cartList.size-1){
                totalPrice+= cartList.get(i).price.toDouble()*cartList.get(i).quantity.toDouble()
                Log.e("TotalPrice",totalPrice.toString())

            }
            SearchResturantView.totalPriceText.text="Tk "+totalPrice.toString()
        }
        binding.add.setOnClickListener {
            var intAmount: Int = binding.quantity.text.toString().toInt()
            intAmount++
            binding.quantity.text=intAmount.toString()
        }
        binding.minius.setOnClickListener {
            var intAmount: Int = binding.quantity.text.toString().toInt()
            if (intAmount  < 2) {
                val snackbar = Snackbar.make(it, "Wrong Action....!", Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
            }else{
                intAmount--
                binding.quantity.text=intAmount.toString()
            }


        }

        //val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
/*        binding.click.setOnClickListener {
           findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_home)
        }*/
        return binding.root
    }

}