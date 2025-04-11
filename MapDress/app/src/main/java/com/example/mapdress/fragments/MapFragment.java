package com.example.mapdress.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mapdress.API.WeatherApiService;
import com.example.mapdress.API.WeatherResponse;
import com.example.mapdress.MainActivity;
import com.example.mapdress.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng astana = new LatLng(51.1694, 71.4491);
        mMap.addMarker(new MarkerOptions().position(astana).title("Астана"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(astana, 10));
        mMap.setOnMarkerClickListener(clickedMarker -> {
            showBottomSheet(clickedMarker);
            return true;
        });
    }

    private void showBottomSheet(Marker marker){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout, null);

        TextView title = bottomSheetView.findViewById(R.id.cityName);
        TextView description = bottomSheetView.findViewById(R.id.mainInfo);

        title.setText(marker.getTitle());
        description.setText("Загружается погода...");

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);

        LatLng position = marker.getPosition();

        String apiKey = "a16d04723ecc8d5cf4a2089964343b26";
        Call<WeatherResponse> call = apiService.getWeather(
                position.latitude,
                position.longitude,
                apiKey,
                "metric",
                "ru"
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String info = "Температура: " + weather.main.temp + "°C\n" +
                            "Описание: " + weather.weather[0].description;
                    description.setText(info);
                } else {
                    description.setText("Не удалось получить погоду.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                description.setText("Ошибка запроса: " + t.getMessage());
            }
        });
    }

}
