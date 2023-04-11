package hatpeon.app.com.BottomShit

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Fragment.RestaurantsItemSearch
import hatpeon.app.com.Fragment.RestaurantsView
import hatpeon.app.com.Fragment.Search.SearchResturantView
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentCustomBottomSheetDialogBinding


class SearchBottomSheetDialogFragment():
    BottomSheetDialogFragment() {
    lateinit var binding:FragmentCustomBottomSheetDialogBinding
    var database: CartDatabase? = null
    var menu: Menu = Menu()
    var cartList: List<Cart> = ArrayList()
    var lastnamePresent = false
    var totalPrice = 0.0
    companion object{
        lateinit var resid:String
    }
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
            Glide.with(requireContext()).
            load(menu.image)
                .timeout(60000)
                /*   .placeholder(R.drawable.hatpeon)*/
                .into(binding.imageview)
            binding.location.text=menu.description
        }
        val args=this.arguments
        resid =args?.get("id").toString()
        database = CartDatabase.getInstance(requireContext()!!.applicationContext)
        lastnamePresent = true
        binding.addToCart.setOnClickListener {
            lateinit var customDialog: Dialog
            lastnamePresent = false
            dismiss()
            cartList = database!!.getData().cart
            for (i in 0..cartList.size-1){
                val recorded_resID: String = cartList.get(i).restaurantId.toString()
                if (!recorded_resID.equals(menu.restuarantid.toInt().toString())) {
                    customDialog = Dialog(requireContext())
                    customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    customDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    customDialog.setContentView(R.layout.already_in_cart)

                    val okBtn = customDialog.findViewById<AppCompatButton>(R.id.ok)
                    okBtn.setOnClickListener {
                        Log.e("click","yes")
                        customDialog.dismiss()
                    }
                    customDialog.setCancelable(false)
                    customDialog.setCanceledOnTouchOutside(false)
                    customDialog.show()
                    lastnamePresent = true
                    Log.e("INCart","Yes")
                }
            }
            for (i in 0..cartList.size-1){
                val recorded_lastMenu: String = cartList.get(i).itemId.toString()
                if (recorded_lastMenu.equals(menu.id.toInt().toString())) {
                    customDialog = Dialog(requireContext())
                    customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    customDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    customDialog.setContentView(R.layout.already_in_cart)

                    val okBtn = customDialog.findViewById<AppCompatButton>(R.id.ok)
                    okBtn.setOnClickListener {
                        Log.e("click","yes")
                        customDialog.dismiss()
                    }
                    customDialog.setCancelable(false)
                    customDialog.setCanceledOnTouchOutside(false)
                    customDialog.show()
                    lastnamePresent = true
                    Log.e("INCart","Yes")
                }
            }
            if (!lastnamePresent){
                val value: Long
                val cart = Cart( menu.id.toInt(),menu.name,menu.restuarantid,menu.discount_price,"",menu.unit_price,binding.quantity.text.toString(),"","")
                value = database!!.getData().insertCart(cart)
                Log.e("Save", "Success$value")
                cartList = database!!.getData().cart
                RestaurantsItemSearch.cartCount.text=cartList.size.toString()
                RestaurantsItemSearch.cartDialog.visibility=View.VISIBLE

                for (i in 0..cartList.size-1){
                    totalPrice+= cartList.get(i).price.toDouble()*cartList.get(i).quantity.toDouble()
                    Log.e("TotalPrice",totalPrice.toString())

                }
                RestaurantsItemSearch.totalPriceText.text="Tk "+totalPrice.toString()
            }



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