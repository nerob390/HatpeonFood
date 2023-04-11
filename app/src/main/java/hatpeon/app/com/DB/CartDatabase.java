package hatpeon.app.com.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import hatpeon.app.com.Model.Cart;


@androidx.room.Database(entities = {
          Cart.class
},version = 1, exportSchema = false)

public abstract class CartDatabase extends RoomDatabase {
    public static CartDatabase db;
    public abstract RoomDao getData();

    public static CartDatabase getInstance(Context context){
        if(db==null){
            db = Room.databaseBuilder(context,CartDatabase.class,"hatpeon")
                    .allowMainThreadQueries()
                    .build();
        }
        return db;
    }
}