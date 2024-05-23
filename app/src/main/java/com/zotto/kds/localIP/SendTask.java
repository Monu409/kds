package com.zotto.kds.localIP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

@SuppressLint("StaticFieldLeak")

public class SendTask extends AsyncTask<Void, Void, String> {
    String msg;
    String orderIp;
    int orderPort;
    Context context;

   public SendTask(Context context, String message, String orderIp, int orderPort) {
        this.context = context;
        msg = message;
        this.orderIp = orderIp;
        this.orderPort = orderPort;
    }

    @Override
    protected String doInBackground(Void... voids) {

        Log.e("ip", orderIp + "port===" + orderPort);
        try {
            Socket clientSocket = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(orderIp, orderPort);
            clientSocket.connect(sockaddr, 2000);
//                    Socket clientSocket = new Socket(ipAddress.get(i), allPorts.get(i));
            OutputStream outToServer = clientSocket.getOutputStream();
            PrintWriter output = new PrintWriter(outToServer);

            Log.e("message to be send", "= " + msg);
            output.println(msg);
            output.flush();
            clientSocket.close();
        } catch (Exception e) {
            Log.e("message Exception is", "== " + e.getMessage());
            e.printStackTrace();
        }

//                var ipAddress  = gson.fromJson(getSelectedIp(this@SettingFragment), java.util.ArrayList::class.java)
//            Gson gson = new Gson();
//            ArrayList<String> ipAddress = gson.fromJson(SessionManager.getSelectedIp(context), ArrayList.class);
//            ArrayList<Integer> ports = gson.fromJson(SessionManager.getSelectedPort(context), ArrayList.class);

//            for (int i = 0; i < ipAddress.size(); i++) {
//                Log.e("ip", ipAddress.get(i) + "port===" + ports.get(i));
//                try {
//                    Socket clientSocket = new Socket();
//                    SocketAddress sockaddr = new InetSocketAddress(ipAddress.get(i), allPorts.get(i));
//                    clientSocket.connect(sockaddr, 2000);
////                    Socket clientSocket = new Socket(ipAddress.get(i), allPorts.get(i));
//                    OutputStream outToServer = clientSocket.getOutputStream();
//                    PrintWriter output = new PrintWriter(outToServer);
//
//                    Log.e("message to be send", "= " + msg);
//                    output.println(msg);
//                    output.flush();
//                    clientSocket.close();
//                } catch (Exception e) {
//                    Log.e("message Exception is", "== " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }

        return msg;
    }

    protected void onPostExecute(String result) {
        Log.e("message is", "== sent");
        Log.i("TAG", "on post execution result => " + result);
//            StringBuilder stringBuilder = new StringBuilder(result);
//            if (stringBuilder.charAt(0) == '1' && stringBuilder.charAt(1) == ':') {
//                stringBuilder.deleteCharAt(0);
//                stringBuilder.deleteCharAt(0);
//                result = stringBuilder.toString();
//
////                messageArray.add(new Message(result, 0, Calendar.getInstance().getTime()));
////                mMessageRecycler.setAdapter(mMessageAdapter);
////                smessage.setText("");
//            }
        Toast.makeText(context, "Message sent successfully-->" + result, Toast.LENGTH_SHORT).show();

    }


}
