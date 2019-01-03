package it.uniba.di.sms.barintondo;

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

import it.uniba.di.sms.barintondo.utils.ProfileOpenHelper;

public class BackgroundGetNickname extends AsyncTask<String, Void,String> {
    AlertDialog dialog;
    Context context;
    String email, password;
    ProfileOpenHelper openHelper;
    public Boolean registration = false;

    public BackgroundGetNickname(Context context, String email, String password, ProfileOpenHelper openHelper)
    {
        this.context = context;
        this.email = email;
        this.password = password;
        this.openHelper = openHelper;
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
        ProfileOpenHelper.insertInto(s, email, password, openHelper);
        Intent intent_name = new Intent();
        intent_name.setClass(context.getApplicationContext(), HomeActivity.class);
        context.startActivity(intent_name);
    }

    @Override
    protected String doInBackground(String... voids) {
        String result = "";

        String connstr = "http://barintondo.altervista.org/getNickname.php";

        try {
            URL url = new URL(connstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8");
            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();

            InputStream ips = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
            String line ="";
            while ((line = reader.readLine()) != null) {
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

