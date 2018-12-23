package com.example.elie.ex3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

        myContext = context;
        contactsList = objectList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem  = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(myContext).inflate(R.layout.row_item,parent,false);

        final Contact current = contactsList.get(position);

        TextView name = listItem.findViewById(R.id.textView5);
        TextView phone = listItem.findViewById(R.id.textView4);
        ImageView icon = listItem.findViewById(R.id.imageView);

        name.setText(current.getName());
        phone.setText(current.getPhone());

        if (current.getPhone().equals(""))
            icon.setImageResource(R.drawable.no_call);
        else
            icon.setImageResource(R.drawable.call);

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current.getPhone().equals("") == false){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + current.getPhone().toString()));
                    myContext.startActivity(intent);
                }
            }
        });

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
            contactsDB.execSQL("CREATE TABLE IF NOT EXISTS ContactsList (name TEXT UNIQUE, phone TEXT);");
        } catch(Exception e){
            Toast.makeText(this,"Error in creating the contacts DB", Toast.LENGTH_LONG).show();
        }

        fillListAndDisplay(false, "","");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.insertButton:
                insertContact();
                break;

            case R.id.searchButton:
                searchInDB();
                break;
        }
    }

    private void searchInDB() {
        String name = inputName.getText().toString();
        String phone = inputPhone.getText().toString();


        fillListAndDisplay(true, name, phone);
    }




    private void insertContact() {
        String name = inputName.getText().toString();
        String phone = inputPhone.getText().toString();

        if (!name.isEmpty()) {

            String sql = "INSERT OR REPLACE INTO ContactsList (name, phone) VALUES('" + name + "','" + phone + "');";

            try{
                contactsDB.execSQL(sql);
            }catch (Exception e){
            }
        }

        fillListAndDisplay(false, "","");
    }






    private void fillListAndDisplay(Boolean search, String name, String phone){

        listItems = new ArrayList<>();
        String sql;

        if (search)
            sql = "SELECT * FROM ContactsList WHERE INSTR(name,'" + name + "')<>0 AND INSTR(phone,'" + phone + "')<>0;";
        else
            sql = "SELECT * FROM contactsList";

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
        adapter.notifyDataSetChanged();
    }
}
