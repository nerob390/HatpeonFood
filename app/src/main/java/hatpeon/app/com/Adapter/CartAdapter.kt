package hatpeon.app.com.Adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Fragment.CartFragment
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.databinding.CartChildBinding

class CartAdapter (private val cartList: ArrayList<Cart>, private val context: Context,val recyclerView: RecyclerView): RecyclerView.Adapter<CartViewHolder>() {

    var database: CartDatabase? = null
    var totalPrice = 0.0
    var totalPricefromcart1=0.0
    var totalPriceInCart = 0.0
    var cartdb: List<Cart> = ArrayList()
    var cartdb1: List<Cart> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartChildBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart = cartList[position]

        database = CartDatabase.getInstance(context.applicationContext)
        holder.binding.quantity.text=cart.quantity
        totalPrice=cart.quantity.toDouble()*cart.price.toDouble()
        holder.binding.price.text="Tk "+totalPrice.toString()
        cartdb = database!!.getData().cart
/*        for (i in 0..cartdb.size-1){
            //holder.binding.quantity.text=cartdb.get(i).quantity
            totalPrice=cartList.get(i).quantity.toDouble()*cartList.get(i).price.toDouble()

        }*/

        holder.binding.name.text=cart.name


        totalPriceInCart+= cart.price.toDouble()*cart.quantity.toDouble()

        //CartFragment.totalPriceText.text="Tk "+totalPriceInCart.toString()
        holder.setIsRecyclable(false);
        holder.binding.add.setOnClickListener {


//            notifyItemRangeChanged(position, arraylist.size)
//            recyclerView.removeViewAt(position)
//            notifyDataSetChanged()
            var totalPricefromcart = 0.0
            var intAmount: Int = holder.binding.quantity.text.toString().toInt()
            intAmount++
            database!!.getData().updateCart(intAmount.toString(),cart.getItemId().toString())
            holder.binding.quantity.text=intAmount.toString()
            var totalPricecart = 0.0
            totalPricecart=intAmount.toString().toDouble()*cart.price.toDouble()
            Log.e("totalPricecart",totalPricecart.toString())
            holder.binding.price.text="Tk "+totalPricecart.toString()

            totalPriceInCart+= cart.price.toDouble()*cart.quantity.toDouble()

            cartdb = database!!.getData().cart
            for (i in 0..cartdb.size-1){
                totalPricefromcart+= cartdb.get(i).price.toDouble()*cartdb.get(i).quantity.toDouble()
               // holder.binding.quantity.text=cartdb.get(i).quantity
            }
            CartFragment.totalPriceText.text="Tk "+totalPricefromcart.toString()

        }
        holder.binding.minius.setOnClickListener {
            var intAmount: Int = holder.binding.quantity.text.toString().toInt()
            if (intAmount  < 2) {

/*                Log.e("almostDone","yes")
                database!!.getData().deleted(cartList.get(position).getItemId().toString())
                Log.e("getID",cartList.get(position).getItemId().toString())
                cartList.removeAt(holder.position)
                Log.e("position",position.toString())
*//*                CartFragment.totalPriceText.text="Tk "+totalPricefromcart1.toString()
                holder.binding.quantity.text=cart.quantity
                totalPrice=cart.quantity.toDouble()*cart.price.toDouble()
                holder.binding.price.text="Tk "+totalPrice.toString()*//*
                recyclerView.removeViewAt(position)
                notifyItemRangeChanged(position, cartList.size)
                notifyDataSetChanged()
                if (cartList.size==0){
                    CartFragment.totalPriceText.text="Tk "+"0".toString()
                    CartFragment.cartDialog.visibility=View.GONE
                }

    *//*            CartFragment.cartAdapter!!.notifyItemRemoved(position)
               var arraylist = ArrayList(cartList)
                Log.e("getID",arraylist.get(position).getItemId().toString())
                arraylist.removeAt(holder.position)
                Log.e("position",position.toString())

                notifyItemRangeChanged(position, arraylist.size)
                notifyDataSetChanged()*//*
                //   database!!.getData().deleted(arraylist.get(position).getItemId().toString())
               val database1 = CartDatabase.getInstance(context!!.applicationContext)
              database1!!.getData().cart
               // Log.e()
          //   for (i in 0..cartList.size-1){
                  // totalPricefromcart1+= cartList.get(i).price.toDouble()*cartList.get(i).quantity.toDouble()

                    //holder.binding.quantity.text=cartdb.get(i).quantity
               //}
                CartFragment.totalPriceText.text="Tk "+database1!!.getData().sum().toString()
               Log.e("taka",totalPricefromcart1.toString())*/
            }else{
                var totalPricefromcart = 0.0
                intAmount--
                database!!.getData().updateCart(intAmount.toString(),cart.getItemId().toString())
                holder.binding.quantity.text=intAmount.toString()
                var totalPricecart = 0.0
                totalPricecart=intAmount.toString().toDouble()*cart.price.toDouble()
                Log.e("totalPricecart",totalPricecart.toString())
                holder.binding.price.text="Tk "+totalPricecart.toString()

                totalPriceInCart-= cart.price.toDouble()*cart.quantity.toDouble()
                cartdb = database!!.getData().cart
                for (i in 0..cartdb.size-1){
                    totalPricefromcart+= cartdb.get(i).price.toDouble()*cartdb.get(i).quantity.toDouble()
                   // holder.binding.quantity.text=cartdb.get(i).quantity
                }
                CartFragment.totalPriceText.text="Tk "+totalPricefromcart.toString()
              //  CartFragment.totalPriceText.text="Tk "+totalPricecart.toString()
            }

        }

    }

    override fun getItemCount(): Int {
        return cartList.size
    }
}

class CartViewHolder(val binding: CartChildBinding):RecyclerView.ViewHolder(binding.root) {

}