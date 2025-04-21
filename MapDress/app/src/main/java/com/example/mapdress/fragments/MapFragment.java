    package com.example.mapdress.fragments;

    import android.os.Bundle;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.FrameLayout;
    import android.widget.TextView;
    import android.widget.Toast;

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
    import com.google.android.material.bottomsheet.BottomSheetBehavior;
    import com.google.android.material.bottomsheet.BottomSheetDialog;
    import com.example.mapdress.BuildConfig;


    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class MapFragment extends Fragment implements OnMapReadyCallback {
        private GoogleMap mMap;
        private Marker tempMarker;


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
            mMap.setOnMapClickListener(latLng -> {
                if (tempMarker != null) {
                    tempMarker.remove();
                }

                tempMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Точка нажатия"));
                if (tempMarker != null){
                    showBottomSheet(tempMarker);
                }

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

            String apiKey = BuildConfig.OPENWEATHER_API_KEY;
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
                        String info = "Температура: " + weather.main.temp + "°C (ощущается как " + weather.main.feels_like + "°C)\n" +
                                "Макс.: " + weather.main.temp_max + "°C, Мин.: " + weather.main.temp_min + "°C\n" +
                                "Влажность: " + weather.main.humidity + "%\n" +
                                "Давление: " + weather.main.pressure + " гПа\n" +
                                "Ветер: " + weather.wind.speed + " м/с, " + (weather.wind.deg != null ? weather.wind.deg + "°\n" : "направление неизвестно\n") +
                                (weather.rain != null && weather.rain.oneH != null ? "Дождь (1ч): " + weather.rain.oneH + " мм\n" : "") +
                                (weather.snow != null && weather.snow.oneH != null ? "Снег (1ч): " + weather.snow.oneH + " мм\n" : "") +
                                "Облачность: " + weather.clouds.all + "%\n" +
                                "Видимость: " + (weather.visibility != null ? weather.visibility + " м\n" : "неизвестно\n") +
                                "Погода: " + weather.weather[0].description + " (" + weather.weather[0].main + ")";

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

            FrameLayout bottomSheetBehavior = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetBehavior != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetBehavior);
                behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            if (tempMarker != null) {
                                tempMarker.remove();
                                tempMarker = null;
                            }
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    }
                });
            }

            bottomSheetDialog.setOnDismissListener(dialog -> {
                if (tempMarker != null) {
                    tempMarker.remove();
                    tempMarker = null;
                }
            });



        }



    }
