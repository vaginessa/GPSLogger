/**
 * FragmentRecordingControls - Java Class for Android
 * Created by G.Capelli (BasicAirData) on 20/5/2016
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package eu.basicairdata.graziano.gpslogger;


import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentRecordingControls extends Fragment{

    public FragmentRecordingControls() {
        // Required empty public constructor
    }


    private TextView TVGeoPoints;
    private TextView TVPlacemarks;
    private TextView TVRecordButton;
    private TextView TVAnnotateButton;

    final GPSApplication gpsApplication = GPSApplication.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording_controls, container, false);


        TVAnnotateButton = view.findViewById(R.id.id_annotate);
        TVAnnotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlacemarkRequest(v);
            }
        });

        TVGeoPoints = view.findViewById(R.id.id_textView_GeoPoints);
        TVRecordButton = view.findViewById(R.id.id_record);
        TVRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("myApp", "[#] FragmentRecordingControls - TOGGLE TVGeoPoints.onClick");
                ontoggleRecordGeoPoint(v);
            }
        });

        TVPlacemarks = (TextView) view.findViewById(R.id.id_textView_Placemarks);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Workaround for Nokia Devices, Android 9
        // https://github.com/BasicAirData/GPSLogger/issues/77
        if (EventBus.getDefault().isRegistered(this)) {
            //Log.w("myApp", "[#] FragmentRecordingControls - EventBus: FragmentRecordingControls already registered");
            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
        Update();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void ontoggleRecordGeoPoint(View view) {
        if (isAdded()) {
            final boolean grs = gpsApplication.getRecording();
            boolean newRecordingState = !grs;
            gpsApplication.setRecording(newRecordingState);
            EventBus.getDefault().post(EventBusMSG.UPDATE_TRACK);
            TVRecordButton.setBackgroundColor(newRecordingState ? getResources().getColor(R.color.colorPrimary) : Color.TRANSPARENT);
            //TVGeoPoints.setTextColor(getResources().getColor(newRecordingState ? R.color.textColorRecControlPrimary_Active : R.color.textColorRecControlPrimary));
            //TVGeoPointsLabel.setTextColor(getResources().getColor(newRecordingState ? R.color.textColorRecControlSecondary_Active : R.color.textColorRecControlSecondary));
        }
    }

    public void onPlacemarkRequest(View view) {
        if (isAdded()) {
            final boolean pr = gpsApplication.getPlacemarkRequest();
            boolean newPlacemarkRequestState = !pr;
            gpsApplication.setPlacemarkRequest(newPlacemarkRequestState);
            TVAnnotateButton.setBackgroundColor(newPlacemarkRequestState ? getResources().getColor(R.color.colorPrimary) : Color.TRANSPARENT);
            //TVPlacemarks.setTextColor(getResources().getColor(newPlacemarkRequestState ? R.color.textColorRecControlPrimary_Active : R.color.textColorRecControlPrimary));
            //TVPlacemarksLabel.setTextColor(getResources().getColor(newPlacemarkRequestState ? R.color.textColorRecControlSecondary_Active : R.color.textColorRecControlSecondary));

        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onEvent(Short msg) {
        if (msg == EventBusMSG.UPDATE_TRACK) {
            Update();
        }
    }

    public void Update() {
        if (isAdded()) {
            final Track track = gpsApplication.getCurrentTrack();
            final boolean grs = gpsApplication.getRecording();
            final boolean pr = gpsApplication.getPlacemarkRequest();
            if (track != null) {
                if (TVGeoPoints != null)            TVGeoPoints.setText(track.getNumberOfLocations() == 0 ? "" : String.valueOf(track.getNumberOfLocations()));
                if (TVPlacemarks != null)           TVPlacemarks.setText(String.valueOf(track.getNumberOfPlacemarks() == 0 ? "" : track.getNumberOfPlacemarks()));
                if (TVRecordButton != null)         TVRecordButton.setBackgroundColor(grs ? getResources().getColor(R.color.colorPrimary) : Color.TRANSPARENT);
                if (TVAnnotateButton != null)       TVAnnotateButton.setBackgroundColor(pr ? getResources().getColor(R.color.colorPrimary) : Color.TRANSPARENT);
                //if (TVPlacemarks != null)           TVPlacemarks.setTextColor(getResources().getColor(pr ? R.color.textColorRecControlPrimary_Active : R.color.textColorRecControlPrimary));
                //if (TVPlacemarksLabel != null)      TVPlacemarksLabel.setTextColor(getResources().getColor(pr ? R.color.textColorRecControlSecondary_Active : R.color.textColorRecControlSecondary));
                //if (TVGeoPoints != null)            TVGeoPoints.setTextColor(getResources().getColor(grs ? R.color.textColorRecControlPrimary_Active : R.color.textColorRecControlPrimary));
                //if (TVGeoPointsLabel != null)       TVGeoPointsLabel.setTextColor(getResources().getColor(grs ? R.color.textColorRecControlSecondary_Active : R.color.textColorRecControlSecondary));
            }
        }
    }
}