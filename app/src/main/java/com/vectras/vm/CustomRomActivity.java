package com.vectras.vm;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vectras.qemu.MainSettingsManager;
import com.vectras.vm.Fragment.CreateImageDialogFragment;
import com.vectras.vm.MainRoms.DataMainRoms;
import com.vectras.vm.logger.VectrasStatus;
import com.vectras.vm.utils.FileUtils;
import com.vectras.vm.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CustomRomActivity extends AppCompatActivity {

    public static TextInputEditText title;
    public TextInputEditText icon;
    public static TextInputEditText drive;
    public TextInputEditText cdrom;
    public TextInputEditText qemu;
    public Button addRomBtn;
    boolean iseditparams = false;
    public String previousName = "";
    public String secondVMdirectory = "";
    public boolean addromnowdone = false;

    public ProgressBar loadingPb;

    public static CustomRomActivity activity;

    private boolean isFilled(TextInputEditText TXT) {
        if (TXT.getText().toString().trim().length() > 0)
            return true;
        else
            return false;
    }

    boolean modify;

    public static DataMainRoms current;

    private AlertDialog alertDialog;

    private ArrayList<HashMap<String, Objects>> mmap = new ArrayList<>();

    private String contentjson = "";

    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (!modify)
            menu.add(1, 1, 1, "custom rom").setShortcut('3', 'c').setIcon(R.drawable.input_circle).setShowAsAction(1);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                }

                startActivityForResult(intent, 0);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_rom);
        activity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        loadingPb = findViewById(R.id.loadingPb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Rom Options");
        //AdView mAdView = findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        title = findViewById(R.id.title);
        icon = findViewById(R.id.icon);
        drive = findViewById(R.id.drive);
        cdrom = findViewById(R.id.cdrom);
        qemu = findViewById(R.id.qemu);
        TextInputLayout iconLayout = findViewById(R.id.iconField);
        TextInputLayout driveLayout = findViewById(R.id.driveField);
        TextInputLayout cdromLayout = findViewById(R.id.cdromField);
        TextInputLayout qemuLayout = findViewById(R.id.qemuField);
        TextView arch = findViewById(R.id.textArch);
        arch.setText(MainSettingsManager.getArch(this));
        ImageView ivIcon = findViewById(R.id.ivIcon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                }

                startActivityForResult(intent, 1001);
            }
        });
        iconLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                }

                startActivityForResult(intent, 1001);
            }
        });
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                }

                startActivityForResult(intent, 1002);
            }
        });
        driveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                }

                startActivityForResult(intent, 1002);
            }
        });

        driveLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File vDir = new File(com.vectras.vm.AppConfig.maindirpath + "IMG");
                if (!vDir.exists()) {
                    vDir.mkdirs();
                }
                CreateImageDialogFragment dialogFragment = new CreateImageDialogFragment();
                dialogFragment.customRom = true;
                dialogFragment.show(getSupportFragmentManager(), "CreateImageDialogFragment");
            }
        });

        View.OnClickListener cdromClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cdrom.getText().length() > 0) {
                    AlertDialog ad;
                    ad = new AlertDialog.Builder(activity, R.style.MainDialogTheme).create();
                    ad.setTitle("REPLACE ISO");
                    ad.setMessage("there is iso imported you want to replace it?");
                    ad.setButton(Dialog.BUTTON_POSITIVE, "REPLACE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("*/*");

                            // Optionally, specify a URI for the file that should appear in the
                            // system file picker when it loads.
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                            }

                            startActivityForResult(intent, 1003);
                            return;
                        }
                    });
                    ad.setButton(Dialog.BUTTON_NEGATIVE, "REMOVE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            File isoFile = new File(cdrom.getText().toString());
                            if (isoFile.exists()) {
                                isoFile.delete();
                            }
                            cdrom.setText("");

                            Pattern pattern = Pattern.compile("-drive index=1,media=cdrom,file='([^']*)'");
                            Matcher matcher = pattern.matcher(qemu.getText().toString());

                            if (matcher.find()) {
                                // Replace the entire -drive ... file='...' parameter with an empty string
                                String modifiedQemuText = qemu.getText().toString().replace(matcher.group(0), "");
                                qemu.setText(modifiedQemuText);
                            } else {
                                // Handle the case where the -drive parameter doesn't exist
                                // This could involve logging the issue, displaying a message to the user, etc.
                            }
                        }
                    });
                    ad.show();
                } else {
                    Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");

                    // Optionally, specify a URI for the file that should appear in the
                    // system file picker when it loads.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                    }

                    startActivityForResult(intent, 1003);
                }
            }
        };

        cdrom.setOnClickListener(cdromClickListener);
        cdromLayout.setOnClickListener(cdromClickListener);

        addRomBtn = findViewById(R.id.addRomBtn);
        addRomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty()) {
                    VectrasApp.oneDialog(getResources().getString(R.string.oops), getResources().getString(R.string.need_set_name),true, false, activity);
                } else if ((!drive.getText().toString().isEmpty()) || (!cdrom.getText().toString().isEmpty())) {
                    startCreateVM();
                } else {
                    if (VectrasApp.isHaveADisk(qemu.getText().toString())) {
                        startCreateVM();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.MainDialogTheme).create();
                        alertDialog.setTitle(getResources().getString(R.string.problem_has_been_detected));
                        alertDialog.setMessage(getResources().getString(R.string.you_have_not_added_any_storage_devices));
                        alertDialog.setCancelable(true);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.continuetext), (dialog, which) -> {
                            startCreateVM();
                        });
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), (dialog, which) -> {

                        });
                        alertDialog.show();
                    }
                }
            }
        });

        qemu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iseditparams = true;
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditActivity.class);
                intent.putExtra("content", Objects.requireNonNull(qemu.getText()).toString());
                startActivity(intent);
            }
        });

        qemuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iseditparams = true;
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditActivity.class);
                intent.putExtra("content", Objects.requireNonNull(qemu.getText()).toString());
                startActivity(intent);
            }
        });

        TextView textName = findViewById(R.id.textName);
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textName.setText(title.getText().toString());

                if (!Objects.requireNonNull(icon.getText()).toString().isEmpty())
                    return;

                VectrasApp.setIconWithName(ivIcon, title.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        title.addTextChangedListener(afterTextChangedListener);
        icon.addTextChangedListener(afterTextChangedListener);
        drive.addTextChangedListener(afterTextChangedListener);
        qemu.addTextChangedListener(afterTextChangedListener);


        TextInputLayout tIQemu = findViewById(R.id.qemuField);
        tIQemu.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qcc = "android-app://com.anbui.cqcm.app";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(qcc));
                startActivity(i);
            }
        });

        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                }

                startActivityForResult(intent, 1001);
            }
        });

        modify = getIntent().getBooleanExtra("MODIFY", false);
        if (modify) {
            addRomBtn.setText(R.string.save_changes);
            title.setText(current.itemName);
            icon.setText(current.itemIcon);
            drive.setText(current.itemPath);

            Pattern pattern = Pattern.compile("-drive index=1,media=cdrom,file='([^']*)'");
            Matcher matcher = pattern.matcher(current.itemExtra);

            if (matcher.find()) {
                String cdromPath = matcher.group(1);
                cdrom.setText(cdromPath);
            }

            qemu.setText(current.itemExtra);

            File imgFile = new  File(icon.getText().toString());

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivIcon.setImageBitmap(myBitmap);
            } else {
                VectrasApp.setIconWithName(ivIcon, current.itemName);
            }
            previousName = current.itemName;
        } else {
            if (getIntent().hasExtra("addromnow")) {
                title.setText(getIntent().getStringExtra("romname"));
                if (getIntent().getStringExtra("romextra").isEmpty()) {
                    setDefault();
                } else {
                    qemu.setText(getIntent().getStringExtra("romextra"));
                }
                icon.setText(getIntent().getStringExtra("romicon"));
                if (!getIntent().getStringExtra("romicon").isEmpty()) {
                    File imgFile = new File(getIntent().getStringExtra("romicon"));
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ivIcon.setImageBitmap(myBitmap);
                    }
                }
                if (getIntent().getStringExtra("romfilename").endsWith(".cvbi")) {
                    importCVBI(getIntent().getStringExtra("rompath"), getIntent().getStringExtra("romfilename"));
                } else {
                    drive.setText(getIntent().getStringExtra("rompath"));
                    addromnowdone = true;
                }
            } else {
                title.setText("New VM");
                setDefault();
            }
        }
        VectrasApp.prepareDataForAppConfig(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkpermissions();
        if (iseditparams) {
            iseditparams = false;
            qemu.setText(EditActivity.result);
        }
    }

    public static class RomsJso extends JSONObject {

        public JSONObject makeJSONObject(String imgName, String imgIcon, String imgArch, String imgPath, String imgExtra) {

            JSONObject obj = new JSONObject();

            try {
                obj.put("imgName", imgName);
                obj.put("imgIcon", imgIcon);
                obj.put("imgArch", imgArch);
                obj.put("imgPath", imgPath);
                obj.put("imgExtra", imgExtra);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return obj;
        }

    }

    byte[] data;

    public String getPath(Uri uri) {
        return FileUtils.getPath(this, uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent ReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, ReturnedIntent);

        if (title.getText().toString().length() > 0) {
            File romDir = new File(AppConfig.maindirpath + "roms/" + title.getText().toString());
            romDir.mkdirs();
        }

        LinearLayout custom = findViewById(R.id.custom);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            Uri content_describer = ReturnedIntent.getData();
            TextInputEditText icon = findViewById(R.id.icon);
            File selectedFilePath = new File(getPath(content_describer));
            ImageView ivIcon = findViewById(R.id.ivIcon);
            loadingPb.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileInputStream File = null;
                    Bitmap selectedImage = null;
                    try {
                        File = (FileInputStream) getContentResolver().openInputStream(content_describer);
                        selectedImage = BitmapFactory.decodeStream(File);
                        Bitmap finalSelectedImage = selectedImage;
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                ivIcon.setImageBitmap(finalSelectedImage);
                            }
                        };
                        activity.runOnUiThread(runnable);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            try {
                                SaveImage(selectedImage, new File(AppConfig.maindirpath + "roms/" + title.getText().toString()), title.getText().toString() + "-" + selectedFilePath.getName());
                            } finally {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingPb.setVisibility(View.GONE);
                                        icon.setText(AppConfig.maindirpath + "roms/" + title.getText().toString() + "/" + title.getText().toString() + "-" + selectedFilePath.getName());
                                    }
                                };
                                activity.runOnUiThread(runnable);
                                File.close();
                            }
                        } catch (IOException e) {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    loadingPb.setVisibility(View.GONE);
                                }
                            };
                            activity.runOnUiThread(runnable);
                            UIUtils.UIAlert(activity, "error", e.toString());
                        }

                    }
                }
            }).start();
        } else if (requestCode == 1002 && resultCode == RESULT_OK) {
            Uri content_describer = ReturnedIntent.getData();
            File selectedFilePath = new File(getPath(content_describer));
            drive.setText(AppConfig.maindirpath + "roms/" + title.getText().toString() + "/" + selectedFilePath.getName());
            loadingPb.setVisibility(View.VISIBLE);
            custom.setVisibility(View.GONE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileInputStream File = null;
                    try {
                        File = (FileInputStream) getContentResolver().openInputStream(content_describer);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        try {
                            OutputStream out = new FileOutputStream(new File(AppConfig.maindirpath + "roms/" + title.getText().toString() + "/" + selectedFilePath.getName()));
                            try {
                                // Transfer bytes from in to out
                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = File.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }
                            } finally {
                                out.close();
                            }
                        } finally {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    loadingPb.setVisibility(View.GONE);
                                    custom.setVisibility(View.VISIBLE);
                                }
                            };
                            activity.runOnUiThread(runnable);
                            File.close();
                        }
                    } catch (IOException e) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                loadingPb.setVisibility(View.GONE);
                                custom.setVisibility(View.VISIBLE);
                                UIUtils.UIAlert(activity, "error", e.toString());
                            }
                        };
                        activity.runOnUiThread(runnable);
                    }
                }
            }).start();
        } else if (requestCode == 1003 && resultCode == RESULT_OK) {
            Uri content_describer = ReturnedIntent.getData();
            File selectedFilePath = new File(getPath(content_describer));
            if (selectedFilePath.getName().endsWith(".iso")) {
                String cdromPath = AppConfig.maindirpath + "roms/" + title.getText().toString() + "/" + selectedFilePath.getName();
                cdrom.setText(cdromPath);

                String qemuText = qemu.getText().toString();
                String cdromParam = "-drive index=1,media=cdrom,file='" + cdromPath + "'";

                Pattern pattern = Pattern.compile("-drive index=1,media=cdrom,file='(.*?)'");
                Matcher matcher = pattern.matcher(qemuText);

                if (!qemuText.contains("-drive index=1,media=cdrom,file=")) {
                    qemu.append(" " + cdromParam);
                } else {
                    if (matcher.find()) {
                        String cdromPath1 = matcher.group(1);
                        qemu.setText(qemuText.replace(cdromPath1, cdromPath)); // Fixed this line to actually change the text of `qemu`
                    }
                }
                loadingPb.setVisibility(View.VISIBLE);
                custom.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileInputStream File = null;
                        try {
                            File = (FileInputStream) getContentResolver().openInputStream(content_describer);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            try {
                                OutputStream out = new FileOutputStream(new File(AppConfig.maindirpath + "roms/" + title.getText().toString() + "/" + selectedFilePath.getName()));
                                try {
                                    // Transfer bytes from in to out
                                    byte[] buf = new byte[1024];
                                    int len;
                                    while ((len = File.read(buf)) > 0) {
                                        out.write(buf, 0, len);
                                    }
                                } finally {
                                    out.close();
                                }
                            } finally {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingPb.setVisibility(View.GONE);
                                        custom.setVisibility(View.VISIBLE);
                                    }
                                };
                                activity.runOnUiThread(runnable);
                                File.close();
                            }
                        } catch (IOException e) {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    loadingPb.setVisibility(View.GONE);
                                    custom.setVisibility(View.VISIBLE);
                                    UIUtils.UIAlert(activity, "error", e.toString());
                                }
                            };
                            activity.runOnUiThread(runnable);
                        }
                    }
                }).start();
            } else
                UIUtils.UIAlert(activity, "please select iso file to continue.", "File not supported");
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            Uri content_describer = ReturnedIntent.getData();
            File selectedFilePath = new File(getPath(content_describer));
            importCVBI(selectedFilePath.getPath(), selectedFilePath.getName());
        } else if (requestCode == 1000 && resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    private static void SaveImage(Bitmap finalBitmap, File imgDir, String name) {
        File myDir = imgDir;
        myDir.mkdirs();

        String fname = name;
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void errorjsondialog() {
        if (isFileExists(AppConfig.romsdatajson)) {
            if (!VectrasApp.checkJSONIsNormal(AppConfig.romsdatajson)) {
                alertDialog = new AlertDialog.Builder(activity, R.style.MainDialogTheme).create();
                alertDialog.setTitle(getResources().getString(R.string.oops));
                alertDialog.setMessage(getResources().getString(R.string.need_fix_json_before_create));
                alertDialog.setCancelable(true);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.delete_all), (dialog, which) -> {
                    VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", "[]");
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), (dialog, which) -> {

                });
                alertDialog.show();
            }

        } else {

        }
    }

    private String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(filePath);
             BufferedReader reader = new BufferedReader(new
                     InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();

    }

    private boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    private void checkpermissions() {
        if (!VectrasApp.checkpermissionsgranted(activity, true)) {

        }
    }

    private void startCreateVM() {
        errorjsondialog();

        File isoFile = new File(cdrom.getText().toString());
        if (isoFile.exists() && !qemu.getText().toString().contains("-drive index=1,media=cdrom,file=")) {
            isoFile.delete();
        }

        if (modify) {

            int position = getIntent().getIntExtra("POS", 0);
            final File jsonFile = new File(AppConfig.maindirpath + "roms-data" + ".json");
            current.itemDrv1 = drive.getText().toString();
            current.itemExtra = qemu.getText().toString();
            try {
                JSONObject jObj = MainActivity.jArray.getJSONObject(position);
                jObj.put("imgName", title.getText().toString());
                jObj.put("imgIcon", icon.getText().toString());
                jObj.put("imgPath", drive.getText().toString());
                jObj.put("imgExtra", qemu.getText().toString());

                MainActivity.jArray.put(position, jObj);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                Writer output = null;
                output = new BufferedWriter(new FileWriter(jsonFile));
                output.write(MainActivity.jArray.toString());
                output.close();
            } catch (Exception e) {
                UIUtils.toastLong(MainActivity.activity, e.toString());
            } finally {
                MainActivity.loadDataVbi();
                finish();
                //activity.startActivity(new Intent(activity, SplashActivity.class));
            }
        } else {
            String CREDENTIAL_SHARED_PREF = "settings_prefs";
            SharedPreferences credentials = activity.getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = credentials.edit();
            editor.putBoolean("isFirstLaunch", Boolean.TRUE);
            editor.apply();
            loadingPb.setVisibility(View.VISIBLE);
            final File jsonFile = new File(AppConfig.maindirpath + "roms-data" + ".json");
            RomsJso obj = new RomsJso();
            if (jsonFile.exists()) {
                try {
                    List<DataMainRoms> data = new ArrayList<>();
                    JSONArray jArray = null;
                    jArray = new JSONArray(FileUtils.readFromFile(MainActivity.activity, jsonFile));

                    try {
                        // Extract data from json and store into ArrayList as class objects
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            DataMainRoms romsMainData = new DataMainRoms();
                            romsMainData.itemName = json_data.getString("imgName");
                            romsMainData.itemIcon = json_data.getString("imgIcon");
                            romsMainData.itemPath = json_data.getString("imgPath");
                            romsMainData.itemExtra = json_data.getString("imgExtra");
                            data.add(romsMainData);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.activity, e.toString(), Toast.LENGTH_LONG).show();
                    }

                    JSONObject jsonObject = obj.makeJSONObject(title.getText().toString(), icon.getText().toString(), MainSettingsManager.getArch(activity), drive.getText().toString(), qemu.getText().toString());
                    jArray.put(jsonObject);
                    try {
                        Writer output = null;
                        output = new BufferedWriter(new FileWriter(jsonFile));
                        //output.write(jArray.toString().replace("\\", "").replace("//", "/"));
                        output.write(jArray.toString());
                        output.close();
                    } catch (Exception e) {
                        UIUtils.toastLong(activity, e.toString());
                        loadingPb.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    loadingPb.setVisibility(View.GONE);
                    throw new RuntimeException(e);
                }
            } else {
                JSONObject jsonObject = obj.makeJSONObject(title.getText().toString(), icon.getText().toString(), MainSettingsManager.getArch(activity), drive.getText().toString(), qemu.getText().toString());
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                try {
                    Writer output = null;
                    output = new BufferedWriter(new FileWriter(jsonFile));
                    //output.write(jsonArray.toString().replace("\\", "").replace("//", "/"));
                    output.write(jsonArray.toString());
                    output.close();
                } catch (Exception e) {
                    UIUtils.toastLong(activity, e.toString());
                }
                VectrasStatus.logInfo("Welcome to Vectras ♡");
            }
            if (getIntent().hasExtra("addromnow")) {
                RomsManagerActivity.isFinishNow = true;
            }

            finish();
            //activity.startActivity(new Intent(activity, SplashActivity.class));
        }
        if (!previousName.isEmpty() && !title.getText().toString().equals(previousName)) {
            if (VectrasApp.isFileExists(AppConfig.maindirpath + "roms/" + previousName + "/vmID.txt")) {
                VectrasApp.copyAFile(AppConfig.maindirpath + "roms/" + previousName + "/vmID.txt", AppConfig.maindirpath + "roms/" + title.getText().toString() + "/vmID.txt");
            } else {
                VectrasApp.writeToFile(AppConfig.maindirpath + "roms/" + title.getText().toString(), "/vmID.txt", VectrasApp.ramdomVMID());
                VectrasApp.copyAFile(AppConfig.maindirpath + "roms/" + title.getText().toString() + "/vmID.txt", AppConfig.maindirpath + previousName + "/vmID.txt");
            }
        } else {
            VectrasApp.writeToFile(AppConfig.maindirpath + "roms/" + title.getText().toString(), "/vmID.txt", VectrasApp.ramdomVMID());
        }
        if ((!secondVMdirectory.isEmpty()) && VectrasApp.isFileExists(AppConfig.maindirpath + "roms/" + title.getText().toString() + "/vmID.txt")) {
            if (!(AppConfig.maindirpath + "roms/" + title.getText().toString()).equals(secondVMdirectory)) {
                VectrasApp.copyAFile(AppConfig.maindirpath + "roms/" + title.getText().toString() + "/vmID.txt", secondVMdirectory + "/vmID.txt");
            }
       }
    }

    private void setDefault() {
        String defQemuParams;
        if (AppConfig.getSetupFiles().contains("arm64-v8a") || AppConfig.getSetupFiles().contains("x86_64")) {
            switch (MainSettingsManager.getArch(MainActivity.activity)) {
                case "ARM64":
                    defQemuParams = "-M virt,virtualization=true -cpu cortex-a76 -accel tcg,thread=multi -net nic,model=e1000 -net user -device nec-usb-xhci -device usb-kbd -device usb-mouse -device VGA";
                    break;
                case "PPC":
                    defQemuParams = "-M mac99 -cpu g4 -accel tcg,thread=multi -smp 1";
                    break;
                case "I386":
                    defQemuParams = "-M pc -cpu qemu32,+avx -accel tcg,thread=multi -smp 4 -vga std -netdev user,id=usernet -device e1000,netdev=usernet";
                    break;
                default:
                    defQemuParams = "-M pc -cpu qemu64,+avx -accel tcg,thread=multi -smp 4 -vga std -netdev user,id=usernet -device e1000,netdev=usernet";
                    break;
            }
        } else {
            switch (MainSettingsManager.getArch(MainActivity.activity)) {
                case "ARM64":
                    defQemuParams = "-M virt -cpu cortex-a76 -net nic,model=e1000 -net user -device nec-usb-xhci -device usb-kbd -device usb-mouse -device VGA";
                    break;
                case "PPC":
                    defQemuParams = "-M mac99 -cpu g4 -smp 1";
                    break;
                case "I386":
                    defQemuParams = "-M pc -cpu qemu32,+avx -smp 4 -vga std -netdev user,id=usernet -device e1000,netdev=usernet";
                    break;
                default:
                    defQemuParams = "-M pc -cpu qemu64,+avx -smp 4 -vga std -netdev user,id=usernet -device e1000,netdev=usernet";
                    break;
            }
        }
        qemu.setText(defQemuParams);
    }

    private void importCVBI(String _filepath, String _filename) {
        LinearLayout custom = findViewById(R.id.custom);
        ImageView ivIcon = findViewById(R.id.ivIcon);
        if (_filepath.endsWith(".cvbi")) {
            //Error code: CR_CVBI1
            if (!VectrasApp.isFileExists(_filepath)) {
                if (getIntent().hasExtra("addromnow")) {
                    VectrasApp.oneDialog(getResources().getString(R.string.oops), getResources().getString(R.string.error_CR_CVBI1), false, true, this);
                } else {
                    VectrasApp.oneDialog(getResources().getString(R.string.oops), getResources().getString(R.string.error_CR_CVBI1), true, false, this);
                }
                return;
            }
            loadingPb.setVisibility(View.VISIBLE);
            custom.setVisibility(View.GONE);
            ivIcon.setEnabled(false);
            Thread t = new Thread() {
                public void run() {
                    FileInputStream zipFile = null;
                    try {
                        zipFile = (FileInputStream) getContentResolver().openInputStream((Uri.fromFile(new File(_filepath))));
                        File targetDirectory = new File(AppConfig.maindirpath + "roms/" + _filename.replace(".cvbi", ""));
                        ZipInputStream zis = null;
                        zis = new ZipInputStream(
                                new BufferedInputStream(zipFile));
                        try {
                            ZipEntry ze;
                            int count;
                            byte[] buffer = new byte[8192];
                            while ((ze = zis.getNextEntry()) != null) {
                                File file = new File(targetDirectory, ze.getName());
                                File dir = ze.isDirectory() ? file : file.getParentFile();
                                if (!dir.isDirectory() && !dir.mkdirs())
                                    throw new FileNotFoundException("Failed to ensure directory: " +
                                            dir.getAbsolutePath());
                                if (ze.isDirectory())
                                    continue;
                                try (FileOutputStream fout = new FileOutputStream(file)) {
                                    while ((count = zis.read(buffer)) != -1)
                                        fout.write(buffer, 0, count);
                                }
                        /* if time should be restored as well
                        long time = ze.getTime();
                        if (time > 0)
                            file.setLastModified(time);
                        */
                            }
                        } catch (IOException e) {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    UIUtils.toastLong(activity, e.toString());
                                }
                            };
                            activity.runOnUiThread(runnable);
                        } finally {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    loadingPb.setVisibility(View.GONE);
                                    custom.setVisibility(View.VISIBLE);
                                    ivIcon.setEnabled(true);
                                    try {
                                        secondVMdirectory = AppConfig.maindirpath + "roms/" + _filename.replace(".cvbi", "");
                                        if (!VectrasApp.isFileExists(AppConfig.maindirpath + "roms/" + _filename.replace(".cvbi", "") + "/rom-data.json")) {
                                            String _getDiskFile = VectrasApp.quickScanDiskFileInFolder(AppConfig.maindirpath + "roms/" + _filename.replace(".cvbi", ""));
                                            if (!_getDiskFile.isEmpty()) {
                                                //Error code: CR_CVBI2
                                                if (getIntent().hasExtra("addromnow") && !addromnowdone) {
                                                    addromnowdone = true;
                                                    title.setText(getIntent().getStringExtra("romname"));
                                                    if (getIntent().getStringExtra("romextra").isEmpty()) {
                                                        setDefault();
                                                        drive.setText(_getDiskFile);
                                                    } else {
                                                        if (getIntent().getStringExtra("romextra").contains(getIntent().getStringExtra("finalromfilename"))) {
                                                            qemu.setText(getIntent().getStringExtra("romextra").replaceAll(getIntent().getStringExtra("finalromfilename"), "\"" + _getDiskFile + "\""));
                                                        } else {
                                                            drive.setText(_getDiskFile);
                                                            qemu.setText(getIntent().getStringExtra("romextra"));
                                                        }
                                                    }
                                                    icon.setText(getIntent().getStringExtra("romicon"));
                                                    if (!getIntent().getStringExtra("romicon").isEmpty()) {
                                                        File imgFile = new File(getIntent().getStringExtra("romicon"));
                                                        if (imgFile.exists()) {
                                                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                                            ivIcon.setImageBitmap(myBitmap);
                                                        }
                                                    }
                                                } else {
                                                    if (title.getText().toString().isEmpty() || title.getText().toString().equals("New VM")) {
                                                        title.setText(_filename.replace(".cvbi", ""));
                                                    }
                                                    if (qemu.getText().toString().isEmpty()) {
                                                        setDefault();
                                                    }
                                                    drive.setText(_getDiskFile);
                                                }
                                                VectrasApp.oneDialog(getResources().getString(R.string.oops), getResources().getString(R.string.error_CR_CVBI2), true, false, CustomRomActivity.this);
                                            } else {
                                                //Error code: CR_CVBI3
                                                if (getIntent().hasExtra("addromnow")) {
                                                    VectrasApp.oneDialog(getResources().getString(R.string.oops), getResources().getString(R.string.error_CR_CVBI3), false, true, CustomRomActivity.this);
                                                } else {
                                                    VectrasApp.oneDialog(getResources().getString(R.string.oops), getResources().getString(R.string.error_CR_CVBI3), true, false, CustomRomActivity.this);
                                                }
                                            }
                                            return;
                                        }

                                        JSONObject jObj = new JSONObject(FileUtils.readFromFile(MainActivity.activity, new File(AppConfig.maindirpath
                                                + "roms/" + _filename.replace(".cvbi", "") + "/rom-data.json")));

                                        title.setText(jObj.getString("title"));
                                        icon.setText(AppConfig.maindirpath
                                                + "roms/" + _filename.replace(".cvbi", "") + "/" + jObj.getString("icon"));
                                        drive.setText(AppConfig.maindirpath
                                                + "roms/" + _filename.replace(".cvbi", "") + "/" + jObj.getString("drive"));
                                        qemu.setText(jObj.getString("qemu"));
                                        ImageView ivIcon = findViewById(R.id.ivIcon);
                                        Bitmap bmImg = BitmapFactory.decodeFile(AppConfig.maindirpath
                                                + "roms/" + _filename.replace(".cvbi", "") + "/" + jObj.getString("icon"));
                                        ivIcon.setImageBitmap(bmImg);
                                        UIUtils.UIAlert(activity, getResources().getString(R.string.from) + ": " + jObj.getString("author") + "\n\n" + Html.fromHtml(jObj.getString("desc")), getResources().getString(R.string.description) + ":");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            };
                            activity.runOnUiThread(runnable);
                            try {
                                zis.close();
                            } catch (IOException e) {
                                UIUtils.toastLong(activity, e.toString());
                                throw new RuntimeException(e);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            t.start();
        } else {
            if (getIntent().hasExtra("addromnow")) {
                VectrasApp.oneDialog(getResources().getString(R.string.problem_has_been_detected), getResources().getString(R.string.format_not_supported_please_select_file_with_format_cvbi), false, true, this);
            } else {
                VectrasApp.oneDialog(getResources().getString(R.string.problem_has_been_detected), getResources().getString(R.string.format_not_supported_please_select_file_with_format_cvbi), true, false, this);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        File lol = new File(AppConfig.maindirpath + drive.getText().toString());
        try {
            lol.delete();
        } catch (Exception e) {
        }
    }
}
