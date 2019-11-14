package com.example.easy.ui.cloud;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.easy.tool.Globe;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Thread.sleep;

public class DownloadPhoto {
    private static String getphotouri = "http://114.55.64.152:3000/getphoto";
    private static String downloaduri = "http://114.55.64.152:3000/downloadphoto";
//    private static String getphotouri = "http://192.168.1.103:3000/getphoto";
//    private static String downloaduri = "http://192.168.1.103:3000/downloadphoto";
    private static JSONArray result;
    private static Response response;
    private static InputStream[] PhotoBitMap;
    private static String[][] PhotoMsg;


    public static void downloadPhoto(){
        new Thread(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void run(){
                OkHttpClient client = new OkHttpClient();
                RequestBody formbody = new FormBody.Builder()
                        .add("username", Globe.getLoginUser())
                        .build();
                Request request = new Request.Builder()
                        .url(getphotouri)
                        .post(formbody)
                        .build();
                try{
                    Response response = client.newCall(request).execute();
                    if(!response.isSuccessful()){
                        throw new IOException("Unexpected code" + response);
                    }
                    result = new JSONArray(response.body().string());
                    PhotoMsg = new String[result.length()][8];
                    for (int i = 0;i < result.length(); i++){
                        Gson gson = new Gson();
                        UserFiles userfiles = gson.fromJson(result.getString(i), UserFiles.class);
                        PhotoMsg[i][0] = userfiles.username;
                        PhotoMsg[i][1] = userfiles.filename;
                        PhotoMsg[i][2] = userfiles.filepath;
                        PhotoMsg[i][3] = userfiles.filepathtmp;
                        PhotoMsg[i][4] = userfiles.uploadtime.toString();
                        PhotoMsg[i][5] = userfiles.age;
                        PhotoMsg[i][6] = userfiles.score;
                        //PhotoMsg[i][5] = userfiles.img;
                    }
                    Globe.setPhotoMsg(PhotoMsg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
        while(result == null){
            try {
                sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        PhotoBitMap = new InputStream[result.length()];
        for (int i = 0; i < result.length(); i++){
            final JSONArray finalResult = result;
            final int finalI = i;
            new Thread(){
                public void run(){
                    try{
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formbody = null;
                        formbody = new FormBody.Builder()
                                .add("username", Globe.getLoginUser())
                                .add("filename", PhotoMsg[finalI][1])
                                .build();
                        Request request = new Request.Builder()
                                .url(downloaduri)
                                .post(formbody)
                                .build();
                        response = client.newCall(request).execute();
                        PhotoBitMap[finalI] = response.body().byteStream();
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        Globe.setPhotoBitMap(PhotoBitMap);
        while (Globe.getPhotoMsg() == null){
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
   }

    public class UserFiles{
        public String username;
        public String filename;
        public String filepath;
        public String filepathtmp;
        public Date uploadtime;
        public String age;
        public String score;
        //public String img;
    }
}
