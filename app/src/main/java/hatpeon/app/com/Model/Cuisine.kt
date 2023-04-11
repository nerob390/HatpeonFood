package hatpeon.app.com.Model

import java.io.Serializable

class Cuisine:Serializable{
    lateinit var id:String;
    lateinit var name:String;
    lateinit var slug:String;
    lateinit var image:String;
    lateinit var description:String;

    constructor(id: String, name: String, slug: String, image: String, description: String) {
        this.id = id
        this.name = name
        this.slug = slug
        this.image = image
        this.description = description
    }
    constructor()
}