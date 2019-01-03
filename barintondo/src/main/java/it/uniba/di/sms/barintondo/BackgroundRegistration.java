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

public class BackgroundRegistration extends AsyncTask<String, Void,String> {

    AlertDialog dialog;
    Context context;
    String origin;
    public Boolean registration = false;
    public BackgroundRegistration(Context context, String origin)
    {
        this.context = context;
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
            /*
            Intent intent_name = new Intent();
            intent_name.setClass(context.getApplicationContext(), HomeActivity.class);
            context.startActivity(intent_name);
            */
                Toast.makeText(context, "Account creato", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "Account già presente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected String doInBackground(String... voids) {
        String result = "";
        String nickname = voids[0];
        String user = voids[1];
        String pass = voids[2];

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
