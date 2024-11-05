package com.zotto.kds.localIP;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zotto.kds.R;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatClient extends AppCompatActivity {
    String TAG = "CLIENT ACTIVITY";

    EditText smessage;
    AppCompatButton sent;
    String serverIpAddress = "";
    int myport;
    int sendPort;
    //    ArrayList<Message> messageArray;
    ImageButton fileUp;
    TextView textView;
    ChatServer chatServer;
    //    fileServer f;
    String ownIp;
    Toolbar toolbar;
    ProgressBar progressBar;
    //    PickiT pickiT;
    private Boolean exit = false;
//    private RecyclerView mMessageRecycler;

    //    private ChatAdapterRecycler mMessageAdapter;
    private int REQUEST_CODE = 200;

//    ArrayList<String> ipAddress = new ArrayList();
//    ArrayList<Integer> allPorts = new ArrayList();

    ArrayList<String> ipAddress = new ArrayList(Arrays.asList("192.168.163.80"));
    ArrayList<Integer> allPorts = new ArrayList(Arrays.asList(8383));

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_client_activity);
//        Gson gson = new Gson();
//        ipAddress = gson.fromJson(SessionManager.getSelectedIp(this), ArrayList.class);
//        ArrayList<String> ports = gson.fromJson(SessionManager.getSelectedPort(this), ArrayList.class);
//
//        Log.e("ip is", "== " + ipAddress);
//        Log.e("ports is", "== " + ports);
//
//        if (ports != null)
//            for (String port : ports) {
//                if (port != null && !port.equals(""))
//                    allPorts.add(Integer.parseInt(port));
//            }

        Log.e("allPorts is", "== " + allPorts);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ownIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        if (permissionAlreadyGranted()) {
            Toast.makeText(ChatClient.this, "Permission is already granted!", Toast.LENGTH_SHORT).show();
        }
        smessage = findViewById(R.id.smessage);
        sent = findViewById(R.id.btn_sent);
        requestPermission();
        sent.setOnClickListener(v -> {
            if (!smessage.getText().toString().isEmpty()) {

                Log.e("setOnClickListener  is", "run now== ");
                User user = new User(smessage.getText().toString());
                user.execute();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please write something", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


    }

    private boolean permissionAlreadyGranted() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show();
                boolean showRationale = shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE);
                if (!showRationale) {
                    openSettingsDialog();
                }


            }
        }
    }


    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatClient.this);
        builder.setTitle("Required Permissions");
        builder.setMessage("This app require permission to use awesome feature. Grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    @SuppressLint("StaticFieldLeak")

    public class User extends AsyncTask<Void, Void, String> {
        String msg;

        User(String message) {
            msg = message;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.e("doInBackground is", "== now ");
            for (int i = 0; i < ipAddress.size(); i++) {
                try {
//                String ipadd = serverIpAddress;
//                int portr = sendPort;
                    Socket clientSocket = new Socket();
                    SocketAddress sockaddr = new InetSocketAddress(ipAddress.get(i), allPorts.get(i));
                    clientSocket.connect(sockaddr, 2000);
//                    Socket clientSocket = new Socket(ipAddress.get(i), allPorts.get(i));
                    OutputStream outToServer = clientSocket.getOutputStream();
                    PrintWriter output = new PrintWriter(outToServer);
                    output.println(msg);
                    output.flush();
                    clientSocket.close();
//                    runOnUiThread(() -> sent.setEnabled(false));

                } catch (Exception e) {
                    Log.e("message Exception is", "== " + e.getMessage());
                    e.printStackTrace();
                }
            }


            return msg;
        }

        protected void onPostExecute(String result) {
            Log.e("message is", "== sent");
            runOnUiThread(() -> sent.setEnabled(true));
            Log.i(TAG, "on post execution result => " + result);
            StringBuilder stringBuilder = new StringBuilder(result);
            if (stringBuilder.charAt(0) == '1' && stringBuilder.charAt(1) == ':') {
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                result = stringBuilder.toString();

//                messageArray.add(new Message(result, 0, Calendar.getInstance().getTime()));
//                mMessageRecycler.setAdapter(mMessageAdapter);
//                smessage.setText("");
            }
//            Toast.makeText(getApplicationContext(), "" + "Message sent successfully.", Toast.LENGTH_SHORT).show();

        }


    }

}