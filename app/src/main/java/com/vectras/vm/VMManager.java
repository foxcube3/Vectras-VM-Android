package com.vectras.vm;

import static com.vectras.vm.VectrasApp.isFileExists;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vectras.qemu.MainSettingsManager;
import com.vectras.vm.MainRoms.AdapterMainRoms;
import com.vectras.vm.utils.UIUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VMManager {

    public static HashMap<String, Object> mapForCreateNewVM = new HashMap<>();
    public static ArrayList<HashMap<String, Object>> listmapForCreateNewVM = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> listmapForRemoveVM = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> listmapForHideVMID = new ArrayList<>();
    public static String finalJson = "";
    public static String pendingJsonContent = "";
    public static String pendingVMID = "";
    public static int pendingPosition = 0;
    public static int restoredVMs = 0;
    public static boolean isKeptSomeFiles = false;

    public static void createNewVM(String name, String thumbnail, String drive, String arch, String cdrom, String params, String vmID, int port) {
        mapForCreateNewVM.clear();
        mapForCreateNewVM.put("imgName", name);
        mapForCreateNewVM.put("imgIcon", thumbnail);
        mapForCreateNewVM.put("imgPath", drive);
        mapForCreateNewVM.put("imgCdrom", cdrom);
        mapForCreateNewVM.put("imgExtra", params);
        mapForCreateNewVM.put("imgArch", arch);
        mapForCreateNewVM.put("vmID", vmID);
        mapForCreateNewVM.put("qmpPort", port);

        listmapForCreateNewVM.clear();
        listmapForCreateNewVM = new Gson().fromJson(VectrasApp.readFile(AppConfig.romsdatajson), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());

        listmapForCreateNewVM.add(0,mapForCreateNewVM);
        finalJson = new Gson().toJson(listmapForCreateNewVM);

        VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", finalJson);
        finalJson = new Gson().toJson(mapForCreateNewVM);
        VectrasApp.writeToFile(AppConfig.maindirpath + "/roms/" + Objects.requireNonNull(mapForCreateNewVM.get("vmID")).toString(), "rom-data.json", finalJson.replace("\\u003d", "="));
        VectrasApp.writeToFile(AppConfig.maindirpath + "/roms/" + Objects.requireNonNull(mapForCreateNewVM.get("vmID")).toString(), "vmID.txt", Objects.requireNonNull(mapForCreateNewVM.get("vmID")).toString());
    }

    public static void editVM(String name, String thumbnail, String drive, String arch, String cdrom, String params, int position) {
        listmapForCreateNewVM.clear();
        listmapForCreateNewVM = new Gson().fromJson(VectrasApp.readFile(AppConfig.romsdatajson), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());

        mapForCreateNewVM.clear();
        mapForCreateNewVM.put("imgName", name);
        mapForCreateNewVM.put("imgIcon", thumbnail);
        mapForCreateNewVM.put("imgPath", drive);
        mapForCreateNewVM.put("imgCdrom", cdrom);
        mapForCreateNewVM.put("imgExtra", params);
        mapForCreateNewVM.put("imgArch", arch);
        if (listmapForCreateNewVM.get(position).containsKey("qmpPort")) {
            mapForCreateNewVM.put("qmpPort", listmapForCreateNewVM.get(position).get("qmpPort"));
        } else {
            mapForCreateNewVM.put("qmpPort", startRandomPort());
        }

        if (listmapForCreateNewVM.get(position).containsKey("vmID")) {
            mapForCreateNewVM.put("vmID", Objects.requireNonNull(listmapForCreateNewVM.get(position).get("vmID")).toString());
        } else {
            mapForCreateNewVM.put("vmID", idGenerator());
        }

        listmapForCreateNewVM.set(position,mapForCreateNewVM);
        finalJson = new Gson().toJson(listmapForCreateNewVM);
        VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", finalJson);
        finalJson = new Gson().toJson(mapForCreateNewVM);
        VectrasApp.writeToFile(AppConfig.maindirpath + "/roms/" + Objects.requireNonNull(mapForCreateNewVM.get("vmID")).toString(), "rom-data.json", finalJson.replace("\\u003d", "="));
        VectrasApp.writeToFile(AppConfig.maindirpath + "/roms/" + Objects.requireNonNull(mapForCreateNewVM.get("vmID")).toString(), "vmID.txt", Objects.requireNonNull(mapForCreateNewVM.get("vmID")).toString());
    }

    public static void deleteVMDialog(String _vmName, int _position, Activity _activity) {
        AlertDialog ad = new AlertDialog.Builder(_activity, R.style.MainDialogTheme).create();
        ad.setTitle(_activity.getString(R.string.remove)+ " " + _vmName);
        ad.setMessage(_activity.getString(R.string.are_you_sure));
        ad.setButton(Dialog.BUTTON_NEGATIVE, _activity.getString(R.string.remove) + " " + _vmName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                pendingPosition = _position;
                pendingJsonContent = VectrasApp.readFile(AppConfig.maindirpath + "roms-data.json");

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.activity, R.style.MainDialogTheme).create();
                alertDialog.setTitle(_activity.getString(R.string.keep_files_question_mark));
                alertDialog.setMessage(_activity.getString(R.string.do_you_want_to_keep_this_ROM_file_and_CD_ROM_file));
                alertDialog.setCancelable(false);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, _activity.getString(R.string.keep), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        hideVMIDWithPosition();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, _activity.getString(R.string.remove_all), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isKeptSomeFiles = false;
                        deleteVM();

                        if (isKeptSomeFiles && VectrasApp.readFile(AppConfig.maindirpath + "roms-data.json").contains("{")) {
                            VectrasApp.oneDialog(_activity.getString(R.string.keep), _activity.getString(R.string.kept_some_files), true, false, _activity);
                        }
                    }
                });
                alertDialog.show();

                MainActivity.mMainAdapter = new AdapterMainRoms(MainActivity.activity, MainActivity.data);
                MainActivity.data.remove(_position);
                MainActivity.mRVMainRoms.setAdapter(MainActivity.mMainAdapter);
                MainActivity.mRVMainRoms.setLayoutManager(new GridLayoutManager(MainActivity.activity, 2));
                MainActivity.jArray.remove(_position);
                try {
                    Writer output = null;
                    File jsonFile = new File(AppConfig.maindirpath + "roms-data" + ".json");
                    output = new BufferedWriter(new FileWriter(jsonFile));
                    output.write(MainActivity.jArray.toString());
                    output.close();
                } catch (Exception e) {
                    UIUtils.toastLong(_activity, e.toString());
                }
                UIUtils.toastLong(_activity, _vmName + _activity.getString(R.string.are_removed_successfully));
                if (!VectrasApp.readFile(AppConfig.maindirpath + "roms-data.json").contains("{")) {
                    MainActivity.mdatasize2();
                }
            }
        });
        ad.setButton(Dialog.BUTTON_POSITIVE, _activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });
        ad.show();
    }

    public static String idGenerator() {
        String _result = startRamdomVMID();

        if (isFileExists(AppConfig.maindirpath + "/roms/" + _result)) {
            _result = startRamdomVMID();
        }

        if (isFileExists(AppConfig.maindirpath + "/roms/" + _result)) {
            _result = startRamdomVMID();
        }

        return _result;
    }

    public static String startRamdomVMID() {
        String addAdb = "";
        Random random = new Random();
        int randomAbc = random.nextInt(12);
        if (randomAbc == 0) {
            addAdb = "a";
        } else if (randomAbc == 1) {
            addAdb = "b";
        } else if (randomAbc == 2) {
            addAdb = "c";
        } else if (randomAbc == 3) {
            addAdb = "d";
        } else if (randomAbc == 4) {
            addAdb = "e";
        } else if (randomAbc == 5) {
            addAdb = "f";
        } else if (randomAbc == 6) {
            addAdb = "g";
        } else if (randomAbc == 7) {
            addAdb = "h";
        } else if (randomAbc == 8) {
            addAdb = "i";
        } else if (randomAbc == 9) {
            addAdb = "j";
        } else if (randomAbc == 10) {
            addAdb = "k";
        } else if (randomAbc == 11) {
            addAdb = "l";
        }
        return addAdb + String.valueOf((long)(random.nextInt(10001)));
    }

    public static int startRandomPort() {
        Random _random = new Random();
        int _min = 10000;
        int _max = 65535;
        return _random.nextInt(_max - _min + 1) + _min;
    }

    public static void deleteVM() {
        listmapForRemoveVM.clear();
        listmapForRemoveVM = new Gson().fromJson(pendingJsonContent, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
        if (listmapForRemoveVM.get(pendingPosition).containsKey("vmID")) {
            pendingVMID = Objects.requireNonNull(listmapForRemoveVM.get(pendingPosition).get("vmID")).toString();
            Log.i("VMManager", "deleteVM: ID obtained: " + pendingVMID);
        } else {
            Log.e("VMManager", "deleteVM: Cannot get ID.");
            return;
        }
        listmapForRemoveVM.remove(pendingPosition);
        finalJson = new Gson().toJson(listmapForRemoveVM);
        if (!pendingVMID.isEmpty()) {
            int _startRepeat = 0;
            String _currentVMIDToScan = "";
            ArrayList<String> _filelist = new ArrayList<>();
            VectrasApp.listDir(AppConfig.vmFolder, _filelist);
            if (!_filelist.isEmpty()) {
                for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                    if (_startRepeat < _filelist.size()) {
                        if (isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.txt")) {
                            _currentVMIDToScan = VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/vmID.txt").replace("\n", "");
                            if (!_currentVMIDToScan.isEmpty()) {
                                if (_currentVMIDToScan.equals(pendingVMID)) {
                                    if (!finalJson.contains(_filelist.get((int)(_startRepeat)))) {
                                        VectrasApp.deleteDirectory(_filelist.get((int)(_startRepeat)));
                                    } else {
                                        isKeptSomeFiles = true;
                                        hideVMID(pendingVMID);
                                    }
                                }
                            }
                        }
                    }
                    _startRepeat++;
                }
            }
        }
    }

    public static void hideVMID(String _vmID) {
        if (!_vmID.isEmpty()) {
            int _startRepeat = 0;
            String _currentVMIDToScan = "";
            ArrayList<String> _filelist = new ArrayList<>();
            VectrasApp.listDir(AppConfig.vmFolder, _filelist);
            if (!_filelist.isEmpty()) {
                for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                    if (_startRepeat < _filelist.size()) {
                        if (isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.txt")) {
                            _currentVMIDToScan = VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/vmID.txt").replace("\n", "");
                            if (!_currentVMIDToScan.isEmpty()) {
                                if (_currentVMIDToScan.equals(_vmID)) {
                                    VectrasApp.moveAFile(_filelist.get((int)(_startRepeat)) + "/vmID.txt", _filelist.get((int)(_startRepeat)) + "/vmID.old.txt");
                                }
                            }
                        }
                    }
                    _startRepeat++;
                }
            }
        }
    }

    public static void hideVMIDWithPosition() {
        listmapForHideVMID.clear();
        listmapForHideVMID = new Gson().fromJson(pendingJsonContent, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
        if (listmapForHideVMID.get(pendingPosition).containsKey("vmID")) {
            pendingVMID = Objects.requireNonNull(listmapForHideVMID.get(pendingPosition).get("vmID")).toString();
        } else {
            return;
        }
        if (!pendingVMID.isEmpty()) {
            int _startRepeat = 0;
            String _currentVMIDToScan = "";
            ArrayList<String> _filelist = new ArrayList<>();
            VectrasApp.listDir(AppConfig.vmFolder, _filelist);
            if (!_filelist.isEmpty()) {
                for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                    if (_startRepeat < _filelist.size()) {
                        if (isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.txt")) {
                            _currentVMIDToScan = VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/vmID.txt").replace("\n", "");
                            if (!_currentVMIDToScan.isEmpty()) {
                                if (_currentVMIDToScan.equals(pendingVMID)) {
                                    VectrasApp.moveAFile(_filelist.get((int)(_startRepeat)) + "/vmID.txt", _filelist.get((int)(_startRepeat)) + "/vmID.old.txt");
                                }
                            }
                        }
                    }
                    _startRepeat++;
                }
            }
        }
    }

    public static void cleanUp() {
        finalJson = VectrasApp.readFile(AppConfig.romsdatajson);
        if (!finalJson.isEmpty()) {
            int _startRepeat = 0;
            ArrayList<String> _filelist = new ArrayList<>();
            VectrasApp.listDir(AppConfig.vmFolder, _filelist);
            if (!_filelist.isEmpty()) {
                for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                    if (_startRepeat < _filelist.size()) {
                        if (!isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.txt")) {
                            if (!finalJson.contains(_filelist.get((int) (_startRepeat)))) {
                                VectrasApp.deleteDirectory(_filelist.get((int) (_startRepeat)));
                            }
                        }
                    }
                    _startRepeat++;
                }
            }
        }
    }

    public static void restoreVMs() {
        int _startRepeat = 0;
        String _resulttemp ="";
        String _result ="";
        restoredVMs = 0;
        ArrayList<String> _filelist = new ArrayList<>();
        VectrasApp.listDir(AppConfig.vmFolder, _filelist);
        if (!_filelist.isEmpty()) {
            for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                if (_startRepeat < _filelist.size()) {
                    if (!isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.txt")) {
                        if (isFileExists(_filelist.get((int)(_startRepeat)) + "/rom-data.json")) {
                            if (VectrasApp.checkJSONMapIsNormalFromString(VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json"))) {
                                if (_resulttemp.contains("}")) {
                                    _resulttemp += "," + VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                } else {
                                    _resulttemp = VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                }
                                if (VectrasApp.checkJSONIsNormalFromString(VectrasApp.readFile(AppConfig.maindirpath + "/roms-data.json").replaceAll("]", _resulttemp + "]"))) {
                                    if (_result.contains("}")) {
                                        _result += "," + VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                    } else {
                                        _result = VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                    }
                                    if (isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.old.txt")) {
                                        enableVMID(VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/vmID.old.txt"));
                                    } else {
                                        VectrasApp.writeToFile(_filelist.get((int)(_startRepeat)), "/vmID.txt", VMManager.idGenerator());
                                    }
                                    restoredVMs++;
                                } else if (VectrasApp.checkJSONIsNormalFromString(VectrasApp.readFile(AppConfig.maindirpath + "/roms-data.json").replaceAll("]", "," + _resulttemp + "]"))) {
                                    if (_result.contains("}")) {
                                        _result += "," + VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                    } else {
                                        _result = "," + VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                    }
                                    if (isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.old.txt")) {
                                        enableVMID(VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/vmID.old.txt"));
                                    } else {
                                        VectrasApp.writeToFile(_filelist.get((int)(_startRepeat)), "/vmID.txt", VMManager.idGenerator());
                                    }
                                    restoredVMs++;
                                } else {
                                    Log.i("CqcmActivity", VectrasApp.readFile(AppConfig.maindirpath + "/roms-data.json").replaceAll("]", _resulttemp + "]"));
                                }
                            }
                        }
                    }

                    _startRepeat++;
                    if (_startRepeat == _filelist.size()) {
                        if (!_result.isEmpty()) {
                            if (VectrasApp.checkJSONIsNormalFromString("[" + _result + "]")) {
                                if (isFileExists(AppConfig.romsdatajson)) {
                                    if (VectrasApp.checkJSONIsNormal(AppConfig.romsdatajson)) {
                                        String _JSONcontent = VectrasApp.readFile(AppConfig.romsdatajson);
                                        String _JSONcontentnew = _JSONcontent.replaceAll("]", _result + "]");
                                        if (VectrasApp.checkJSONIsNormalFromString(_JSONcontentnew)) {
                                            VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", _JSONcontentnew);
                                        } else {
                                            restoredVMs = 0;
                                        }
                                    } else {
                                        restoredVMs = 0;
                                    }
                                } else {
                                    VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", "[" + _result + "]");
                                }
                            } else {
                                restoredVMs = 0;
                            }
                        } else {
                            restoredVMs = 0;
                        }
                    }
                }
            }

        }
    }

    public static void startFixRomsDataJson() {
        int _startRepeat = 0;
        String _resulttemp ="";
        String _result ="";
        restoredVMs = 0;
        ArrayList<String> _filelist = new ArrayList<>();
        VectrasApp.listDir(AppConfig.vmFolder, _filelist);
        if (!_filelist.isEmpty()) {
            for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                if (_startRepeat < _filelist.size()) {
                    if (isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.txt")) {
                        if (isFileExists(_filelist.get((int)(_startRepeat)) + "/rom-data.json")) {
                            if (VectrasApp.checkJSONMapIsNormalFromString(VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json"))) {
                                if (_resulttemp.contains("}")) {
                                    _resulttemp += "," + VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                } else {
                                    _resulttemp = VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                }
                                if (VectrasApp.checkJSONIsNormalFromString(VectrasApp.readFile(AppConfig.maindirpath + "/roms-data.json").replaceAll("]", _resulttemp + "]"))) {
                                    if (_result.contains("}")) {
                                        _result += "," + VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                    } else {
                                        _result = VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                    }
                                    restoredVMs++;
                                } else if (VectrasApp.checkJSONIsNormalFromString(VectrasApp.readFile(AppConfig.maindirpath + "/roms-data.json").replaceAll("]", "," + _resulttemp + "]"))) {
                                    if (_result.contains("}")) {
                                        _result += "," + VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                    } else {
                                        _result = "," + VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/rom-data.json");
                                    }
                                    restoredVMs++;
                                } else {
                                    Log.i("CqcmActivity", VectrasApp.readFile(AppConfig.maindirpath + "/roms-data.json").replaceAll("]", _resulttemp + "]"));
                                }
                            }
                        }
                    }

                    _startRepeat++;
                    if (_startRepeat == _filelist.size()) {
                        if (!_result.isEmpty()) {
                            if (VectrasApp.checkJSONIsNormalFromString("[" + _result + "]")) {
                                if (isFileExists(AppConfig.romsdatajson)) {
                                    if (VectrasApp.checkJSONIsNormal(AppConfig.romsdatajson)) {
                                        String _JSONcontent = VectrasApp.readFile(AppConfig.romsdatajson);
                                        String _JSONcontentnew = _JSONcontent.replaceAll("]", _result + "]");
                                        if (VectrasApp.checkJSONIsNormalFromString(_JSONcontentnew)) {
                                            VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", _JSONcontentnew);
                                        } else {
                                            restoredVMs = 0;
                                        }
                                    } else {
                                        restoredVMs = 0;
                                    }
                                } else {
                                    VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", "[" + _result + "]");
                                }
                            } else {
                                restoredVMs = 0;
                            }
                        } else {
                            restoredVMs = 0;
                        }
                    }
                }
            }

        }
    }

    public static void enableVMID(String _vmID) {
        if (_vmID.isEmpty())
            return;
        int _startRepeat = 0;
        ArrayList<String> _filelist = new ArrayList<>();
        VectrasApp.listDir(AppConfig.vmFolder, _filelist);
        if (!_filelist.isEmpty()) {
            for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                if (_startRepeat < _filelist.size()) {
                    if (isFileExists(_filelist.get((int)(_startRepeat)) + "/vmID.old.txt")) {
                        if (VectrasApp.readFile(_filelist.get((int)(_startRepeat)) + "/vmID.old.txt").equals(_vmID)) {
                            VectrasApp.moveAFile(_filelist.get((int)(_startRepeat)) + "/vmID.old.txt", _filelist.get((int)(_startRepeat)) + "/vmID.txt");
                        }
                    }
                }
                _startRepeat++;
            }
        }
    }

    public static void movetoRecycleBin() {
        File vDir = new File(AppConfig.recyclebin);
        if (!vDir.exists()) {
            if (!vDir.mkdirs()) {
                return;
            }
        }
        finalJson = VectrasApp.readFile(AppConfig.romsdatajson);
        if (!finalJson.isEmpty()) {
            int _startRepeat = 0;
            ArrayList<String> _filelist = new ArrayList<>();
            VectrasApp.listDir(AppConfig.vmFolder, _filelist);
            if (!_filelist.isEmpty()) {
                for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                    if (_startRepeat < _filelist.size()) {
                        if (!finalJson.contains(Uri.parse(_filelist.get((int) (_startRepeat))).getLastPathSegment())) {
                            VectrasApp.moveAFile(_filelist.get((int) (_startRepeat)), AppConfig.recyclebin + Uri.parse(_filelist.get((int) (_startRepeat))).getLastPathSegment());
                        }
                    }
                    _startRepeat++;
                }
            }
        }
    }

    public static String quickScanDiskFileInFolder(String _foderpath) {
        if (!_foderpath.isEmpty()) {
            int _startRepeat = 0;
            ArrayList<String> _filelist = new ArrayList<>();
            VectrasApp.listDir(_foderpath, _filelist);
            if (!_filelist.isEmpty()) {
                for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                    if (_startRepeat < _filelist.size()) {
                        if (isADiskFile(_filelist.get((int)(_startRepeat)))) {
                            return _filelist.get((int)(_startRepeat));
                        }
                    }
                    _startRepeat++;
                }
            }
        }
        return "";
    }

    public static boolean isADiskFile (String _filepath) {
        if (_filepath.contains(".")) {
            String _getFileName = Objects.requireNonNull(Uri.parse(_filepath).getLastPathSegment()).toLowerCase();
            String _getFileFormat = _getFileName.substring((int)(_getFileName.lastIndexOf(".") + 1), (int)(_getFileName.length()));
            if ("qcow2,img,vhd,vhdx,vdi,qcow,vmdk,vpc".contains(_getFileFormat)){
                return true;
            }
        }
        return false;
    }

    public static String quickScanISOFileInFolder(String _foderpath) {
        if (!_foderpath.isEmpty()) {
            int _startRepeat = 0;
            ArrayList<String> _filelist = new ArrayList<>();
            VectrasApp.listDir(_foderpath, _filelist);
            if (!_filelist.isEmpty()) {
                for (int _repeat = 0; _repeat < (int)(_filelist.size()); _repeat++) {
                    if (_startRepeat < _filelist.size()) {
                        if (isAISOFile(_filelist.get((int)(_startRepeat)))) {
                            return _filelist.get((int)(_startRepeat));
                        }
                    }
                    _startRepeat++;
                }
            }
        }
        return "";
    }

    public static boolean isAISOFile (String _filepath) {
        if (_filepath.contains(".")) {
            String _getFileName = Objects.requireNonNull(Uri.parse(_filepath).getLastPathSegment()).toLowerCase();
            String _getFileFormat = _getFileName.substring((int)(_getFileName.lastIndexOf(".") + 1), (int)(_getFileName.length()));
            if ("iso".contains(_getFileFormat)){
                return true;
            }
        }
        return false;
    }

    public static void setArch(String _arch, Activity _activity) {
        switch (_arch) {
            case "I386":
                MainSettingsManager.setArch(_activity, "I386");
                break;
            case "ARM64":
                MainSettingsManager.setArch(_activity, "ARM64");
                break;
            case "PPC":
                MainSettingsManager.setArch(_activity, "PPC");
                break;
            default:
                MainSettingsManager.setArch(_activity, "X86_64");
                break;
        }
    }

    public static boolean isExecutedCommandError(String _command, String _result, Activity _activity) {
        if (!_command.contains("qemu-system"))
            return false;
        if (_command.contains("qemu-system") && _result.contains("Killed"))
            return true;
        //Error code: PROOT_IS_MISSING_0
        if (_result.contains("proot\": error=2,")) {
            AlertDialog alertDialog = new AlertDialog.Builder(_activity, R.style.MainDialogTheme).create();
            alertDialog.setTitle(_activity.getResources().getString(R.string.problem_has_been_detected));
            alertDialog.setMessage(_activity.getResources().getString(R.string.error_PROOT_IS_MISSING_0));
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, _activity.getResources().getString(R.string.continuetext), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.isActivate = false;
                    VectrasApp.deleteDirectory(_activity.getFilesDir().getAbsolutePath() + "/data");
                    VectrasApp.deleteDirectory(_activity.getFilesDir().getAbsolutePath() + "/distro");
                    VectrasApp.deleteDirectory(_activity.getFilesDir().getAbsolutePath() + "/usr");
                    Intent intent = new Intent();
                    intent.setClass(_activity, SplashActivity.class);
                    _activity.startActivity(intent);
                    _activity.finish();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, _activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
            return true;
        } else if (_result.contains(") exists") && _result.contains("drive with bus")) {
            //Error code: DRIVE_INDEX_0_EXISTS
            VectrasApp.oneDialog(_activity.getResources().getString(R.string.problem_has_been_detected), _activity.getResources().getString(R.string.error_DRIVE_INDEX_0_EXISTS), true, false, _activity);
            return true;
        } else if (_result.contains("gtk initialization failed") || _result.contains("x11 not available")) {
            //Error code: X11_NOT_AVAILABLE
            AlertDialog alertDialog = new AlertDialog.Builder(_activity, R.style.MainDialogTheme).create();
            alertDialog.setTitle(_activity.getResources().getString(R.string.problem_has_been_detected));
            alertDialog.setMessage(_activity.getResources().getString(R.string.error_X11_NOT_AVAILABLE));
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, _activity.getResources().getString(R.string.continuetext), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainSettingsManager.setVmUi(_activity, "VNC");
                    VectrasApp.oneDialog(_activity.getResources().getString(R.string.done), _activity.getResources().getString(R.string.switched_to_VNC), true, false, _activity);
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, _activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
            return true;
        } else if (_result.contains("No such file or directory1")) {
            //Error code: NO_SUCH_FILE_OR_DIRECTORY
            VectrasApp.oneDialog(_activity.getResources().getString(R.string.problem_has_been_detected), _activity.getResources().getString(R.string.error_NO_SUCH_FILE_OR_DIRECTORY), true, false, _activity);
            _activity.stopService(new Intent(_activity, MainService.class));
            return true;
        } else {
            return false;
        }
    }

    public static boolean isRomsDataJsonNormal(Boolean _needfix, Context _context) {
        if (isFileExists(AppConfig.romsdatajson)) {
            if (!VectrasApp.checkJSONIsNormal(AppConfig.romsdatajson)) {
                if (_needfix) {
                    AlertDialog alertDialog = new AlertDialog.Builder(_context, R.style.MainDialogTheme).create();
                    alertDialog.setTitle(_context.getResources().getString(R.string.oops));
                    alertDialog.setMessage(_context.getResources().getString(R.string.need_fix_json_before_create));
                    alertDialog.setCancelable(true);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, _context.getResources().getString(R.string.continuetext), (dialog, which) -> {
                        VectrasApp.moveAFile(AppConfig.maindirpath + "roms-data.json", AppConfig.maindirpath + "roms-data.old.json");
                        VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", "[]");
                        startFixRomsDataJson();
                        fixRomsDataJsonResult(_context);
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, _context.getResources().getString(R.string.cancel), (dialog, which) -> {

                    });
                    alertDialog.show();

                }
                return false;
            } else {
                return true;
            }
        } else {
            VectrasApp.writeToFile(AppConfig.maindirpath, "roms-data.json", "[]");
            return true;
        }
    }

    public static void fixRomsDataJsonResult(Context _context) {
        if (restoredVMs == 0) {
            VectrasApp.oneDialogWithContext(_context.getString(R.string.done), _context.getString(R.string.roms_data_json_fixed_unsuccessfully),true, _context);
        } else {
            VectrasApp.oneDialogWithContext(_context.getString(R.string.done), _context.getString(R.string.roms_data_json_fixed_successfully),true, _context);
        }
        MainActivity.loadDataVbi();
        MainActivity.mdatasize2();
        movetoRecycleBin();
    }

    public static boolean isthiscommandsafe(String _command) {
        if (_command.startsWith("qemu")) {
            if (!_command.contains("&")) {
                if (!_command.contains("\n")) {
                    if (!_command.contains(";")) {
                        if (!_command.contains("|")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isthiscommandsafeimg(String _command) {
        if (!_command.contains("qcow2")) {
            String _getsize = _command.substring(_command.lastIndexOf(" ") + 1);
            if (_getsize.toLowerCase().endsWith("t") || _getsize.toLowerCase().endsWith("p")  || _getsize.toLowerCase().endsWith("e")) {
                return false;
            }
            if (_getsize.toLowerCase().endsWith("g")) {
                return _getsize.length() <= 2;
            }
            if (_getsize.toLowerCase().endsWith("m")) {
                return _getsize.length() <= 4;
            }
            if (_getsize.toLowerCase().endsWith("k")) {
                return _getsize.length() <= 8;
            }
        }
        return true;
    }
}
