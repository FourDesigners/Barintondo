package it.uniba.di.sms.barintondo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.load.model.FileLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import it.uniba.di.sms.barintondo.HomeActivity;
import it.uniba.di.sms.barintondo.LoginActivity;

public class BackgroundRegistration extends AsyncTask<String, Void,String> {

    AlertDialog dialog;
    Context context;
    String nickname, email, password;
    ProfileOpenHelper openHelper;
    String origin;
    public Boolean registration = false;
    public BackgroundRegistration(Context context, String nickname, String email, String password, ProfileOpenHelper openHelper, String origin)
    {
        this.context = context;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.openHelper = openHelper;
        this.origin = origin;
    }

    @Override
    protected void onPreExecute() {
        //dialog = new AlertDialog.Builder(context).create();
        //dialog.setTitle("Registration Status");
    }
    @Override
    protected void onPostExecute(String s) {
        //dialog.setMessage(s);
        //dialog.show();
        if(origin.equals(LoginActivity.class.toString())) {
            if(s.contains("Registration successfull")) Toast.makeText(context, "Sincronizzazione...", Toast.LENGTH_SHORT).show();
        }else {
            if(s.contains("Registration successful")) {
                Log.e("DBRegistration", "ok");

                if(!ProfileOpenHelper.isPresent(email, openHelper)) {
                    ProfileOpenHelper.insertInto(nickname, email, password, openHelper);
                }
                //Toast.makeText(context, "Account creato", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, HomeActivity.class);
                context.startActivity(intent);
            }else {
                Toast.makeText(context, "Account già presente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected String doInBackground(String... voids) {
        String result = "";
        String user = email;
        String pass = password;

        String connstr = "http://barintondo.altervista.org/registration.php";

        try {
            URL url = new URL(connstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data = URLEncoder.encode("nickname","UTF-8")+"="+URLEncoder.encode(nickname,"UTF-8")
                    +"&&"+URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")
                    +"&&"+URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8");
            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();

            InputStream ips = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
            String line ="";
            while ((line = reader.readLine()) != null)
            {
                result += line;
            }
            reader.close();
            ips.close();
            http.disconnect();
            return result;

        } catch (MalformedURLException e) {
            result = e.getMessage();
        } catch (IOException e) {
            result = e.getMessage();
        }


        return result;
    }
}
