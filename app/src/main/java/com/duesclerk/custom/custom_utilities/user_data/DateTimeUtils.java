package com.duesclerk.custom.custom_utilities.user_data;

import java.util.Date;

@SuppressWarnings("unused")
public class DateTimeUtils {

    private static final long secondsInMilli = 1000; // Get seconds in milliseconds
    private static final long minutesInMilli = secondsInMilli * 60; // Get minutes in milliseconds
    private static final long hoursInMilli = minutesInMilli * 60; // Get hours in milliseconds
    private static final long daysInMilli = hoursInMilli * 24; // Get days in milliseconds

    /**
     * Function to get time difference in minutes
     *
     * @param startDate - Start date
     * @param endDate   - End date
     */
    public static long getDateTimeDifferenceInMinutes(Date startDate, Date endDate) {

        // Get date and time difference in milliseconds
        long difference = endDate.getTime() - startDate.getTime();

        long elapsedMinutes = difference / minutesInMilli; // Get elapsed minutes

        // Get elapsed minutes in milliseconds
        long elapsedMinutesInMilliseconds = difference % minutesInMilli;

        return elapsedMinutes; // Return date and time difference in minutes
    }

    /**
     * Function to get time difference in hours
     *
     * @param startDate - Start date
     * @param endDate   - End date
     */
    public static long getDateTimeDifferenceInHours(Date startDate, Date endDate) {

        // Get date and time difference in milliseconds
        long difference = endDate.getTime() - startDate.getTime();

        long elapsedHours = difference / hoursInMilli; // Get elapsed hours

        // Get elapsed hours in millisecond
        long elapsedHoursInMilliseconds = difference % hoursInMilli;

        return elapsedHours; // Return date and time difference in hours
    }

    /**
     * Function to get time difference in days
     *
     * @param startDate - Start date
     * @param endDate   - End date
     */
    public static long getDateTimeDifferenceInDays(Date startDate, Date endDate) {

        // Get date and time difference in milliseconds
        long difference = endDate.getTime() - startDate.getTime();

        long elapsedDays = difference / daysInMilli; // Get elapsed days

        // Get elapsed days in milliseconds
        long elapsedDaysInMilliseconds = difference % daysInMilli;

        return elapsedDays; // Return date and time difference in days
    }

    // System.out.printf(
    //      "%d days, %d hours, %d minutes, %d seconds%n",
    //      elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
}
