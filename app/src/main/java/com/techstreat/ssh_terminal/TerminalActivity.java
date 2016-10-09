package com.techstreat.ssh_terminal;

import android.app.FragmentManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.techstreat.ssh_terminal.asyncNetworkTasks.IConnectionNotifier;
import com.techstreat.ssh_terminal.asyncNetworkTasks.SshConnectTask;
import com.techstreat.ssh_terminal.constants.Constants;
import com.techstreat.ssh_terminal.databaseutils.Preference;
import com.techstreat.ssh_terminal.preferences.SharedPreferencesManager;
import com.techstreat.ssh_terminal.sshutils.SessionUserInfo;
import com.techstreat.ssh_terminal.sshutils.ShellConnection;
import com.techstreat.ssh_terminal.sshutils.SshConnection;
import com.techstreat.ssh_terminal.terminal.TerminalSession;
import com.techstreat.ssh_terminal.terminal.TerminalView;

public class TerminalActivity extends AppCompatActivity implements IConnectionNotifier{

    private SshConnection conn;
    private boolean keyboardShown;

    private SharedPreferencesManager prefInstance;

    public static TerminalSession terminalSession;
    private TerminalView view;

    private RetainedTerminal retainedTerminal;
    final static String log = "TerminalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefInstance = SharedPreferencesManager.getInstance();
        keyboardShown = false;

        FragmentManager fm = getFragmentManager();
        String ter = "terminal";
        retainedTerminal = (RetainedTerminal) fm.findFragmentByTag(ter);

        if (retainedTerminal != null)
        {
            terminalSession = retainedTerminal.getTerminalSession();
            view  = (TerminalView) findViewById(R.id.emulatorView);
            view.copyOld(retainedTerminal.getTerminalView());
            conn = terminalSession.getConnection();

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            view.setDensity(metrics);

            view.attachSession(terminalSession);
            connectionResult(true);
        }

        else
        {
            view  = (TerminalView)findViewById(R.id.emulatorView);

            retainedTerminal = new RetainedTerminal();
            fm.beginTransaction().add(retainedTerminal, ter).commit();

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            view.setDensity(metrics);

            Intent intent = getIntent();
            Preference p = intent.getParcelableExtra(Constants.PREFERENCE_PARCEABLE);

            SessionUserInfo user = new SessionUserInfo(p.getHostName(), p.getUsername(), p.getPort(), this);
            user.setPassword(p.getPassword());

            ShellConnection connection = new ShellConnection(user);
            terminalSession = new TerminalSession(connection);
            view.attachSession(terminalSession);

            retainedTerminal.setTerminalSession(terminalSession);
            retainedTerminal.setTerminalView(view);

            conn = connection;
            //give the connection to the view so he can update Pty size on the fly
            view.addConnection(connection);

            //view.setUseCookedIME(true);

            prefInstance.setPreferencesonShellConnection(conn);
            prefInstance.setPreferencesTerminal(view);
            prefInstance.setPreferenceSession(terminalSession);

            SshConnectTask task = new SshConnectTask(this);
            task.execute(conn);
        }

        Button Ctrl = (Button) findViewById(R.id.Ctrl);
        setupControlButton(Ctrl);

        Button Tab = (Button) findViewById(R.id.tab);
        setupTabButton(Tab);

        Button Esc = (Button) findViewById(R.id.esc);
        setupEscButton(Esc);

        ImageButton leftButton = (ImageButton) findViewById(R.id.leftButton);
        setupLeftButton(leftButton);

        ImageButton rightButton = (ImageButton) findViewById(R.id.rightButton);
        setupRightButton(rightButton);

        ImageButton upButton = (ImageButton) findViewById(R.id.upButton);
        setupUpButton(upButton);

        ImageButton keyboardButton = (ImageButton) findViewById(R.id.keyboardButton);
        setupKeyboardButton(keyboardButton);
    }

    @Override
    public void onBackPressed() {
        if(!conn.isConnected())
        {
            try
            {
                //kill the connecting thread
                if(conn != null)
                {
                    conn.disconnect();
                }
                super.onBackPressed();
            }
            catch(Exception e)
            {
                Log.d(log, "exception while disconnecting");
            }
        }
        else
        {
            AlertDialog.Builder ab=new AlertDialog.Builder(TerminalActivity.this);
            ab.setMessage("Are you sure you would like to disconnect?");
            ab.setTitle("Disconnect");
            ab.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    conn.disconnect();
                    finish();
                }
            });
            ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ab.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.terminal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void connectionResult(boolean result) {
        if(result == false)
        {
            setTitle("Error...");
            return;
        }
        setTitle(conn.getName());
        view.refreshScreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        boolean ret = true;
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            view.increaseSize();
        }
        else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN))
        {
            view.reduceSize();
        }
        else
        {
            ret = super.onKeyDown(keyCode, event);
        }
        return ret;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        boolean ret = true;
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
        }
        else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN))
        {
        }
        else
        {
            ret = super.onKeyUp(keyCode, event);
        }
        return ret;
    }

    private void setupControlButton(Button ctrl)
    {
        ctrl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                view.sendControlKey();
            }
        });
    }

    private void setupEscButton(Button esc)
    {
        esc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                terminalSession.write(Constants.ESC);
            }
        });
    }

    private void setupTabButton(Button tab)
    {
        tab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                terminalSession.write(Constants.TAB);
            }
        });
    }

    private void setupUpButton(ImageButton upButton)
    {
        upButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                terminalSession.write(Constants.UP);
            }
        });
    }

    private void setupLeftButton(ImageButton leftButton)
    {
        leftButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                terminalSession.write(Constants.LEFT);
            }
        });
    }

    private void setupRightButton(ImageButton rightButton)
    {
        rightButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                terminalSession.write(Constants.RIGHT);
            }
        });
    }

    private void setupKeyboardButton(final ImageButton keyboard)
    {
        keyboard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handleKeyboard();
            }
        });
    }

    private void handleKeyboard()
    {
        final InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
        if(keyboardShown)
        {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            keyboardShown = false;
        }
        else
        {
            imm.showSoftInput(view, 0);
            keyboardShown = true;
        }
        view.updateSize(true);
        view.refreshScreen();
    }
}
