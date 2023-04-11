package hatpeon.app.com.Model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "cart")
public class Cart implements Serializable {
    @PrimaryKey(autoGenerate = true)

    public int Id;
    @ColumnInfo
    int itemId;
    @ColumnInfo
    String name;

    @ColumnInfo
    String restaurantId;

    @ColumnInfo
    String discountedPrice;

    @ColumnInfo
    String instructions;

    @ColumnInfo
    String price;

    @ColumnInfo
    String quantity;

    @ColumnInfo
    String menuItemVariationId;

    @ColumnInfo
    String options;

    public Cart(int itemId, String name, String restaurantId, String discountedPrice, String instructions, String price, String quantity, String menuItemVariationId, String options) {
        this.itemId = itemId;
        this.name = name;
        this.restaurantId = restaurantId;
        this.discountedPrice = discountedPrice;
        this.instructions = instructions;
        this.price = price;
        this.quantity = quantity;
        this.menuItemVariationId = menuItemVariationId;
        this.options = options;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMenuItemVariationId() {
        return menuItemVariationId;
    }

    public void setMenuItemVariationId(String menuItemVariationId) {
        this.menuItemVariationId = menuItemVariationId;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}