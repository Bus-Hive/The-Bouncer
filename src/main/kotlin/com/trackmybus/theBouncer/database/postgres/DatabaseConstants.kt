package com.trackmybus.theBouncer.database.postgres

object DatabaseConstants {
    const val MAX_CHUNK_SIZE = 500_000
    const val MAX_SEMAPHORE_PERMITS = 3
    const val AGENCIES_TABLE_NAME = "agencies"
    const val CALENDAR_DATES_TABLE_NAME = "calendar_dates"
    const val CALENDARS_TABLE_NAME = "calendars"
    const val FEED_INFO_TABLE_NAME = "feed_info"
    const val ROUTES_TABLE_NAME = "routes"
    const val SHAPES_TABLE_NAME = "shapes"
    const val STOP_TIMES_TABLE_NAME = "stop_times"
    const val STOPS_TABLE_NAME = "stops"
    const val TRIPS_TABLE_NAME = "trips"
    const val HASH_TABLE_NAME = "hashes_table"
}
