package hatpeon.app.com.Model

import java.io.Serializable

class Menu:Serializable{
    lateinit var id:String;
    lateinit var restuarantid:String;
    lateinit var name:String;
    lateinit var slug:String;
    lateinit var menu_number:String;
    lateinit var unit_price:String;
    lateinit var discount_price:String;
    lateinit var currency_code:String;
    lateinit var image:String;
    lateinit var description:String;



    constructor(
        id: String,
        restuarantid: String,
        name: String,
        slug: String,
        menu_number: String,
        unit_price: String,
        discount_price: String,
        currency_code: String,
        image: String,
        description: String
    ) {
        this.id = id
        this.restuarantid = restuarantid
        this.name = name
        this.slug = slug
        this.menu_number = menu_number
        this.unit_price = unit_price
        this.discount_price = discount_price
        this.currency_code = currency_code
        this.image = image
        this.description = description
    }
    constructor()
}