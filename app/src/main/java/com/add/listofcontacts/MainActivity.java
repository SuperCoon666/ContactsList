package com.add.listofcontacts;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    ArrayList<HashMap<String, Object>> contactList = new ArrayList<>();
    HashMap<String, Object> map;

    private final String TAG = "CONTACT";
    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String id[] = {"id", "name", "phone", "lastTimeContacted", "contactStatus"};
        int to[] = {R.id.id, R.id.name, R.id.phone, R.id.lastTimeContacted, R.id.contactStatus};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, contactList, R.layout.list_view, id, to);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(simpleAdapter);

        // Проверка разрешения
         if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            // Разрешения чтения контактов имеются
            Log.d(TAG, "Permission is granted");
            readContacts(this);
        } else {
            // Разрешений нет
            Log.d(TAG, "Permission is not granted");
            // Запрос разрешений
            Log.d(TAG, "Request permissions");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission .READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { switch (requestCode) {
        case PERMISSIONS_REQUEST_READ_CONTACTS : {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Разрешения получены
                Log.d(TAG, "Permission was granted!");
                // Чтение контактов
                readContacts(this);
            } else {
                // Разрешения НЕ получены.
                Log.d(TAG, "Permission denied!");
            } return;
        }
        }
        }

    private void readContacts(Context context) {
        Contact contact;
        Cursor cursor=context.getContentResolver().query( ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                contact = new Contact();

                String id = cursor.getString( cursor.getColumnIndex( ContactsContract.Contacts._ID));
                contact.setId(id);


                String name = cursor.getString( cursor.getColumnIndex( ContactsContract.Contacts.DISPLAY_NAME));
                contact.setName(name);


                String has_phone = cursor.getString( cursor.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) ;
                if (Integer.parseInt(has_phone) > 0) {
                    // extract phone number
                    Cursor cursor1 = context.getContentResolver().query( ContactsContract.CommonDataKinds .Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds .Phone.CONTACT_ID + " = ?", new String[]{id}, null); while(cursor1.moveToNext()) {
                        String phone = cursor1.getString( cursor1.getColumnIndex( ContactsContract. CommonDataKinds. Phone.NUMBER));
                        contact.setPhone(phone);
                    }
                    cursor1.close();
                }
                map = new HashMap<String, Object>();
                map.put("id", "id: " + contact.getId());
                map.put("name", "name: " + contact.getName());
                map.put("phone", "phone: " + contact.getPhone());
                contactList.add(map);
            }
        }
                }
}


class Contact {
    private String id;
    private String lastTimeContacted;
    private String contactStatus;
    private String name = "";
    private String phone = "";

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
