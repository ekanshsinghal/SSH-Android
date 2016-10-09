package com.techstreat.ssh_terminal;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.techstreat.ssh_terminal.constants.Constants;
import com.techstreat.ssh_terminal.databaseutils.Preference;

import java.util.ArrayList;

public class HostList extends AppCompatActivity {

    private ListView listView;
    private Preference selectedPref;

    private View lastSelectedview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup contentView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_host_list, null);
        listView = (ListView) contentView.findViewById(R.id.list_view);

        Button btnAdd = (Button) contentView.findViewById(R.id.button_add);
        setupAddButton(btnAdd);

        Button btnDeleteList = (Button) contentView.findViewById(R.id.button_delete);
        setupDeleteButton(btnDeleteList);

        setContentView(contentView);
        selectedPref = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectedPref = null;
        setupListView(listView);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void setupListView(final ListView lv) {
        final ArrayList<String> connname=new ArrayList();
        final Cursor cursor= MainActivity.sqLiteDatabase.rawQuery("select * from "+MainActivity.tablepref,null);
        while (cursor.moveToNext())
        {
            String cname=cursor.getString(0);
            connname.add(cname);
        }
        ArrayAdapter arrayAdapter=new ArrayAdapter(HostList.this,android.R.layout.simple_list_item_1,connname);
        lv.setAdapter(arrayAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Preference preference=new Preference(cursor.getString(3),cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getInt(4));
                selectedPref = preference;
                if (lastSelectedview != null) {
                    lastSelectedview.setBackgroundColor(Color.TRANSPARENT);
                }
                lastSelectedview = view;
            }
        }
        );
    }


    private void setupAddButton(Button btnAdd) {
        final Activity activity = this;
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddHost.class);
                startActivity(intent);
            }
        });
    }

    private void setupDeleteButton(Button btnDeleteList) {

        btnDeleteList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder ab=new AlertDialog.Builder(HostList.this);
                if (selectedPref != null) {
                    ab.setMessage("Are you sure you would like to delete the connection: ");
                    ab.setTitle("Warning!!!");
                    ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            deletePreference(selectedPref);
                        }
                    });
                    ab.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ab.show();
                } else {
                    ab.setMessage("Please select a saved connection to delete!");
                    ab.setTitle("Not Selected ");
                    ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ab.show();
                }
            }
        });
    }

    private void deletePreference(Preference pref) {
        MainActivity.deletePreference(pref);
        finish();
        startActivity(getIntent());
    }
}