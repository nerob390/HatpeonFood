package hatpeon.app.com.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import hatpeon.app.com.Model.Cart;


@Dao
public interface RoomDao {
/*    @Query("SELECT * FROM user")
    List<User> getAll();

    @Insert
    long insertAll(User users);*/


/*    @Query("update user set name=:name,number=:number Where uid=:uid")
    int update(String name,String number,int uid );

    @Query("delete from user where uid=:uid")
    int deleted(int uid);

    @Delete
    void delete(User user);

    @Insert
    long insertCart(Cart cart);

    @Query("SELECT * FROM cart")
    List<Cart> getCart();

    @Query("UPDATE cart SET quantity = :quantity where itemId = :itemId")
    int updateCart(String quantity,String itemId);

    @Query("Delete from cart")
    void deleteCartData();*/

    @Insert
    long insertCart(Cart cart);

    @Query("SELECT * FROM cart")
    List<Cart> getCart();

    @Query("Delete from cart")
    void deleteCartData();

    @Query("UPDATE cart SET quantity = :quantity where itemId = :itemId")
    int updateCart(String quantity,String itemId);


    @Query("delete from cart where itemId=:itemId")
    int deleted(String itemId);

    @Query("SELECT SUM(quantity*price) AS value FROM cart")
    int sum();

}