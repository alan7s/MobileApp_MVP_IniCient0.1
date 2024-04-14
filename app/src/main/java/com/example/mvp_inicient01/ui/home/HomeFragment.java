package com.example.mvp_inicient01.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.example.mvp_inicient01.R;
import com.example.mvp_inicient01.databinding.FragmentHomeBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SupportMapFragment supportMapFragment;
    private LocationManager locationManager;
    private LocationListener locationListener;
    Button startLOGButton, clearLOGButton;
    private Boolean startLogging = false;
    private String userID;


    private static final String ACCESS_TOKEN = "DROPBOX_TOKEN"; //dropbox api key

    private Timer uploadTimer;

    public static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        requestLocationPermission();

        startLOGButton = root.findViewById(R.id.startLOG);
        clearLOGButton = root.findViewById(R.id.clearLOG);

        startLOGButton.setOnClickListener(new View.OnClickListener() {
            boolean isLogging = false;

            @Override
            public void onClick(View v) {
                if (isLogging) {
                    // Altera o texto do botão para "Iniciar Log"
                    startLOGButton.setText("Iniciar Trajeto");
                    Toast.makeText(getActivity().getApplicationContext(), "Trajeto parado!", Toast.LENGTH_SHORT).show();
                    startLogging = false;
                } else {
                    // Altera o texto do botão para "Parar Log"
                    startLOGButton.setText("Parar Trajeto");
                    Toast.makeText(getActivity().getApplicationContext(), "Trajeto iniciado!", Toast.LENGTH_SHORT).show();
                    startLogging = true;
                }

                // Inverte o estado do botão
                isLogging = !isLogging;
            }
        });

        clearLOGButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCSVFile(userID + ".csv");
            }
        });
        getUserAndroidID();
        startUploadTimer();

        return root;
    }

    private void startUploadTimer() {
        uploadTimer = new Timer();
        uploadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Chamada para o método de upload
                File file = new File(requireActivity().getApplicationContext().getFilesDir(), userID + ".csv");
                //Log.d("DropboxUploader", String.valueOf(file.exists()));
                uploadCSVFileToDropbox2(file);
            }
        }, 0, 1 * 60 * 1000); // 5 minutos em milissegundos
    }

    private void getUserAndroidID(){
        userID = Settings.Secure.getString(requireActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        TextView userPhoneNumber = binding.telefoneTextView;
        userPhoneNumber.setText(userID);
        //Toast.makeText(getActivity().getApplicationContext(), "userID = " + userID, Toast.LENGTH_LONG).show();
        //Log.d("teste", userID);
    }
    private void requestLocationPermission() {
        Dexter.withContext(getActivity().getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        initLocationListener();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getActivity().getApplicationContext(), "Necessário habilitar permissões de localização!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


    private void initLocationListener() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateMapLocation(location);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        };
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void writeToCSV(String fileName, String gpsTime, String speed, String longitude, String latitude, Boolean status) {
        try {
            if(status) {
                File file = new File(getActivity().getApplicationContext().getFilesDir(), fileName);
                FileOutputStream writer = new FileOutputStream(file, true);
                if (file.length() == 0) {
                    String header = "GPS Time, Longitude, Latitude, Speed(km/h)\n";
                    writer.write(header.getBytes());
                    Toast.makeText(getActivity().getApplicationContext(), "Arquivo criado: " + fileName, Toast.LENGTH_SHORT).show();
                }
                String csvContent = gpsTime + "," + longitude + "," + latitude + "," + speed + "\n";
                writer.write(csvContent.getBytes());
                writer.close();
                //Toast.makeText(getActivity().getApplicationContext(), "Wrote to file: " + fileName, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteCSVFile(String fileName) {
        try {
            File file = new File(getActivity().getApplicationContext().getFilesDir(), fileName);
            if (file.exists()) {
                if (file.delete()) {
                    Toast.makeText(getActivity().getApplicationContext(), fileName + " limpo!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Não foi possível limpar" + fileName, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), fileName + " não encontrado.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSpeedDisplay(Location location) {
        double speed = location.getSpeed();
        double currentSpeed = round(speed, 3, BigDecimal.ROUND_HALF_UP);
        double kmphSpeed = round((currentSpeed * 3.6), 3, BigDecimal.ROUND_HALF_UP);
        //Log.d("speed", Double.toString(kmphSpeed));
        TextView timeTextView = getActivity().findViewById(R.id.timeTextView);
        TextView speedTextView = getActivity().findViewById(R.id.speedTextView);
        TextView longitudeTextView = getActivity().findViewById(R.id.longitudeTextView);
        TextView latitudeTextView = getActivity().findViewById(R.id.latitudeTextView);

        SimpleDateFormat formatDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
        String gpsTime = formatDate.format(new Date(location.getTime()));

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        DecimalFormat df = new DecimalFormat("#.########");

        String speedText = kmphSpeed + " km/h";
        String longitudeText = df.format(longitude);
        String latitudeText = df.format(latitude);

        speedTextView.setText(speedText);
        timeTextView.setText(gpsTime);
        longitudeTextView.setText(longitudeText);
        latitudeTextView.setText(latitudeText);

        writeToCSV(userID + ".csv",gpsTime,Double.toString(kmphSpeed),longitudeText,latitudeText, startLogging);
    }
    private void updateMapLocation(Location location) {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Localização atual!");
                    googleMap.clear();
                    googleMap.addMarker(markerOptions);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    updateSpeedDisplay(location);
                } else {
                    Toast.makeText(getActivity(), "Ative as permissões do seu aplicativo de localização.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void uploadCSVFileToDropbox2(File file) {
        // Configurações do cliente do Dropbox
        DbxRequestConfig config = DbxRequestConfig.newBuilder("TesteUploadCEFET").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        TextView logfileTextView = getView().findViewById(R.id.logfileTextView);
        try {
            InputStream inputStream = new FileInputStream(file);
            client.files().uploadBuilder("/" + file.getName())
                .withMode(WriteMode.OVERWRITE)
                .uploadAndFinish(inputStream);
            String logfileText = "Ativo";
            logfileTextView.setText(logfileText);
            Log.d("DropboxUploader", "Arquivo enviado com sucesso para o Dropbox!");
        } catch (Exception e) {
            String logfileText = "Pendente";
            logfileTextView.setText(logfileText);
            e.printStackTrace();
            Log.e("DropboxUploader", "Erro ao enviar arquivo para o Dropbox: " + e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        if (uploadTimer != null) {
            uploadTimer.cancel();
        }
    }
}
