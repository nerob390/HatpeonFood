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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Fragment.RestaurantsView
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.Model.Menu
import hatpeon.app.com.R
import hatpeon.app.com.databinding.FragmentCustomBottomSheetDialogBinding


class CustomBottomSheetDialogFragment():
    BottomSheetDialogFragment() {
    lateinit var binding:FragmentCustomBottomSheetDialogBinding
    lateinit var customDialog: Dialog
    var lastnamePresent = false
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
            binding.location.text=RestaurantsView.restaurant.address

        }

        database = CartDatabase.getInstance(requireContext()!!.applicationContext)
        lastnamePresent = true
        binding.addToCart.setOnClickListener {
          lastnamePresent = false
            dismiss()
            cartList = database!!.getData().cart

                for (i in 0..cartList.size-1){
                    val recorded_lastname: String = cartList.get(i).itemId.toString()
                    if (recorded_lastname.equals(menu.id.toInt().toString())) {

                        customDialog = Dialog(requireContext())
                        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        customDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        customDialog.setContentView(R.layout.already_in_cart)

                        val okBtn = customDialog.findViewById<AppCompatButton>(R.id.ok)
                        okBtn.setOnClickListener {
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
                    val cart = Cart( menu.id.toInt(),menu.name,RestaurantsView.resid,menu.discount_price,"",menu.unit_price,binding.quantity.text.toString(),"","")
                    value = database!!.getData().insertCart(cart)
                    Log.e("Save", "Success$value")
                    cartList = database!!.getData().cart
                    RestaurantsView.cartCount.text=cartList.size.toString()
                    RestaurantsView.cartDialog.visibility=View.VISIBLE

                    for (i in 0..cartList.size-1){
                        totalPrice+= cartList.get(i).price.toDouble()*cartList.get(i).quantity.toDouble()
                        Log.e("TotalPrice",totalPrice.toString())

                    }
                    RestaurantsView.totalPriceText.text="Tk "+totalPrice.toString()
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