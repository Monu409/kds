package com.zotto.kds.localIP;

//import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.zotto.kds.rabbitmq.Config;
import com.zotto.kds.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class ChatServer extends Thread {

    private Context context;
    private String serverIpAddress;
    private Activity activity;
    private String ownIp;
    private String TAG = "CHATSERVER";
    private RecyclerView messageList;
    //    private ArrayList<Message> messageArray;
//    private ChatAdapterRecycler mAdapter;
    private int port;

    String id;
    int orderPort;

    String orderIp;

    //    ArrayList<String> ipAddress = new ArrayList();
    ArrayList<Integer> allPorts = new ArrayList();

    public ChatServer(String ownIp, Activity activity, Context context, int port, String serverIpAddress) {
        this.ownIp = ownIp;
        this.port = port;
        this.context = context;
        this.serverIpAddress = serverIpAddress;
        this.activity = activity;
        id = SessionManager.getRestaurantId(context);
//        startServer();
    }

    //@SuppressLint("SetTextI18n")
    public void run() {
        try {
            ServerSocket initSocket = new ServerSocket(port);
            initSocket.setReuseAddress(true);
            if(!initSocket.isBound()){
                initSocket.bind(new InetSocketAddress(serverIpAddress, port));
            }
            System.out.println(TAG + "started");
            while (!Thread.interrupted()) {
                Socket connectSocket = initSocket.accept();
                receiveTexts handle = new receiveTexts();
                handle.execute(connectSocket);
            }
            initSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void startServer() {
//        new Thread(() -> {
//            try {
//                ServerSocket serverSocket = new ServerSocket(port);
//                System.out.println("Server started. Waiting for clients...");
//
//                // Accept client connections
//                Socket clientSocket = serverSocket.accept();
//                System.out.println("Client connected!");
//
//                // Read data from client
//                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//
//                String receivedMessage;
//                while ((receivedMessage = in.readLine()) != null) {
//                    System.out.println("Received: " + receivedMessage);
//
//                    // Optionally send a response
//                    out.println("Message received: " + receivedMessage);
//
//                }
//
//                clientSocket.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//    @SuppressLint("StaticFieldLeak")
    public class receiveTexts extends AsyncTask<Socket, Void, String> {
        String text;

        @Override
        protected String doInBackground(Socket... sockets) {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(sockets[0].getInputStream()));
                text = input.readLine();
                Log.i(TAG, "Received => " + text);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: Result" + result);
            if (result.charAt(0) == '1' && result.charAt(1) == ':') {
                Log.e("result", result);
                parsMessage(result);
            } else {
                Log.e("result 123", result);
                parsMessage(result);
            }
        }

    }

    private void parsMessage(String message) {
        try {
            Log.e("request12", message);
            //  writeLogCat(message)
            JSONObject mainJson = new JSONObject(message);


//            orderIp =
            JSONArray orders = mainJson.getJSONArray("MainOrder");

            orderPort = Integer.parseInt(orders.getJSONObject(0).getString("port"));
            orderIp = orders.getJSONObject(0).getString("ip_address");

            if (orders == null || orders.length() < 1)
                return;

            JSONObject json = orders.getJSONObject(0);
            if (json.has("Items")) {
                json.put("products", json.getJSONArray("Items"));
                json.remove("Items");
            } else {
                if (orders.length() > 1) {
                    JSONObject itemjson = orders.getJSONObject(1);
                    if (itemjson.has("Items")) {
                        json.put("products", itemjson.getJSONArray("Items"));
                        json.remove("Items");
                    }
                }
            }
            String type = json.getString("type");
            if (type.equals("remote_control")) {
                handleDataMessage(json, type);
            } else {
                handleDataMessage(json, type);
            }

//            Send response as ack /////////////////////////
            JSONObject fullJson = new JSONObject();
            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("restId", SessionManager.getRestaurantId(context));
                messageJson.put("orderid", orders.getJSONObject(0).getString("order_id"));
                messageJson.put("sequence_order_id", orders.getJSONObject(0).getString("sequence_order_id"));
                messageJson.put("time", orders.getJSONObject(0).getString("delivery_time"));
                messageJson.put("status", orders.getJSONObject(0).getString("order_status"));
                messageJson.put("location", orders.getJSONObject(0).getString("order_location"));
                messageJson.put("chain_id", "8");//
                messageJson.put("chain_order_id", orders.getJSONObject(0).getString("chain_order_id"));
//                messageJson.put("comments", orders.getJSONObject(0).getString("comments"));
//                messageJson.put("ticket_id", json.getJSONObject("ticket_id"));
//                messageJson.put("name", orders.getJSONObject(0).getString("firstname"));
                messageJson.put("table", orders.getJSONObject(0).getString("table_id"));
//                messageJson.put("rename", orders.getJSONObject(0).getString("delivery_time"));
                messageJson.put("kds_active", orders.getJSONObject(0).getString("kds_active"));
                messageJson.put("isreceieved", "1");
//                messageJson.put("fmname", json.getJSONObject("fmname"));
//                messageJson.put("omname", json.getJSONObject("omname"));
//                messageJson.put("detourname", json.getJSONObject("detourname"));
                fullJson.put("type", "orderUpdate");
                fullJson.put("message", messageJson);
                Log.e("responce", "" + fullJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            SessionManager.setSelectedIp(context, orderIp);
            SessionManager.setSelectedPort(context,orderPort+"");

            SendTask sendTask = new SendTask(context,fullJson.toString(),orderIp,orderPort);
            sendTask.execute();
        } catch (Exception e) {

            Log.e(TAG + "Data_Payload: ", e.getMessage().toString());

        }
    }

    private void handleDataMessage(JSONObject json, String type) {
        Log.e("handleDataMessage", json.toString() + "type-->" + type);
        try {
            if (type.equals("remote_control")) {
                Intent pushNotification = new Intent(Config.ROBOT_NOTIFICATION);
                pushNotification.putExtra("message", json.toString());
                LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
            } else if (type.equals("endSession")) {
                Intent pushNotification = new Intent(Config.ORDER_NOTIFICATION);
                pushNotification.putExtra("message", json.toString());
                LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
            } else {
                Intent pushNotification = new Intent(Config.LOCAL_IP_ORDER_NOTIFICATION);
                pushNotification.putExtra("message", json.toString());
                LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
            }
        } catch (Exception e) {
            Log.e(TAG + "ip data=", "Exception: " + e.getMessage());
        }
    }

}
