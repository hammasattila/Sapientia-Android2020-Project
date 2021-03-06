package hamm.android.project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hamm.android.project.model.RestaurantBase
import hamm.android.project.model.RestaurantUserData
import hamm.android.project.model.RestaurantPhoto

@Database(entities = [RestaurantBase::class, RestaurantUserData::class, RestaurantPhoto::class], version = 4, exportSchema = false)
abstract class RestaurantDatabase: RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao

    companion object {
        @Volatile
        private var INSTANCE: RestaurantDatabase? = null

        fun getDatabase(context: Context): RestaurantDatabase {
            val temInstance = INSTANCE
            if(null != temInstance) {
                return temInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RestaurantDatabase::class.java,
                    "restaurant_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}