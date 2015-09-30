package com.shinjaehun.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shinjaehun.sunshine.data.WeatherContract;

import org.w3c.dom.Text;


public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    //private String mForecastStr;

    //public static final String DETAIL_URI = "URI";

    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    private static final int DETAIL_LOADER = 0;

//    private static final String[] FORECAST_COLUMNS = {
//            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
//            WeatherContract.WeatherEntry.COLUMN_DATE,
//            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
//            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
//            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
//    };

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureview;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
//
//        Intent intent = getActivity().getIntent();
        //        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
        //            mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
        //
        //            ((TextView)rootView.findViewById(R.id.detail_text)).setText(mForecastStr);
        //        }
//        if (intent != null) {
//            mForecastStr = intent.getDataString();
//        }
//        if (null != mForecastStr) {
//            ((TextView)rootView.findViewById(R.id.detail_text)).setText(mForecastStr);
//        }
//

        mIconView = (ImageView)rootView.findViewById(R.id.item_icon);
        mDateView = (TextView)rootView.findViewById(R.id.date_tv);
        mFriendlyDateView = (TextView)rootView.findViewById(R.id.day_tv);
        mDescriptionView = (TextView)rootView.findViewById(R.id.description_tv);
        mHighTempView = (TextView)rootView.findViewById(R.id.high_tv);
        mLowTempView = (TextView)rootView.findViewById(R.id.low_tv);
        mHumidityView = (TextView)rootView.findViewById(R.id.humidity_tv);
        mWindView = (TextView)rootView.findViewById(R.id.wind_tv);
        mPressureview = (TextView)rootView.findViewById(R.id.pressure_tv);

        return rootView;
        //Log.v(LOG_TAG, "In onCreateView");


        //return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
//        ShareActionProvider mShareActionProvider =
//                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(menuItem);
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(createShareForecastIntent());
//        } else {
//            Log.d(LOG_TAG, "Share Action Provider is null?");
//        }

        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //이럴필요가 없다!
//        String locationSetting = Utility.getPreferredLocation(getActivity());
//        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//        Uri weatherForeLocationUri = WeatherContract.WeatherEntry.buildWeatherLocation(locationSetting);

        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );

//        return new CursorLoader(getActivity(), intent.getData(), FORECAST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //이게 좀 다르네
        Log.v(LOG_TAG, "In onLoadFinished");
//        if (!data.moveToFirst()) { return; }
        if ( data != null && data.moveToFirst()) {

//        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
//        String weatherDescription = data.getString(COL_WEATHER_DESC);
//        boolean isMetric = Utility.isMetric(getActivity());
//        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
//        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
            //ForecastAdapter에서는 context라고 선언하는데 여기에서는 왜 getActivity()를 쓰는 걸까?

            //mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

//        TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
//        detailTextView.setText(mForecast);

            int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            long date = data.getLong(COL_WEATHER_DATE);
            String friendlyDateText = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);

            String description = data.getString(COL_WEATHER_DESC);
            mDescriptionView.setText(description);

            boolean isMetric = Utility.isMetric(getActivity());

            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
            String highString = Utility.formatTemperature(getActivity(), high, isMetric);
            mHighTempView.setText(highString);

            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
            String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
            mLowTempView.setText(lowString);

            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
            float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
            mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            mPressureview.setText(getActivity().getString(R.string.format_pressure, pressure));

//            나의 정답
//            long dateInMills = data.getLong(COL_WEATHER_DATE);
//
//            TextView dayView = (TextView) getView().findViewById(R.id.day_tv);
//            dayView.setText(Utility.getDayName(getActivity(), dateInMills));
//
//            TextView dateView = (TextView) getView().findViewById(R.id.date_tv);
//            dateView.setText(Utility.getFormattedMonthDay(getActivity(), dateInMills));
//
//            boolean isMetric = Utility.isMetric(getActivity());
//            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
//            TextView highView = (TextView) getView().findViewById(R.id.high_tv);
//            highView.setText(Utility.formatTemperature(getActivity(), high, isMetric));
//
//            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
//            TextView lowView = (TextView) getView().findViewById(R.id.low_tv);
//            lowView.setText(Utility.formatTemperature(getActivity(), low, isMetric));
//
//            String weatherDescription = data.getString(COL_WEATHER_DESC);
//            TextView descriptionView = (TextView) getView().findViewById(R.id.description_tv);
//            descriptionView.setText(weatherDescription);

            //이게 지금도 필요한 이유는 share intent 때문이라고 함... 그 메시지 태그에 넣을때...
            mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
