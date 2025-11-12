package io.mosip.registration.clientmanager.config;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.database.Cursor;

public final class ClientDatabaseMigrations {

    private static final String TAG = ClientDatabaseMigrations.class.getSimpleName();

    private ClientDatabaseMigrations() {}

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS `permitted_local_config` (" +
                        "`code` TEXT NOT NULL, " +
                        "`name` TEXT, " +
                        "`config_type` TEXT, " +
                        "`is_active` INTEGER, " +
                        "`is_deleted` INTEGER, " +
                        "`del_dtimes` INTEGER, " +
                        "PRIMARY KEY(`code`))");

                database.execSQL("CREATE TABLE IF NOT EXISTS `local_preferences` (" +
                        "`id` TEXT NOT NULL, " +
                        "`name` TEXT, " +
                        "`val` TEXT, " +
                        "`config_type` TEXT, " +
                        "`cr_by` TEXT, " +
                        "`cr_dtime` INTEGER, " +
                        "`upd_by` TEXT, " +
                        "`upd_dtimes` INTEGER, " +
                        "`is_deleted` INTEGER, " +
                        "`del_dtimes` INTEGER, " +
                        "PRIMARY KEY(`id`))");

                database.execSQL("CREATE TABLE IF NOT EXISTS `user_role` (" +
                        "`usr_id` TEXT NOT NULL, " +
                        "`role_code` TEXT NOT NULL, " +
                        "`lang_code` TEXT, " +
                        "PRIMARY KEY(`usr_id`, `role_code`))");

                if (!hasColumn(database, "registration", "id")) {
                    database.execSQL("ALTER TABLE `registration` ADD COLUMN `id` TEXT");
                }

                database.execSQL("UPDATE local_preferences SET config_type = 'CONFIGURATION' WHERE config_type IS NULL");
                Log.i(TAG, "Migration 1_2 completed successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Migration 1_2 failed", e);
            }
        }
    };

    private static boolean hasColumn(@NonNull SupportSQLiteDatabase database, @NonNull String table, @NonNull String column) {
        Cursor cursor = null;
        try {
            cursor = database.query("PRAGMA table_info(`" + table + "`)");
            int nameIndex = cursor.getColumnIndex("name");
            while (cursor.moveToNext()) {
                if (column.equalsIgnoreCase(cursor.getString(nameIndex))) {
                    return true;
                }
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

