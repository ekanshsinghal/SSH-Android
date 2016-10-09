package com.techstreat.ssh_terminal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.techstreat.ssh_terminal.constants.Constants;
import com.techstreat.ssh_terminal.databaseutils.Preference;

public class AddHost extends AppCompatActivity {

    private ViewGroup contentView;
    private Preference pref;
    Preference addP=null;
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        contentView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_add_host, null);

        pref = getIntent().getParcelableExtra(Constants.PREFERENCE_PARCEABLE);

        if (pref != null)
        {
            fillForm();
        }

        Button btn = (Button) contentView.findViewById(R.id.button_save);
        setupAddandEditButton(btn);

        setContentView(contentView);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void fillForm()
    {
        EditText edit;

        edit = (EditText) contentView.findViewById(R.id.hostNameField);
        edit.setText(pref.getHostName());
        edit = (EditText) contentView.findViewById(R.id.usernameField);
        edit.setText(pref.getUsername());
        edit = (EditText) contentView.findViewById(R.id.connectionNameField);
        edit.setText(pref.getName());

        edit = (EditText) contentView.findViewById(R.id.passwordField);
        edit.setText(pref.getPassword());
        edit = (EditText) contentView.findViewById(R.id.portField);
        edit.setText(String.valueOf(pref.getPort()));
    }

    private void findError(String name,String host,String userName,String ports,String passwordOrKey)
    {
        AlertDialog.Builder ab = new AlertDialog.Builder(AddHost.this);
        ab.setTitle("Alert!!!");
        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if(name=="")
        {
            ab.setMessage("Please enter a Connection Name");
            ab.show();
        }
        else if(host=="")
        {
            ab.setMessage("Please enter Host Name/IP");
            ab.show();
        }else if(userName=="")
        {
            ab.setMessage("Please enter Pi Username");
            ab.show();
        } else if (ports=="") {
            ab.setMessage("Please enter Port No.(default 22):");
            ab.show();
        } else if(passwordOrKey=="")
        {
            ab.setMessage("Please enter Pi Password");
            ab.show();
        }
    }

    private void setupAddandEditButton(Button btn)
    {
        btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                edit = (EditText) contentView.findViewById(R.id.connectionNameField);
                String name = edit.getText().toString();
                edit = (EditText) contentView.findViewById(R.id.hostNameField);
                String host = edit.getText().toString();
                edit = (EditText) contentView.findViewById(R.id.usernameField);
                String userName = edit.getText().toString();
                edit = (EditText) findViewById(R.id.portField);
                String ports=null;
                if (edit.getText().toString()!="")
                    ports = edit.getText().toString();
                edit = (EditText) contentView.findViewById(R.id.passwordField);
                String passwordOrKey = edit.getText().toString();

                if (name!="" && host!="" && userName!="" && ports!="" && passwordOrKey!="" && ports!=null)
                {
                    Integer port=Integer.parseInt(ports);
                    addP=new Preference(passwordOrKey,name, host, userName, port);
                } else findError(name,host,userName,ports,passwordOrKey);

                if(addP == null)
                {
                    return;
                }
                else
                {
                    createNewPreference(addP);
                    finish();
                }
            }
        });
    }

    private void createNewPreference(Preference pref)
    {
        MainActivity.addPreference(pref);
    }
}
