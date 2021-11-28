package com.example.dexloading;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadBtn = findViewById(R.id.downBtn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
            }
        });

//        File sdCard= Environment.getExternalStorageDirectory();
//        File dexFile=new File(sdCard,"Download/DexClass.dex");
//        PathClassLoader loader=new PathClassLoader(dexFile.getAbsolutePath(),getClassLoader());
//        try {
//            Class<?> DexClass = loader.loadClass("DexClass");
//            Method printLine=DexClass.getDeclaredMethod("printLine");
//            printLine.invoke(DexClass.newInstance());
//            Log.d("Error", "Executed");
//
//        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
//            e.printStackTrace();
//        }

    }

    private void downloadFile(){
        Retrofit retrofit =new Retrofit.Builder().baseUrl("http://192.168.1.4:3000").addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitApi api=retrofit.create(RetrofitApi.class);
        Call<ResponseBody> call=api.downloadDex();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()){
                    Log.d("Res",response.body().toString());
                    boolean res=writeFileTODisk(response.body());
                    Log.d("writeFIleRes",""+res);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("ErrorBitch","SomeError");
            }
        });
    }

    private boolean writeFileTODisk(ResponseBody body){
        File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "file.dex");
        InputStream inputStream=null;
        OutputStream outputStream=null;
        try{
            byte[] fileReade=new byte[50000];
            long fileSize=body.contentLength();
            long fileSizeDownload=0;
            inputStream=body.byteStream();
            try {
                outputStream=new FileOutputStream(futureStudioIconFile);
                while (true){
                    int read=inputStream.read(fileReade);

                    if(read==-1){
                        break;
                    }
                    outputStream.write(fileReade,0,read);
                    fileSizeDownload+=read;
                    Log.d("FileDownload: ",fileSizeDownload+" of "+fileSize );
                }
                outputStream.flush();


                File sdCard= Environment.getExternalStorageDirectory();
                File dexFile=futureStudioIconFile;
                PathClassLoader loader=new PathClassLoader(dexFile.getAbsolutePath(),getClassLoader());
                try {
                    Class<?> DexClass = loader.loadClass("DexClass");
                    Method printLine=DexClass.getDeclaredMethod("printLine");
                    printLine.invoke(DexClass.newInstance());
                    Log.d("Error", "Executed");

                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }

                return true;

            } catch (IOException e) {
                return false;
            }
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}