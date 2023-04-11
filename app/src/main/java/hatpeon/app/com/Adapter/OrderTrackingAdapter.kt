package hatpeon.app.com.Adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hatpeon.app.com.DB.CartDatabase
import hatpeon.app.com.Fragment.CheckOutFragment
import hatpeon.app.com.Model.Cart
import hatpeon.app.com.databinding.CartSummaryChildBinding
import hatpeon.app.com.databinding.TrackingChildBinding

class OrderTrackingAdapter (private val cartList: List<Cart>, private val context: Context): RecyclerView.Adapter<OrderSummaryViewHolder>() {


    var totalPrice = 0.0
    var totalPriceInCart = 0.0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TrackingChildBinding.inflate(inflater, parent, false)
        return OrderSummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) {
        val cart = cartList[position]

        holder.binding.quantity.text=cart.quantity
        totalPrice=cart.quantity.toDouble()*cart.price.toDouble()
        holder.binding.name.text=cart.name
        holder.binding.price.text="Tk "+totalPrice.toString()

        holder.binding.mainprice.text="TK "+cart.price

        totalPriceInCart+= cart.price.toDouble()*cart.quantity.toDouble()



    }

    override fun getItemCount(): Int {
        return cartList.size
    }
}

class OrderSummaryViewHolder(val binding: TrackingChildBinding):RecyclerView.ViewHolder(binding.root) {

}