package org.ultradevs.mytodolist.utils;

import android.net.Uri;
import android.provider.BaseColumns;

public class UserContract {

        // The authority, which is how your code knows which Content Provider to access
        public static final String AUTHORITY = "org.ultradevs.todolist";

        // The base content URI = "content://" + <authority>
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        // Define the possible paths for accessing data in this contract
        public static final String PATH_USERS = "users";

        /* UserEntry is an inner class that defines the contents of the task table */
        public static final class USERS_ENTERY implements BaseColumns {

            // UserEntry content URI = base content URI + path
            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();


            // Task table and column names
            public static final String TABLE_NAME = "users";

            public static final String COLUMN_NAME = "name";
            public static final String COLUMN_PASSWORD = "pass";
            public static final String COLUMN_EMAIL = "email";
            public static final String COLUMN_GENDER = "gender";
            public static final String COLUMN_STATUS = "status";


        }
}
