package hatpeon.app.com.Model

import java.io.Serializable

class Restaurant:Serializable{
    lateinit var id:String
    var food_type_id: String ?=null
    lateinit var name:String
    lateinit var description:String
    lateinit var address:String
    lateinit var image:String
    lateinit var avgRating:String
    lateinit var avgRatingUser:String
    lateinit var opening_time:String
    lateinit var closing_time:String
    lateinit var coupons:String
    lateinit var couponsvalue:String


    constructor(
        id: String,
        food_type_id: String?,
        name: String,
        description: String,
        address: String,
        image: String,
        avgRating: String,
        avgRatingUser: String,
        opening_time: String,
        closing_time: String,
        coupons: String,
        couponsvalue: String
    ) {
        this.id = id
        this.food_type_id = food_type_id
        this.name = name
        this.description = description
        this.address = address
        this.image = image
        this.avgRating = avgRating
        this.avgRatingUser = avgRatingUser
        this.opening_time = opening_time
        this.closing_time = closing_time
        this.coupons = coupons
        this.couponsvalue = couponsvalue
    }
    constructor()
}