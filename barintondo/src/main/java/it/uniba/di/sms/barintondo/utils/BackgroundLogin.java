package it.uniba.di.sms.barintondo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

public class BackgroundLogin extends AsyncTask<String, Void,String> {

    AlertDialog dialog;
    Context context;
    public Boolean login = false;
    private String email, password;
    private ProfileOpenHelper openHelper;
    public BackgroundLogin(Context context, String email, String password, ProfileOpenHelper openHelper)
    {
        this.context = context;
        this.email = email;
        this.password = password;
        this.openHelper = openHelper;
    }

    @Override
    protected void onPreExecute() {
        //dialog = new AlertDialog.Builder(context).create();
        //dialog.setTitle("Login Status");
    }
    @Override
    protected void onPostExecute(String s) {
        //dialog.setMessage(s);
        //dialog.show();
        //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        if(s.contains("login successfull"))
        {
            Log.e("DBLOGIN", "ok");
            ProfileOpenHelper.setNickname(context, email, password, openHelper);
            //Toast.makeText(context, "Credenziali valide", Toast.LENGTH_SHORT).show();
            /*
            Intent intent_name = new Intent();
            intent_name.setClass(context.getApplicationContext(), HomeActivity.class);
            context.startActivity(intent_name);
            */
        }else {
            Toast.makeText(context, "Credenziali non valide", Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected String doInBackground(String... voids) {
        String result = "";
        String user = email;
        String pass = password;

        String connstr = "http://barintondo.altervista.org/login.php";

        try {
            URL url = new URL(connstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data = URLEncoder.encode("user","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")
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

