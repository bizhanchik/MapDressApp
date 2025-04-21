// MapFragment.java
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
import com.example.mapdress.AI.GeminiAdvisor;
import com.example.mapdress.API.WeatherApiService;
import com.example.mapdress.API.WeatherResponse;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng astana = new LatLng(51.1694, 71.4491);
        mMap.addMarker(new MarkerOptions().position(astana).title("Астана"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(astana, 10));
        mMap.setOnMarkerClickListener(clickedMarker -> { showBottomSheet(clickedMarker); return true; });
        mMap.setOnMapClickListener(latLng -> {
            if (tempMarker != null) tempMarker.remove();
            tempMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Точка нажатия"));
            if (tempMarker != null) showBottomSheet(tempMarker);
        });
    }

    private void showBottomSheet(Marker marker) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout, null);

        TextView title = view.findViewById(R.id.cityName);
        TextView description = view.findViewById(R.id.mainInfo);
        TextView advice = view.findViewById(R.id.advice);

        title.setText(marker.getTitle());
        description.setText("Загружается погода...");
        advice.setText("Ожидайте совет от ИИ...");

        dialog.setContentView(view);
        dialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherApiService api = retrofit.create(WeatherApiService.class);

        LatLng pos = marker.getPosition();
        Call<WeatherResponse> call = api.getWeather(pos.latitude, pos.longitude, BuildConfig.OPENWEATHER_API_KEY, "metric", "ru");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> c, Response<WeatherResponse> r) {
                if (r.isSuccessful() && r.body() != null) {
                    WeatherResponse w = r.body();
                    String info = "Температура: " + w.main.temp + "°C (ощущается как " + w.main.feels_like + "°C)\n"
                            + "Макс.: " + w.main.temp_max + "°C, Мин.: " + w.main.temp_min + "°C\n"
                            + "Влажность: " + w.main.humidity + "%\n"
                            + "Ветер: " + w.wind.speed + " м/с, " + (w.wind.deg != null ? w.wind.deg + "°\n" : "напр. неизвестно\n")
                            + (w.rain != null && w.rain.oneH != null ? "Дождь (1ч): " + w.rain.oneH + " мм\n" : "")
                            + (w.snow != null && w.snow.oneH != null ? "Снег (1ч): " + w.snow.oneH + " мм\n" : "")
                            + "Облачность: " + w.clouds.all + "%\n"
                            + "Видимость: " + (w.visibility != null ? w.visibility + " м\n" : "неизв.\n")
                            + "Погода: " + w.weather.get(0).description + " (" + w.weather.get(0).main + ")";
                    description.setText(info);

                    GeminiAdvisor.GenerateContentRequest req = new GeminiAdvisor.GenerateContentRequest(
                            new GeminiAdvisor.Message[]{ new GeminiAdvisor.Message(
                                    new GeminiAdvisor.Part[]{new GeminiAdvisor.Part("Погода следующая:\n" + info + "\nКакую одежду лучше надеть?")})
                            }
                    );
                    GeminiAdvisor.getApi().generateContent(req, BuildConfig.GEMINI_API_KEY).enqueue(new Callback<GeminiAdvisor.GenerateContentResponse>() {
                        @Override
                        public void onResponse(Call<GeminiAdvisor.GenerateContentResponse> c2, Response<GeminiAdvisor.GenerateContentResponse> r2) {
                            if (r2.isSuccessful() && r2.body() != null
                                    && r2.body().candidates != null && r2.body().candidates.length > 0
                                    && r2.body().candidates[0].content.parts.length > 0) {
                                advice.setText("Совет: " + r2.body().candidates[0].content.parts[0].text);
                            } else {
                                advice.setText("Не удалось получить совет от ИИ.");
                            }
                        }
                        @Override
                        public void onFailure(Call<GeminiAdvisor.GenerateContentResponse> c2, Throwable t) {
                            advice.setText("Ошибка Gemini: " + t.getMessage());
                        }
                    });
                } else {
                    description.setText("Не удалось получить погоду.");
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> c, Throwable t) {
                description.setText("Ошибка запроса: " + t.getMessage());
            }
        });

        FrameLayout sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (sheet != null) {
            BottomSheetBehavior.from(sheet).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bs, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN && tempMarker != null) {
                        tempMarker.remove();
                        tempMarker = null;
                    }
                }
                @Override public void onSlide(@NonNull View bs, float o) {}
            });
        }
        dialog.setOnDismissListener(d -> {
            if (tempMarker != null) {
                tempMarker.remove();
                tempMarker = null;
            }
        });
    }
}
