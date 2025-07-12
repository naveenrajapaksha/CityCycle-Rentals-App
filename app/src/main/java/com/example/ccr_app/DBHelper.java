package com.example.ccr_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExampleApp.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_DOB = "dob";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image";


    public static final String TABLE_STATIONS = "rental_stations";
    public static final String STATION_ID = "stationId";
    public static final String STATION_LOCATION = "location";


    public static final String TABLE_BIKES = "bikes";
    public static final String BIKE_ID = "bikeId";
    public static final String BIKE_TYPE = "type";
    public static final String BIKE_AVAILABILITY = "availability";
    public static final String BIKE_STATION_ID = "stationId";


    public static final String TABLE_RENTALS = "rentals";
    public static final String RENTAL_ID = "rentalId";
    public static final String RENTAL_USER_EMAIL = "userEmail";
    public static final String RENTAL_BIKE_ID = "bikeId";
    public static final String RENTAL_START_TIME = "startTime";
    public static final String RENTAL_END_TIME = "endTime";
    public static final String RENTAL_COST = "cost";


    public static final String TABLE_PRICING_PLANS = "pricing_plans";
    public static final String PLAN_ID = "planId";
    public static final String PLAN_TYPE = "planType";
    public static final String PLAN_RATE = "rate";


    public static final String TABLE_PAYMENTS = "payments";
    public static final String PAYMENT_ID = "paymentId";
    public static final String PAYMENT_USER_EMAIL = "userEmail";
    public static final String PAYMENT_CARD_NUMBER = "cardNumber";
    public static final String PAYMENT_CARDHOLDER_NAME = "cardholderName";
    public static final String PAYMENT_EXPIRY_DATE = "expiryDate";
    public static final String PAYMENT_CVV = "cvv";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys=ON;");


        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_CONTACT + " TEXT, " +
                COLUMN_DOB + " TEXT, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PROFILE_IMAGE + " BLOB)";
        db.execSQL(createUserTable);


        String createStationsTable = "CREATE TABLE " + TABLE_STATIONS + " (" +
                STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                STATION_LOCATION + " TEXT)";
        db.execSQL(createStationsTable);


        String createBikesTable = "CREATE TABLE " + TABLE_BIKES + " (" +
                BIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BIKE_TYPE + " TEXT, " +
                BIKE_AVAILABILITY + " INTEGER, " + // 1 for available, 0 for unavailable
                BIKE_STATION_ID + " INTEGER, " +
                "FOREIGN KEY(" + BIKE_STATION_ID + ") REFERENCES " + TABLE_STATIONS + "(" + STATION_ID + "))";
        db.execSQL(createBikesTable);


        String createRentalsTable = "CREATE TABLE " + TABLE_RENTALS + " (" +
                RENTAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RENTAL_USER_EMAIL + " TEXT, " +
                RENTAL_BIKE_ID + " INTEGER, " +
                RENTAL_START_TIME + " LONG, " +
                RENTAL_END_TIME + " LONG, " +
                RENTAL_COST + " REAL, " +
                "FOREIGN KEY(" + RENTAL_BIKE_ID + ") REFERENCES " + TABLE_BIKES + "(" + BIKE_ID + "))";
        db.execSQL(createRentalsTable);


        String createPricingPlansTable = "CREATE TABLE " + TABLE_PRICING_PLANS + " (" +
                PLAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLAN_TYPE + " TEXT, " +
                PLAN_RATE + " REAL)";
        db.execSQL(createPricingPlansTable);


        String createPaymentsTable = "CREATE TABLE " + TABLE_PAYMENTS + " (" +
                PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENT_USER_EMAIL + " TEXT, " +
                PAYMENT_CARD_NUMBER + " TEXT, " +
                PAYMENT_CARDHOLDER_NAME + " TEXT, " +
                PAYMENT_EXPIRY_DATE + " TEXT, " +
                PAYMENT_CVV + " TEXT)";
        db.execSQL(createPaymentsTable);

        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RENTALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICING_PLANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key support every time the database is opened
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public boolean insertUser(String name, String email, String contact, String dob, String gender, String password, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_CONTACT, contact);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PROFILE_IMAGE, image);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        Log.d("DBHelper", "Insert user " + email + ": " + (result != -1));
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{email, password});

        boolean exists = cursor != null && cursor.getCount() > 0;
        Log.d("DBHelper", "checkUser result for email " + email + ": " + exists);
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    public void insertSampleData(SQLiteDatabase db) {

        ContentValues user = new ContentValues();
        user.put(COLUMN_NAME, "Naveen");
        user.put(COLUMN_EMAIL, "Naveeen@ex.com");
        user.put(COLUMN_CONTACT, "1234567890");
        user.put(COLUMN_DOB, "1990-01-01");
        user.put(COLUMN_GENDER, "Male");
        user.put(COLUMN_PASSWORD, "12345678");
        user.put(COLUMN_PROFILE_IMAGE, (byte[]) null);
        db.insert(TABLE_USERS, null, user);


        ContentValues station1 = new ContentValues();
        station1.put(STATION_LOCATION, "Embilipitiya Station");
        db.insert(TABLE_STATIONS, null, station1);

        ContentValues station2 = new ContentValues();
        station2.put(STATION_LOCATION, "Rathnapura Station");
        db.insert(TABLE_STATIONS, null, station2);

        ContentValues station3 = new ContentValues();
        station3.put(STATION_LOCATION, "Rakwana Station");
        db.insert(TABLE_STATIONS, null, station3);


        ContentValues bike1 = new ContentValues();
        bike1.put(BIKE_TYPE, "Yamaha Fz");
        bike1.put(BIKE_AVAILABILITY, 1);
        bike1.put(BIKE_STATION_ID, 1);
        db.insert(TABLE_BIKES, null, bike1);

        ContentValues bike2 = new ContentValues();
        bike2.put(BIKE_TYPE, "Bajaj pulser");
        bike2.put(BIKE_AVAILABILITY, 1);
        bike2.put(BIKE_STATION_ID, 1);
        db.insert(TABLE_BIKES, null, bike2);

        ContentValues bike3 = new ContentValues();
        bike3.put(BIKE_TYPE, "Honda dio");
        bike3.put(BIKE_AVAILABILITY, 1);
        bike3.put(BIKE_STATION_ID, 2);
        db.insert(TABLE_BIKES, null, bike3);

        ContentValues bike4 = new ContentValues();
        bike3.put(BIKE_TYPE, "Bajaj platina");
        bike3.put(BIKE_AVAILABILITY, 1);
        bike3.put(BIKE_STATION_ID, 3);
        db.insert(TABLE_BIKES, null, bike3);


        for (int i = 0; i < 2; i++) {
            ContentValues electricBike = new ContentValues();
            electricBike.put(BIKE_TYPE, "Electric Bike");
            electricBike.put(BIKE_AVAILABILITY, 1);
            electricBike.put(BIKE_STATION_ID, 3);
            db.insert(TABLE_BIKES, null, electricBike);
        }


        ContentValues plan1 = new ContentValues();
        plan1.put(PLAN_TYPE, "Hourly");
        plan1.put(PLAN_RATE, 5.0);
        db.insert(TABLE_PRICING_PLANS, null, plan1);

        ContentValues plan2 = new ContentValues();
        plan2.put(PLAN_TYPE, "Daily");
        plan2.put(PLAN_RATE, 20.0);
        db.insert(TABLE_PRICING_PLANS, null, plan2);

        ContentValues plan3 = new ContentValues();
        plan3.put(PLAN_TYPE, "Monthly");
        plan3.put(PLAN_RATE, 100.0);
        db.insert(TABLE_PRICING_PLANS, null, plan3);
    }

    public Cursor getAvailableStations(String searchQuery) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s." + STATION_ID + ", s." + STATION_LOCATION + ", COUNT(b." + BIKE_ID + ") as availableBikes " +
                "FROM " + TABLE_STATIONS + " s " +
                "LEFT JOIN " + TABLE_BIKES + " b ON s." + STATION_ID + " = b." + BIKE_STATION_ID + " " +
                "WHERE b." + BIKE_AVAILABILITY + " = 1 " +
                "AND s." + STATION_LOCATION + " LIKE ? " +
                "GROUP BY s." + STATION_ID + ", s." + STATION_LOCATION;
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchQuery + "%"});
        return cursor;
    }

    public boolean reserveBike(int stationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BIKE_AVAILABILITY, 0); // Mark bike as reserved
        int rows = db.update(TABLE_BIKES, values, BIKE_STATION_ID + " = ? AND " + BIKE_AVAILABILITY + " = 1 LIMIT 1",
                new String[]{String.valueOf(stationId)});
        db.close();
        return rows > 0;
    }

    public Cursor getAvailableBikes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b." + BIKE_ID + ", b." + BIKE_TYPE + ", s." + STATION_LOCATION + " " +
                "FROM " + TABLE_BIKES + " b " +
                "JOIN " + TABLE_STATIONS + " s ON b." + BIKE_STATION_ID + " = s." + STATION_ID + " " +
                "WHERE b." + BIKE_AVAILABILITY + " = 1";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getOngoingRental(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_RENTALS + " WHERE " + RENTAL_USER_EMAIL + " = ? AND " + RENTAL_END_TIME + " IS NULL";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});
        return cursor; // Caller must close cursor
    }

    public long startRental(String userEmail, int bikeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RENTAL_USER_EMAIL, userEmail);
        values.put(RENTAL_BIKE_ID, bikeId);
        values.put(RENTAL_START_TIME, System.currentTimeMillis());
        long rentalId = db.insert(TABLE_RENTALS, null, values);

        ContentValues bikeValues = new ContentValues();
        bikeValues.put(BIKE_AVAILABILITY, 0);
        db.update(TABLE_BIKES, bikeValues, BIKE_ID + " = ?", new String[]{String.valueOf(bikeId)});

        db.close();
        return rentalId;
    }

    public boolean endRental(long rentalId, double cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RENTAL_END_TIME, System.currentTimeMillis());
        values.put(RENTAL_COST, cost);
        int rows = db.update(TABLE_RENTALS, values, RENTAL_ID + " = ?", new String[]{String.valueOf(rentalId)});


        Cursor cursor = db.rawQuery("SELECT " + RENTAL_BIKE_ID + " FROM " + TABLE_RENTALS + " WHERE " + RENTAL_ID + " = ?",
                new String[]{String.valueOf(rentalId)});
        if (cursor != null && cursor.moveToFirst()) {
            int bikeId = cursor.getInt(cursor.getColumnIndexOrThrow(RENTAL_BIKE_ID));
            ContentValues bikeValues = new ContentValues();
            bikeValues.put(BIKE_AVAILABILITY, 1);
            db.update(TABLE_BIKES, bikeValues, BIKE_ID + " = ?", new String[]{String.valueOf(bikeId)});
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return rows > 0;
    }

    public Cursor getPricingPlans() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRICING_PLANS, null);
        return cursor;
    }

    public Cursor getPaymentMethods(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PAYMENTS + " WHERE " + PAYMENT_USER_EMAIL + " = ?",
                new String[]{userEmail});
        return cursor;
    }

    public boolean addPaymentMethod(String userEmail, String cardNumber, String cardholderName, String expiryDate, String cvv) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PAYMENT_USER_EMAIL, userEmail);
        values.put(PAYMENT_CARD_NUMBER, cardNumber);
        values.put(PAYMENT_CARDHOLDER_NAME, cardholderName);
        values.put(PAYMENT_EXPIRY_DATE, expiryDate);
        values.put(PAYMENT_CVV, cvv);
        long result = db.insert(TABLE_PAYMENTS, null, values);
        db.close();
        return result != -1;
    }

    public boolean deletePaymentMethod(int paymentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PAYMENTS, PAYMENT_ID + " = ?", new String[]{String.valueOf(paymentId)});
        db.close();
        return rows > 0;
    }
}