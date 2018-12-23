package com.example.elie.ex3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


class Contact{
    private String name;
    private String phone;

    public Contact(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}


class MyAdapter extends ArrayAdapter<Contact> {

    private Context myContext;
    private List<Contact> contactsList = new ArrayList<>();

    public MyAdapter(Context context, ArrayList<Contact> objectList) {
        super(context, 0, objectList);
        Log.e("tag","in constructor adapter");

        myContext = context;
        contactsList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem  = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(myContext).inflate(R.layout.row_item,parent,false);

        Contact current = contactsList.get(position);

        TextView name = listItem.findViewById(R.id.textView5);
        TextView phone = listItem.findViewById(R.id.textView4);
        ImageView icon = listItem.findViewById(R.id.imageView);

        name.setText(current.getName());
        phone.setText(current.getPhone());

        if (current.getPhone().equals(""))
            icon.setImageResource(R.drawable.no_call);
        else
            icon.setImageResource(R.drawable.call);


        return listItem;
    }
}




public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SQLiteDatabase contactsDB = null;
    private Button insertButton, searchButton;
    private EditText inputName, inputPhone;
    private ListView list;
    private ArrayList<Contact> listItems;
    private MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insertButton = findViewById(R.id.insertButton);
        searchButton = findViewById(R.id.searchButton);

        inputName = findViewById(R.id.inputName);
        inputPhone = findViewById(R.id.inputPhone);

        list = findViewById(R.id.contactsList);

        insertButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);


        try
        {
            contactsDB = openOrCreateDatabase("Contacts", MODE_PRIVATE, null);
            contactsDB.execSQL("CREATE TABLE IF NOT EXISTS ContactsList (name VARCHAR primary key, phone VARCHAR);");
            Toast.makeText(this,"DB created", Toast.LENGTH_LONG).show();
        }

        catch(Exception e){
            Toast.makeText(this,"Error in creating the contact DB", Toast.LENGTH_LONG).show();
        }

        fillListAndDisplay();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.insertButton:
                insertContact();
                break;

            case R.id.searchButton:
                break;
        }
    }



    private void fillListAndDisplay(){

        listItems = new ArrayList<>();

        String sql = "SELECT * FROM contactsList";
        Cursor cursor = contactsDB.rawQuery(sql, null);

        int nameCol = cursor.getColumnIndex("name");
        int phoneCol = cursor.getColumnIndex("phone");

        cursor.moveToFirst();

        if(cursor != null && (cursor.getCount() > 0)){
            do{

                listItems.add(new Contact(cursor.getString(nameCol), cursor.getString(phoneCol)));

            }while(cursor.moveToNext());
        }

        adapter = new MyAdapter(this, listItems);
        list.setAdapter(adapter);

        //adapter.notifyDataSetChanged();
    }



    private void insertContact() {
        String name = inputName.getText().toString();
        String phone = inputPhone.getText().toString();

        if (!name.isEmpty() && !phone.isEmpty()) {

            String sql = "INSERT INTO ContactsList (name, phone) VALUES('" + name + "','" + phone + "')";
            contactsDB.execSQL(sql);

        }

        Log.e("tag", "elie created");
        fillListAndDisplay();
    }
}
