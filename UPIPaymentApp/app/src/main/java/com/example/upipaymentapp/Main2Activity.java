package com.example.upipaymentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    EditText amountEt, noteEt, nameEt, upiIdEt,eventName;
    Button pay;
    final int UPI_PAYMENT = 0;

    String msg;
    String number;

    String channel_id="personal notification";
    int id=001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initializeViews();

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Getting the values from the EditTexts
                String amount = amountEt.getText().toString();
                String message = noteEt.getText().toString();
                String name = nameEt.getText().toString();
                String upiId = upiIdEt.getText().toString();

                if(upiId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "UPI ID cannot be EMPTY...!  Please enter valid UPI ID", Toast.LENGTH_LONG).show();
                } else if(name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Receiver Name cannot be EMPTY...!  Please enter Receiver Name", Toast.LENGTH_LONG).show();
                } else if (amount.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Amount cannot be EMPTY...!  Please enter Amount to send", Toast.LENGTH_LONG).show();
                } else
                    payUsingUpi(amount, upiId, name, message);
            }
        });

    }

    void initializeViews() {
        pay = findViewById(R.id.paybutton);
        amountEt = findViewById(R.id.amounttosend);
        noteEt = findViewById(R.id.sendermessage);
        nameEt = findViewById(R.id.receivername);
        upiIdEt = findViewById(R.id.upiid);
    }

    void sendSMS(String number, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null , message, null, null);
        Toast.makeText(getApplicationContext(), "Message Sent...!", Toast.LENGTH_LONG).show();
    }

    void payUsingUpi(String amount, String upiId, String name, String note) {

        //amount = amount.substring(4,amount.length());

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(Main2Activity.this,"No UPI app found,  Please install one to continue",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(Main2Activity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null)
                str = "discard";
            String status = "";
            String approvalRefNo = "";

            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(Main2Activity.this, "Transaction successful.", Toast.LENGTH_LONG).show();
                createNotificationChannel();
                NotificationCompat.Builder builder=new NotificationCompat.Builder(Main2Activity.this,channel_id)
                        .setSmallIcon(R.drawable.upiicon)
                        .setContentTitle("Transaction successful.")
                        .setContentText("Reference id : "+approvalRefNo)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat manager= NotificationManagerCompat.from(Main2Activity.this);
                manager.notify(id,builder.build());
//                number = "8660397320";
//                msg = "Transaction SUCCESSFUL and Reference ID : "+approvalRefNo;
//                sendSMS(number,msg);
                Log.d("UPI", "responseStr: "+approvalRefNo);
//                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
//                startActivity(intent);

                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                intent.putExtra("Transaction_Reference_ID", approvalRefNo);
                startActivity(intent);

            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(Main2Activity.this, "Payment cancelled by user.", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
//                startActivity(intent);
            }
            else {
//                number = "8660397320";
//                msg = "Transaction Failed. Please try again later.";
//                sendSMS(number,msg);

                Toast.makeText(Main2Activity.this, "Transaction failed.Please try again", Toast.LENGTH_LONG).show();
                createNotificationChannel();
                NotificationCompat.Builder builder=new NotificationCompat.Builder(Main2Activity.this,channel_id)
                        .setSmallIcon(R.drawable.upiicon)
                        .setContentTitle("Transaction Failed.")
                        .setContentText("Please try again later.")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat manager= NotificationManagerCompat.from(Main2Activity.this);
                manager.notify(id,builder.build());

//                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
//                startActivity(intent);
            }
        } else {
            Toast.makeText(Main2Activity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
//            startActivity(intent);
        }
    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CharSequence name="Personal Notifications";
            String description="Include all the personal notifications";
            int  importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(channel_id,name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }



}
