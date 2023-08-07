package com.app.android.repeatedquestionidentifier.database;
import androidx.room.TypeConverter;
import java.util.Date;

//Since we cannot store Date object in ROOM so we use typeconverter
public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}